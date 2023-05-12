import org.json.JSONObject;
import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class ClientProcessor extends Thread {

    private final Socket socket;
    private final WhiteBoardManager whiteBoardManager;
    private String username;
    private String roomID;
    private Boolean isManager;
    private Boolean participating;

    public ClientProcessor(Socket socket) {
        this.socket = socket;
        username = "";
        roomID = "";
        isManager = false;
        participating = false;
        whiteBoardManager = new WhiteBoardManager(this);
        SwingUtilities.invokeLater(() -> whiteBoardManager.setVisible(true));
    }

    @Override
    public void run() {
        try {
            System.out.println("[JOIN SERVER] " + socket.getRemoteSocketAddress().toString());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                responseHandler(new JSONObject(line));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection Failed.", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.out.println("[ERROR:run] " + e.getMessage() + ".");
        }
    }

    private void responseHandler(JSONObject res) {
        if (res.getString("header").equals("create")) {
            if (res.getString("ok").equals("true")) {
                setUsername(res.getString("username"));
                setRoomID(res.getString("roomID"));
                setManager(res.getBoolean("isManager"));
                setParticipating(res.getBoolean("participating"));
                whiteBoardManager.setWhiteBoard();
            } else if (res.getString("ok").equals("false")) {
                System.out.println(res.getString("message"));
            }
        }
    }

    /* GETTERS & SETTERS */
    public Socket getSocket() {
        return socket;
    }
    public WhiteBoardManager getWhiteBoardManager() {
        return whiteBoardManager;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getRoomID() {
        return roomID;
    }
    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
    public Boolean getManager() {
        return isManager;
    }
    public void setManager(Boolean manager) {
        isManager = manager;
    }
    public Boolean getParticipating() {
        return participating;
    }
    public void setParticipating(Boolean participating) {
        this.participating = participating;
    }
}
