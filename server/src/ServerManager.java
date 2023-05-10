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
    private final HashMap<Integer, Room> roomList;

    public ServerManager(String args[]) {
        roomList = new HashMap<>();
        ServerArgs serverArgs = new ServerArgs();
        serverPort = 0;
        CmdLineParser parser = new CmdLineParser(serverArgs);

        // parse parameters.
        try {
            parser.parseArgument(args);
            serverPort = serverArgs.getPort();
        } catch (CmdLineException e) {
            System.out.println("[ERROR] " + e.getMessage());
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
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    public void updateUserList(Room room) {
        List<String> userArray = new ArrayList<>();
        // get all the names.
        for (User u : room.getUserList()) {
            userArray.add(u.getUsername());
        }

        // send the latest user list to everyone in the room
        for (User u : room.getUserList()) {
            JSONObject jsonOut = new JSONObject();
            jsonOut.put("header", "update-participants");
            jsonOut.put("body", new JSONArray(userArray));
            jsonOut.put("manager", room.getManager().getUsername());
            try {
                OutputStreamWriter writer = new OutputStreamWriter(u.getSocket().getOutputStream(), "UTF-8");
                writer.write(jsonOut.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }

    public void roomClosed(Room room) {
        for (User u : room.getUserList()) {
            JSONObject jsonOut = new JSONObject();
            jsonOut.put("header", "room-closed");
            jsonOut.put("message", "The manager has leaved.");
            try {
                OutputStreamWriter writer = new OutputStreamWriter(u.getSocket().getOutputStream(), "UTF-8");
                writer.write(jsonOut.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }


    /* GETTERS */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }
    public HashMap<Integer, Room> getRoomList() {
        return roomList;
    }

    /* MAIN FUNCTION*/
    public static void main(String args[])  {
        ServerManager serverManager = new ServerManager(args);
        serverManager.run();
    }
}
