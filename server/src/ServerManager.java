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
        CmdLineParser parser = new CmdLineParser(serverArgs);

        // parse parameters.
        try {
            parser.parseArgument(args);
            serverPort = serverArgs.getPort();
        } catch (CmdLineException e) {
            System.out.println("[ERROR:ServerManager] " + e.getMessage() + ".");
            parser.printUsage(System.out);
            System.exit(0);
        }
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(serverPort);
            serverSocket.setReuseAddress(true);
            System.out.println("[SUCCEED] SwiftCo server has been initiated.");
            while (true) {
                Socket userSocket = serverSocket.accept();
                ServerProcessor serverProcessor  = new ServerProcessor(this, userSocket);
                new Thread(serverProcessor).start();
            }
        } catch (IOException e) {
            System.out.println("[ERROR:run] " + e.getMessage() + ".");
        }
    }

    synchronized public Room createRoom(String roomID, ServerProcessor manager) {
        if (roomList.containsKey(roomID)) {
            return null;
        } else {
            Room newRoom = new Room(roomID, manager);
            roomList.put(roomID, newRoom);
            return newRoom;
        }
    }

    synchronized public void broadcastUserList(Room room) {
        List<String> userArray = new ArrayList<>();
        // get all the names.
        for (Map.Entry<String, ServerProcessor> entry : room.getUserList().entrySet()) {
            userArray.add(entry.getKey());
        }

        // send the latest user list to everyone in the room
        for (ServerProcessor user : room.getUserList().values()) {
            JSONObject res = new JSONObject();
            res.put("header", "update-participants");
            res.put("user-list", new JSONArray(userArray));
            res.put("manager", room.getManager().getUsername());
            try {
                OutputStreamWriter writer = new OutputStreamWriter(user.getSocket().getOutputStream(), "UTF-8");
                writer.write(res.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:updateUserList] " + e.getMessage() + ".");
            }
        }
    }

    synchronized public boolean forwardJoinRequest(String roomID, ServerProcessor user) {
        JSONObject req = new JSONObject();
        if (roomList.containsKey(roomID) && !roomList.get(roomID).isUserExist(user.getUsername())) {
            req.put("header", "join-request");
            req.put("roomID", roomID);
            req.put("username", user.getUsername());
            try {
                req = roomList.get(roomID).getManager().setStat(req);
                OutputStreamWriter writer = new OutputStreamWriter(roomList.get(roomID).getManager().getSocket().getOutputStream(), "UTF-8");
                writer.write(req.toString() + "\n");
                writer.flush();
                roomList.get(roomID).addUser(user);
                return true;
            } catch (IOException e) {
                System.out.println("[ERROR:forwardJoinRequest] " + e.getMessage() + ".");
                return false;
            }
        } else {
            return false;
        }
    }

    synchronized public void handleJoinResponse(JSONObject req) {
        JSONObject res = new JSONObject();
        String roomID = req.getString("roomID");
        String username = req.getString("username");
        Room joinRoom = roomList.get(roomID);
        ServerProcessor user = joinRoom.getUserList().get(username);

        res.put("header", "join-request");
        if (req.getString("permit").equals("true")) {
            res.put("permit", "true");
            try {
                // set user
                user.setCurrentStay(joinRoom);
                user.setParticipating(true);
                res = user.setStat(res);
                OutputStreamWriter writer = new OutputStreamWriter(user.getSocket().getOutputStream(), "UTF-8");
                writer.write(res.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:handleJoinResponse] " + e.getMessage() + ".");
            }
            // update user list
            broadcastUserList(joinRoom);
        } else if (req.getString("permit").equals("false")) {
            res.put("permit", "false");
            try {
                user.resetUser();
                res = user.setStat(res);
                OutputStreamWriter writer = new OutputStreamWriter(user.getSocket().getOutputStream(), "UTF-8");
                writer.write(res.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:handleJoinRequest] " + e.getMessage() + ".");
            }
        }
    }

    public void kickOut(Room room, String username) {
        if (room.isUserExist(username)) {
            room.getUserList().get(username).resetUser();
            broadcastUserList(room);
        }
    }

    public void userLeft(Room room, ServerProcessor user) {
        if (user.isManager()) {
            user.resetUser();
            roomClosed(room);
        } else {
            user.resetUser();
            broadcastUserList(room);
        }
    }

    synchronized public void broadcastWhiteBoard(Room room, ServerProcessor user, JSONObject whiteboard) {
        room.setEncodeWhitBoard(whiteboard);
        for (ServerProcessor u: room.getUserList().values()) {
            if (user.getUsername().equals(u.getUsername())) continue;
            try {
                OutputStreamWriter writer = new OutputStreamWriter(u.getSocket().getOutputStream(), "UTF-8");
                writer.write(whiteboard.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:broadcastWhiteBoard] " + e.getMessage() + ".");
            }
        }
    }

    public void broadcastChat(Room room, ServerProcessor user, JSONObject chat) {
        if (chat.getString("content").length() > 0) {
            for (ServerProcessor u: room.getUserList().values()) {
                if (user.getUsername().equals(u.getUsername())) continue;
                try {
                    OutputStreamWriter writer = new OutputStreamWriter(u.getSocket().getOutputStream(), "UTF-8");
                    writer.write(chat + "\n");
                    writer.flush();
                } catch (IOException e) {
                    System.out.println("[ERROR:broadcastChat] " + e.getMessage() + ".");
                }
            }
        }
    }

    public void roomClosed(Room room) {
        for (ServerProcessor u : room.getUserList().values()) {
            u.resetUser();
            JSONObject res= new JSONObject();
            res.put("header", "room-closed");
            res.put("message", "The manager has left.");
            try {
                res = u.setStat(res);
                OutputStreamWriter writer = new OutputStreamWriter(u.getSocket().getOutputStream(), "UTF-8");
                writer.write(res.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:roomClosed] " + e.getMessage() + ".");
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
