import org.json.JSONArray;
import org.json.JSONObject;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServerManager extends Thread {
    private int serverPort;
    private ServerSocket serverSocket;
    private final HashMap<String, Room> roomList;

    public ServerManager(String[] args) {
        roomList = new HashMap<>();
        ServerArgs serverArgs = new ServerArgs();
        serverPort = 0;
        CmdLineParser parser = new CmdLineParser(serverArgs);

        // parse parameters.
        try {
            parser.parseArgument(args);
            serverPort = serverArgs.getPort();
        } catch (CmdLineException e) {
            System.out.println("[ERROR:ServerManager] " + e.getMessage());
            parser.printUsage(System.out);
            System.exit(0);
        }
    }

    public void run() {
        try {
            ServerSocket listeningSocket = new ServerSocket(serverPort);
            listeningSocket.setReuseAddress(true);
            System.out.println("[SUCCEED] SwiftCo server has been initiated.");
            while (true) {
                Socket userSocket = listeningSocket.accept();
                ServerProcessor serverProcessor  = new ServerProcessor(this, userSocket);
                new Thread(serverProcessor).start();
            }
        } catch (IOException e) {
            System.out.println("[ERROR:run] " + e.getMessage());
        }
    }

    synchronized public Room createRoom(String roomID, User manager) {
        if (roomList.containsKey(roomID)) {
            return null;
        } else {
            Room newRoom = new Room(roomID, manager);
            roomList.put(roomID, newRoom);
            return newRoom;
        }
    }

    synchronized public void updateUserList(Room room) {
        List<String> userArray = new ArrayList<>();
        // get all the names.
        for (User u : room.getUserList()) {
            userArray.add(u.getUsername());
        }

        // send the latest user list to everyone in the room
        for (User u : room.getUserList()) {
            JSONObject jsonOut = new JSONObject();
            jsonOut.put("header", "update-participants");
            jsonOut.put("user-list", new JSONArray(userArray));
            jsonOut.put("manager", room.getManager().getUsername());
            try {
                OutputStreamWriter writer = new OutputStreamWriter(u.getSocket().getOutputStream(), "UTF-8");
                writer.write(jsonOut.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:updateUserList] " + e.getMessage());
            }
        }
    }

    synchronized public boolean forwardJoinRequest(String roomID, User user) {
        JSONObject req = new JSONObject();
        if (roomList.containsKey(roomID)) {
            req.put("header", "join-request");
            req.put("roomID", roomID);
            req.put("username", user.getUsername());
            if (roomList.get(roomID).getUserList().contains(user)) return false;
            try {
                OutputStreamWriter writer = new OutputStreamWriter(roomList.get(roomID).getManager().getSocket().getOutputStream(), "UTF-8");
                writer.write(req.toString() + "\n");
                writer.flush();
                roomList.get(roomID).addUser(user);
                return true;
            } catch (IOException e) {
                System.out.println("[ERROR:forwardJoinRequest] " + e.getMessage());
                return false;
            }
        } else {
            return false;
        }
    }

    synchronized public void handleJoinRequest(JSONObject req) {
        JSONObject res = new JSONObject();
        String roomID = req.getString("roomID");
        String username = req.getString("username");
        Room joinRoom = roomList.get(roomID);
        User user = joinRoom.findUser(username);
        res.put("header", "join-request");
        if (req.getString("permit").equals("true")) {
            res.put("permit", "true");
            try {
                OutputStreamWriter writer = new OutputStreamWriter(user.getSocket().getOutputStream(), "UTF-8");
                writer.write(res.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:handleJoinRequest] " + e.getMessage());
            }
        } else if (req.getString("permit").equals("false")) {
            res.put("permit", "false");
            try {
                OutputStreamWriter writer = new OutputStreamWriter(user.getSocket().getOutputStream(), "UTF-8");
                writer.write(res.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:handleJoinRequest] " + e.getMessage());
            }
        }
    }

    public void userLeft(Room room, User user) {
        if (user.isManager()) {
            roomClosed(room);
        } else {
            boolean remove = room.getUserList().remove(user);
            if (remove) updateUserList(room);
        }
    }

    synchronized public void broadcastWhiteBoard(Room room, User user, JSONObject whiteboard) {
        room.setEncodeWhitBoard(whiteboard);
        for (User u: room.getUserList()) {
            if (user.getUsername().equals(u.getUsername())) continue;
            try {
                OutputStreamWriter writer = new OutputStreamWriter(u.getSocket().getOutputStream(), "UTF-8");
                writer.write(whiteboard.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:broadcastWhiteBoard] " + e.getMessage());
            }
        }
    }

    public void broadcastChat(Room room, User user, JSONObject chat) {
        if (chat.getString("content").length() > 0) {
            for (User u: room.getUserList()) {
                if (user.getUsername().equals(u.getUsername())) continue;
                try {
                    OutputStreamWriter writer = new OutputStreamWriter(u.getSocket().getOutputStream(), "UTF-8");
                    writer.write(chat + "\n");
                    writer.flush();
                } catch (IOException e) {
                    System.out.println("[ERROR:broadcastChat] " + e.getMessage());
                }
            }
        }
    }

    public void roomClosed(Room room) {
        for (User u : room.getUserList()) {
            JSONObject jsonOut = new JSONObject();
            jsonOut.put("header", "room-closed");
            jsonOut.put("message", "The manager has left.");
            try {
                OutputStreamWriter writer = new OutputStreamWriter(u.getSocket().getOutputStream(), "UTF-8");
                writer.write(jsonOut.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:roomClosed] " + e.getMessage());
            }
        }
    }

    /* GETTERS */
    public HashMap<String, Room> getRoomList() {
        return roomList;
    }

    /* MAIN FUNCTION*/
    public static void main(String[] args)  {
        ServerManager serverManager = new ServerManager(args);
        serverManager.run();
    }
}
