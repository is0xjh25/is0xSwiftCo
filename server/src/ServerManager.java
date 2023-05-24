// is0xSwiftCo
// COMP90015: Assignment2 - Distributed Shared White Board
// Developed By Yun-Chi Hsiao (1074004)
// GitHub: https://github.com/is0xjh25

import org.json.JSONArray;
import org.json.JSONObject;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerManager {
    private int serverPort;
    private final ConcurrentHashMap<String, Room> roomList;

    public ServerManager(String[] args) {
        roomList = new ConcurrentHashMap<>();
        ServerArgs serverArgs = new ServerArgs();
        CmdLineParser parser = new CmdLineParser(serverArgs);

        // parse parameters.
        try {
            parser.parseArgument(args);
            serverPort = serverArgs.getPort();
        } catch (CmdLineException e) {
            System.out.println("[ERROR:ServerManager] " + e.getMessage() + ".");
            parser.printUsage(System.out);
            System.exit(-1);
        }
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            serverSocket.setReuseAddress(true);
            System.out.println("[SUCCEED] SwiftCo server has been initiated.");
            while (true) {
                Socket userSocket = serverSocket.accept();
                ServerProcessor serverProcessor  = new ServerProcessor(this, userSocket);
                new Thread(serverProcessor).start();
            }
        } catch (IOException e) {
            System.out.println("[ERROR:Run] " + e.getMessage() + ".");
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
                OutputStreamWriter writer = new OutputStreamWriter(user.getSocket().getOutputStream(), StandardCharsets.UTF_8);
                writer.write(res + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:UpdateUserList] " + e.getMessage() + ".");
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
                OutputStreamWriter writer = new OutputStreamWriter(roomList.get(roomID).getManager().getSocket().getOutputStream(), StandardCharsets.UTF_8);
                writer.write(req + "\n");
                writer.flush();
                roomList.get(roomID).addUser(user);
                return true;
            } catch (IOException e) {
                System.out.println("[ERROR:ForwardJoinRequest] " + e.getMessage() + ".");
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

        res.put("header", "join");
        if (req.getString("permit").equals("true")) {
            res.put("ok", "true");
            res.put("message", "Welcome.");
            try {
                user.setCurrentStay(joinRoom);
                user.setParticipating(true);
                res = user.setStat(res);
                OutputStreamWriter writer = new OutputStreamWriter(user.getSocket().getOutputStream(), StandardCharsets.UTF_8);
                writer.write(res.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:HandleJoinResponse] " + e.getMessage() + ".");
            }
            try {
                JSONObject oldWhiteBoard = joinRoom.getEncodeWhitBoard();
                if (oldWhiteBoard == null) oldWhiteBoard.put("content", "");
                oldWhiteBoard.put("header", "update-whiteboard");
                OutputStreamWriter writer = new OutputStreamWriter(user.getSocket().getOutputStream(), StandardCharsets.UTF_8);
                writer.write(oldWhiteBoard + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:UpdateWhiteBoard] " + e.getMessage() + ".");
            }
            // update user list
            broadcastUserList(joinRoom);
        } else if (req.getString("permit").equals("false")) {
            res.put("ok", "false");
            res.put("message", "Not permitted.");
            try {
                user.resetUser();
                res = user.setStat(res);
                OutputStreamWriter writer = new OutputStreamWriter(user.getSocket().getOutputStream(), StandardCharsets.UTF_8);
                writer.write(res.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:HandleJoinRequest] " + e.getMessage() + ".");
            }
        }
    }

    public void kickOut(Room room, String username) {
        if (room.isUserExist(username)) {
            ServerProcessor user = room.getUserList().get(username);
            try {
                JSONObject res = new JSONObject();
                res.put("header", "force-quit");
                user.resetUser();
                res = user.setStat(res);
                OutputStreamWriter writer = new OutputStreamWriter(user.getSocket().getOutputStream(), StandardCharsets.UTF_8);
                writer.write(res.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:KickOut] " + e.getMessage() + ".");
            }
            room.getUserList().remove(username);
            broadcastUserList(room);
        }
    }

    public void userLeft(Room room, ServerProcessor user) {
        if (user.isManager()) {
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
                OutputStreamWriter writer = new OutputStreamWriter(u.getSocket().getOutputStream(), StandardCharsets.UTF_8);
                writer.write(whiteboard.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:BroadcastWhiteBoard] " + e.getMessage() + ".");
            }
        }
    }

    public void broadcastChat(Room room, ServerProcessor user, JSONObject chat) {
        if (chat.getString("content").length() > 0) {
            for (ServerProcessor u: room.getUserList().values()) {
                if (user.getUsername().equals(u.getUsername())) continue;
                try {
                    OutputStreamWriter writer = new OutputStreamWriter(u.getSocket().getOutputStream(), StandardCharsets.UTF_8);
                    writer.write(chat + "\n");
                    writer.flush();
                } catch (IOException e) {
                    System.out.println("[ERROR:BroadcastChat] " + e.getMessage() + ".");
                }
            }
        }
    }

    public void roomClosed(Room room) {
        String manager = room.getManager().getUsername();
        for (Iterator<String> keys = room.getUserList().keySet().iterator(); keys.hasNext();) {
            String key = keys.next();
            ServerProcessor user = room.getUserList().get(key);
            user.resetUser();
            if (key.equals(manager)) continue;
            JSONObject res= new JSONObject();
            res.put("header", "room-closed");
            res.put("message", "The manager has left.");
            try {
                res = user.setStat(res);
                OutputStreamWriter writer = new OutputStreamWriter(user.getSocket().getOutputStream(), StandardCharsets.UTF_8);
                writer.write(res.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:RoomClosed] " + e.getMessage() + ".");
            }
        }
        roomList.remove(room.getRoomID());
    }

    /* GETTERS */
    public ConcurrentHashMap<String, Room> getRoomList() {
        return roomList;
    }

    /* MAIN FUNCTION*/
    public static void main(String[] args)  {
        ServerManager serverManager = new ServerManager(args);
        serverManager.start();
    }
}
