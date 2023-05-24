// is0xSwiftCo
// COMP90015: Assignment2 - Distributed Shared White Board
// Developed By Yun-Chi Hsiao (1074004)
// GitHub: https://github.com/is0xjh25

import org.json.JSONArray;
import org.json.JSONObject;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

public class ClientProcessor implements Runnable {
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
            System.out.println("[LEFT SERVER] " + socket.getRemoteSocketAddress().toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(whiteBoardManager, e.getMessage() + ".", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.out.println("[ERROR:Run] " + e.getMessage() + ".");
            System.exit(-1);
        }
    }

    private void responseHandler(JSONObject res) throws IOException {
        if (res.getString("header").equals("error")) {
            // has sent invalid request before
            JOptionPane.showMessageDialog(whiteBoardManager, res.getString("message"), "COMMUNICATION ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        } else if (res.getString("header").equals("create")) {
            if (res.getString("ok").equals("true")) {
                username = (res.getString("username"));
                roomID = (res.getString("roomID"));
                isManager = (res.getBoolean("isManager"));
                participating = (res.getBoolean("participating"));
                whiteBoardManager.setWhiteBoard();
                whiteBoardManager.getParticipantsBox().updateParticipants(arrayTransform(res.getJSONArray("user-list")), res.getString("username"));
            } else if (res.getString("ok").equals("false")) {
                whiteBoardManager.getGui().setHint(res.getString("message"), "warning");
            } else if (res.getString("ok").equals("error")) {
                whiteBoardManager.getGui().setHint(res.getString("message"), "error");
            }
        } else if (res.getString("header").equals("join")) {
            if (res.getString("ok").equals("true")) {
                username = (res.getString("username"));
                roomID = (res.getString("roomID"));
                isManager = (res.getBoolean("isManager"));
                participating = (res.getBoolean("participating"));
                whiteBoardManager.getGui().enableButtons();
                whiteBoardManager.setWhiteBoard();
            } else if (res.getString("ok").equals("pending")) {
                whiteBoardManager.getGui().setHint(res.getString("message"), "info");
                whiteBoardManager.getGui().disableButtons();
            } else if (res.getString("ok").equals("false")) {
                whiteBoardManager.getGui().setHint(res.getString("message"), "warning");
                whiteBoardManager.getGui().enableButtons();
            } else if (res.getString("ok").equals("error")) {
                whiteBoardManager.getGui().setHint(res.getString("message"), "error");
                whiteBoardManager.getGui().enableButtons();
            }
        } else if (res.getString("header").equals("join-request")) {
            int ans = JOptionPane.showConfirmDialog (
                    whiteBoardManager,
                    "Do you want to let " + res.getString("username") + " to join in?",
                    "JOIN REQUEST",
                    JOptionPane.YES_NO_OPTION);
            JSONObject permission = new JSONObject();
            permission.put("header", "join-request");
            permission.put("username", res.getString("username"));
            permission.put("roomID", res.getString("roomID"));
            if (ans == JOptionPane.YES_OPTION) {
                permission.put("permit", "true");
            } else {
                permission.put("permit", "false");
            }
            try {
                OutputStreamWriter OSWriter = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
                OSWriter.write(permission + "\n");
                OSWriter.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:ResponseHandler] " + e.getMessage() + ".");
            }
        } else if (res.getString("header").equals("update-participants")) {
            whiteBoardManager.getParticipantsBox().updateParticipants(arrayTransform(res.getJSONArray("user-list")), res.getString("manager"));
        } else if (res.getString("header").equals("force-quit")) {
            String[] options = new String[] {"No But Yes", "Who Cares"};
            JOptionPane.showOptionDialog(whiteBoardManager, "You have been kicked out!", "SORRY",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);
            username = (res.getString("username"));
            roomID = (res.getString("roomID"));
            isManager = (res.getBoolean("isManager"));
            participating = (res.getBoolean("participating"));
            whiteBoardManager.setMenu();
        } else if (res.getString("header").equals("room-closed")) {
            String[] options = new String[] {"FINE", "OK"};
            JOptionPane.showOptionDialog(whiteBoardManager, res.getString("message"), "BYE",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);
            username = (res.getString("username"));
            roomID = (res.getString("roomID"));
            isManager = (res.getBoolean("isManager"));
            participating = (res.getBoolean("participating"));
            whiteBoardManager.setMenu();
        } else if (res.getString("header").equals("update-chat")) {
            whiteBoardManager.getChatBox().addMessage(res.getString("username"), res.getString("content"));
        } else if (res.getString("header").equals("update-whiteboard")) {
            if (res.has("content")) {
                String encodedImage = res.getString("content");
                if (encodedImage.length() > 0) {
                    byte[] imageBytes = Base64.getDecoder().decode((encodedImage));
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                    if (image != null) {
                        whiteBoardManager.getWhiteBoard().setG2d(image);
                        whiteBoardManager.getWhiteBoard().setBufferImage(image);
                    }
                }
            }
        }
    }

    /* GETTERS */
    public Socket getSocket() {
        return socket;
    }
    public WhiteBoardManager getWhiteBoardManager() {
        return whiteBoardManager;
    }
    public String getUsername() {
        return username;
    }
    public String getRoomID() {
        return roomID;
    }
    public Boolean getManager() {
        return isManager;
    }
    public Boolean getParticipating() {
        return participating;
    }

    /* HELPER FUNCTIONS */
    private ArrayList arrayTransform(JSONArray jsonArray) {
        ArrayList<String> userList = new ArrayList<>();
        if (jsonArray != null) {
            for (int i=0; i<jsonArray.length(); i++){
                userList.add((String) jsonArray.get(i));
            }
        }
        return  userList;
    }
}
