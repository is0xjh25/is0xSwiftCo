import org.json.JSONObject;
import java.io.*;
import java.net.Socket;
import java.util.regex.Pattern;

public class ServerProcessor implements Runnable {
    private final ServerManager serverManager;
    private final Socket socket;
    private boolean participating;
    private Room currentStay;
    private User user;

    public ServerProcessor(ServerManager serverManager, Socket userSocket) {
        this.serverManager = serverManager;
        this.socket = userSocket;
        participating = false;
    }

    @Override
    public void run() {
        try {
            String ip = socket.getRemoteSocketAddress().toString();
            System.out.println("[JOIN] " + ip);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String line;
            try {
                while ((line = in.readLine()) != null) {
                    try {
                        JSONObject jsonOut = requestHandler(new JSONObject(line));
                        out.write(jsonOut.toString() + "\n");
                        out.flush();
                    } catch (IOException e) {
                        System.out.println("[ERROR] " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private JSONObject requestHandler(JSONObject req) {
        JSONObject res = new JSONObject();
        // join/create stage
        if (!participating && req.getString("header").equals("create")) {
            String roomID = req.getString("roomID");
            String username = req.getString("username");
            String connectResult = connectCheck(roomID, username, true);
            res.put("header", "create");
            if (connectResult.equals("ok")) {
                setUser(username, true);
                Room createdRoom = serverManager.createRoom(roomID, user);
                if (createdRoom != null) {
                    res.put("ok", "true");
                    res.put("roomID", roomID);
                    res.put("message", "Room has be created");
                    res.put("content", createdRoom.getEncodeWhitBoard());
                    currentStay = createdRoom;
                    serverManager.updateUserList(createdRoom);
                } else {
                    unsetUser();
                    res.put("ok", "error");
                    res.put("message", "Unable to create a new room. Please try again.");
                }
            } else {
                res.put("ok", "false");
                res.put("message", connectResult);
            }
        } else if (!participating && req.getString("header").equals("join")) {
            String roomID = req.getString("roomID");
            String username = req.getString("username");
            String connectResult = connectCheck(roomID, username, false);
            res.put("header", "join");
            if (connectResult.equals("ok")) {
                // forward request
                setUser(username, false);
                if (serverManager.forwardJoinRequest(roomID, user)) {
                    res.put("ok", "pending");
                    res.put("message", "The join request has been sent.");
                } else {
                    unsetUser();
                    res.put("ok", "error");
                    res.put("message", "Unable to join the room. Please try again.");
                }
            } else {
                res.put("ok", "false");
                res.put("message", connectResult);
            }
        } else if (!participating) {
            res.put("header", "error");
            res.put("message", "Unexpected error occurs. Please report the issue. [participating]");
        } else if (req.getString("header").equals("join-request")) {
            // join request for the manager
            if (user.isManager()) {
                serverManager.handleJoinRequest(req);
            } else {
                res.put("header", "error");
                res.put("message", "Unexpected error occurs. Please report the issue. [join-request]");
            }
        } else if (req.getString("header").equals("update-whiteboard")) {
            if (req.has("content")) {
                serverManager.broadcastWhiteBoard(currentStay, user, req);
            } else {
                res.put("header", "error");
                res.put("message", "Unexpected error occurs. Please report the issue. [update-whiteboard]");
            }
        } else if (req.getString("header").equals("update-chat")) {
            if (req.has("content")) {
                serverManager.broadcastChat(currentStay, user, req);
            } else {
                res.put("header", "error");
                res.put("message", "Unexpected error occurs. Please report the issue. [update-chat]");
            }
        } else if (req.getString("header").equals("user-quit")) {
            serverManager.userLeft(currentStay, user);
        } else {
            res.put("header", "error");
            res.put("message", "Unexpected error occurs. Please report the issue. [unknown-request]");
        }

        return res;
    }

    private void setUser(String username, boolean isManager) {
        participating = true;
        user = new User(username, isManager, socket);
    }

    private void unsetUser() {
        participating = false;
        user = null;
        currentStay = null;
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
