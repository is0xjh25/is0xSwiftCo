import org.json.JSONObject;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.*;

public class WhiteBoardManager extends JFrame {

    private Socket socket;
    private Boolean isManager;
    private String username;
    private WhiteBoard whiteBoard;
    private ManagerBar managerBar;

    public WhiteBoardManager() {
        super("SWIFTCO - SHARED WHITE BOARD");
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setSize(1000, 600);
        isManager = true;
        whiteBoard = new WhiteBoard();
        JPanel whiteBoardContainer = new JPanel();
        JPanel toolBar = new ToolBar(whiteBoard);
        JPanel chatBox = new ChatBox(1234); //roomID, socket
        ParticipantsBox participantsBox = new ParticipantsBox(isManager, "Jim"); //socket
        managerBar = new ManagerBar(whiteBoard, isManager); // whiteBoard, isManager
        ArrayList<String> temp = new ArrayList<String>();
        temp.add("123456789012345");
        temp.add("Matt");
        temp.add("Tom");
        temp.add("Jim");

        participantsBox.updateParticipants(temp);
        whiteBoard.setPreferredSize(new Dimension(500, 525));
        toolBar.setPreferredSize(new Dimension(500, 50));
        managerBar.setPreferredSize(new Dimension(500, 25));
        chatBox.setPreferredSize(new Dimension(250, 600));
        participantsBox.setPreferredSize(new Dimension(250, 600));
        whiteBoardContainer.setBackground(Color.WHITE);
        whiteBoard.setPreferredSize(new Dimension(500, 500));

        add(toolBar, BorderLayout.NORTH);
        add(managerBar, BorderLayout.SOUTH);
        add(chatBox, BorderLayout.EAST);
        add(participantsBox, BorderLayout.WEST);
        whiteBoardContainer.add(whiteBoard);
        add(whiteBoardContainer, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setResizable(false);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAPP();
            }
        });
    }
    
    private void closeAPP() {
        int ans = JOptionPane.showConfirmDialog (
                null,
                "Do you want to leave SwiftCo?",
                "CLOSED APP",
                JOptionPane.YES_NO_OPTION);

        if (ans == JOptionPane.YES_OPTION) {
            managerBar.exitWarning();
            try {
                JSONObject json = new JSONObject();
                if (isManager) {
                    json.put("header", "manager-exit");
                    json.put("body", username);
                } else {
                    json.put("header", "participant-exit");
                    json.put("body", username);
                }
                OutputStreamWriter OSWriter = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
                OSWriter.write(json + "\n");
                OSWriter.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            System.exit(0);
        }
    }

    /* GETTERS & SETTERS */
    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    public WhiteBoard getWhiteBoard() {
        return whiteBoard;
    }
    public void setWhiteBoard(WhiteBoard whiteBoard) {
        this.whiteBoard = whiteBoard;
    }
    public Boolean getManager() {
        return isManager;
    }
    public void setManager(Boolean manager) {
        isManager = manager;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    /* MAIN FUNCTION */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WhiteBoardManager().setVisible(true));
    }
}
