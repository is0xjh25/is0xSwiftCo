import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ServerProcessor implements Runnable {
    private final ServerManager serverManager;
    private final Socket socket;
    private String username;
    private Room currentStay;
    private boolean participating;
    private boolean isManager;

    public ServerProcessor(ServerManager serverManager, Socket userSocket) {
        this.serverManager = serverManager;
        this.socket = userSocket;
        username = "";
        participating = false;
        isManager = false;
    }

    @Override
    public void run() {
        try {
            System.out.println("[JOIN] " + socket.getRemoteSocketAddress().toString());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            try {
                while ((line = in.readLine()) != null) {
                    requestHandler(new JSONObject(line));
                }
                if (currentStay != null) serverManager.userLeft(currentStay, this);
                System.out.println("[LEFT] " + socket.getRemoteSocketAddress().toString());
            } catch (IOException e) {
                System.out.println("[ERROR:run] " + e.getMessage() + ".");
            }
        } catch (IOException e) {
            System.out.println("[ERROR:run] " + e.getMessage() + ".");
        }
    }

    private void requestHandler(JSONObject req) {
        JSONObject res = new JSONObject();
        System.out.println(req);
        // join/create stage
        if (!participating && req.getString("header").equals("create")) {
            String roomID = req.getString("roomID");
            String username = req.getString("username");
            String connectResult = connectCheck(roomID, username, true);
            res.put("header", "create");
            if (connectResult.equals("ok")) {
                setUsername(username);
                Room createdRoom = serverManager.createRoom(roomID, this);
                if (createdRoom != null) {
                    res.put("ok", "true");
                    res.put("message", "Room has been created.");
                    res.put("manager", username);
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(username);
                    res.put("user-list", new JSONArray(temp));
                    res.put("content", createdRoom.getEncodeWhitBoard());
                    setParticipating(true);
                    setManager(true);
                    currentStay = createdRoom;
                } else {
                    res.put("ok", "error");
                    res.put("message", "Unable to create a new room.");
                    resetUser();
                }
            } else {
                res.put("ok", "false");
                res.put("message", connectResult);
                resetUser();
            }
        } else if (!participating && req.getString("header").equals("join")) {
            String roomID = req.getString("roomID");
            String username = req.getString("username");
            String connectResult = connectCheck(roomID, username, false);
            res.put("header", "join");
            if (connectResult.equals("ok")) {
                setCurrentStay(serverManager.getRoomList().get(roomID));
                setUsername(username);
                setManager(false);
                setParticipating(false);
                // forward request
                if (serverManager.forwardJoinRequest(roomID, this)) {
                    res.put("ok", "pending");
                    res.put("message", "The join request has been sent.");
                } else {
                    res.put("ok", "error");
                    res.put("message", "Unable to join the room.");
                    resetUser();
                }
            } else {
                res.put("ok", "false");
                res.put("message", connectResult);
                resetUser();
            }
        } else if (!participating) {
            res.put("header", "error");
            res.put("message", "Please report the issue. [participating]");
            resetUser();
        } else if (req.getString("header").equals("join-request")) {
            // join request for the manager
            if (isManager()) {
                serverManager.handleJoinResponse(req);
                return;
            } else {
                res.put("header", "error");
                res.put("message", "Please report the issue. [join-request]");
                resetUser();
            }
        } else if (req.getString("header").equals("update-whiteboard")) {
            if (req.has("content")) {
                currentStay.setEncodeWhitBoard(req);
                serverManager.broadcastWhiteBoard(currentStay, this, req);
                return;
            } else {
                res.put("header", "error");
                res.put("message", "Please report the issue. [update-whiteboard]");
                resetUser();
            }
        } else if (req.getString("header").equals("update-chat")) {
            if (req.has("content")) {
                serverManager.broadcastChat(currentStay, this, req);
                return;
            } else {
                res.put("header", "error");
                res.put("message", "Please report the issue. [update-chat]");
                resetUser();
            }
        } else if (req.getString("header").equals("user-quit")) {
            res.put("header", "user-quit");
            serverManager.userLeft(currentStay, this);
        } else if (req.getString("header").equals("kick-out")) {
            if (isManager && req.has("username")) {
                serverManager.kickOut(currentStay, req.getString("username"));
                return;
            } else {
                res.put("header", "error");
                res.put("message", "Please report the issue. [kick-out]");
                resetUser();
            }
        } else {
            res.put("header", "error");
            res.put("message", "Please report the issue. [unknown-request]");
            resetUser();
        }

        // add user current stat
        res = setStat(res);
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.write(res.toString() + "\n");
            out.flush();
        } catch (IOException e) {
            System.out.println("[ERROR:requestHandler] " + e.getMessage() + ".");
        }
    }

    public JSONObject setStat(JSONObject res) {
        if (currentStay != null) {
            res.put("roomID", currentStay.getRoomID());
        } else {
            res.put("roomID", "");
        }
        res.put("username", username);
        res.put("isManager", isManager);
        res.put("participating", participating);
        return res;
    }

    public void resetUser() {
        // make sure user is removed from the room
        if (currentStay != null) {
            if (currentStay.getUserList().containsKey(username)) currentStay.getUserList().remove(username);
        }
        currentStay = null;
        username = "";
        participating = false;
        isManager = false;
    }

    private String connectCheck(String roomID, String username, boolean isManager) {
        if (!validRoomID(roomID)) {
            return "Invalid room ID.";
        } else if (!validUsername(username)) {
            return "Invalid room Username.";
        }

        if (isManager && serverManager.getRoomList().containsKey(roomID)) return "Room ID is already taken.";

        if (!isManager) {
            if (!serverManager.getRoomList().containsKey(roomID)) return "Room ID is not found.";
            if (serverManager.getRoomList().get(roomID).isUserExist(username)) return "Username is already taken.";
        }

        return "ok";
    }

    /* GETTERS & SETTERS */
    public Socket getSocket() {
        return socket;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Room getCurrentStay() {
        return currentStay;
    }
    public void setCurrentStay(Room currentStay) {
        this.currentStay = currentStay;
    }
    public boolean isParticipating() {
        return participating;
    }
    public void setParticipating(boolean participating) {
        this.participating = participating;
    }
    public boolean isManager() {
        return isManager;
    }
    public void setManager(boolean manager) {
        isManager = manager;
    }

    /* HELPER FUNCTIONS */
    private boolean validRoomID(String roomID) {
        Pattern roomIDPattern = Pattern.compile("^[0-9]{4}$");
        return roomIDPattern.matcher(roomID).matches();
    }

    private boolean validUsername(String username) {
        Pattern usernamePattern = Pattern.compile("^[a-zA-z0-9]{1,15}$");
        return usernamePattern.matcher(username).matches();
    }
}
