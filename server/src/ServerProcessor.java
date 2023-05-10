import org.json.JSONObject;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;

public class ServerProcessor implements Runnable  {
    private final ServerManager serverManager;
    private final Socket socket;
    private String roomID;
    private String username;
    private boolean isManager;
    private boolean participating;
    private User user;

    public ServerProcessor(ServerManager serverManager, Socket userSocket) {
        this.serverManager = serverManager;
        this.socket = userSocket;
        participating = false;
    }

    @Override
    public void run () {
        try {
            String ip = socket.getRemoteSocketAddress().toString();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String line;
            try {
                while ((line = in.readLine()) != null) {
                    JSONObject jsonIn = new JSONObject(line);
                    JSONObject jsonOut = new JSONObject();
                    String reply = "";
                    if (jsonIn.getString("header").equals("connect") && !participating) {
                        String roomID = jsonIn.getString("roomID");
                        String username = jsonIn.getString("username");
                        String type = jsonIn.getString("type");
                        reply = connectCheck(roomID, username, type);
                        boolean isManager = type.equals("create");

                        jsonOut.put("header", "connect-r");
                        if (reply.equals("ok")) {
                            user = new User(username, true, socket);
                            jsonOut.put("ok", "true");
                            if (isManager) {
                                serverManager.getRoomList().put(Integer.parseInt(roomID), new Room(roomID, user));
                            } else {
                                serverManager.getRoomList().get(Integer.parseInt(roomID)).addUser(user);
                            }
                            participating = true;
                        } else if (reply.equals("Room ID is already taken.") || reply.equals("Room ID is not found.") || reply.equals("Username is already taken.")) {
                            jsonOut.put("ok", "false");
                            jsonOut.put("message", reply);
                        } else {
                            jsonOut.put("ok", "error");
                            jsonOut.put("message", reply);
                        }
                    } else if (jsonIn.getString("header").equals("update-whiteboard") && participating) {

                    } else if (jsonIn.getString("header").equals("update-chat") && participating) {

                    } else if (jsonIn.getString("header").equals("leave") && participating) {

                    }

                    try {
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

    /* HELPER FUNCTIONS */
    private String connectCheck(String roomID, String username, String type) {
        if (!validRoomID(roomID)) {
            return "Invalid room ID.";
        } else if (!validUsername(username)) {
            return "Invalid room Username.";
        }

        if (!type.equals("create") && !type.equals("join")) return "Unexpected user type.";

        if (type.equals("create") && serverManager.getRoomList().containsKey(Integer.parseInt(roomID))) {
            return "Room ID is already taken.";
        }

        if (type.equals("join")) {
            if (!serverManager.getRoomList().containsKey(Integer.parseInt(roomID))) return "Room ID is not found.";
            if (serverManager.getRoomList().get(Integer.parseInt(roomID)).isUserExist(username)) return "Username is already taken.";
        }

        return "ok";
    }

    private boolean validRoomID(String roomID) {
        Pattern roomIDPattern = Pattern.compile("^[0-9]{4}$");
        return roomIDPattern.matcher(roomID).matches();
    }

    private boolean validUsername(String username) {
        Pattern usernamePattern = Pattern.compile("^[a-zA-z0-9]{1,15}$");
        return usernamePattern.matcher(username).matches();
    }
}
