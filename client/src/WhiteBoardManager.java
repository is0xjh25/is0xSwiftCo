import org.json.JSONObject;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import javax.swing.*;

public class WhiteBoardManager extends JFrame {

    private final ClientProcessor cp;
    private JPanel whiteBoardContainer;
    private JPanel toolBar;
    private JPanel chatBox;
    private ParticipantsBox participantsBox;
    private WhiteBoard whiteBoard;
    private ManagerBar managerBar;
    private Gui gui;

    public WhiteBoardManager(ClientProcessor cp) {
        super("SWIFTCO - SHARED WHITE BOARD");
        this.cp = cp;
        init();
    }

    private void init() {
        gui = new Gui(cp);
        setLayout(new BorderLayout());
        setSize(1000, 600);
        whiteBoard = new WhiteBoard(cp);
        whiteBoardContainer = new JPanel();
        toolBar = new ToolBar(whiteBoard);
        chatBox = new ChatBox(cp);
        participantsBox = new ParticipantsBox(cp);
        managerBar = new ManagerBar(cp, whiteBoard);
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

        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAPP();
            }
        });
        setMenu();
    }

    public void setMenu() {
        removeWindowListener(getWindowListeners()[0]);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAPP();
            }
        });
        add(gui, BorderLayout.CENTER);
        gui.setVisible(true);
        whiteBoardContainer.setVisible(false);
        toolBar.setVisible(false);
        chatBox.setVisible(false);
        participantsBox.setVisible(false);
        whiteBoard.setVisible(false);
        managerBar.setVisible(false);
        remove(whiteBoardContainer);
    }

    public void setWhiteBoard() {
        removeWindowListener(getWindowListeners()[0]);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                leftRoom();
            }
        });
        whiteBoardContainer.setVisible(true);
        toolBar.setVisible(true);
        chatBox.setVisible(true);
        participantsBox.setVisible(true);
        whiteBoard.setVisible(true);
        managerBar.setVisible(true);
        add(whiteBoardContainer, BorderLayout.CENTER);
        remove(gui);
    }
    
    private void leftRoom() {
        int ans = JOptionPane.showConfirmDialog (
                null,
                "Do you want to leave the Room #" + cp.getRoomID() + " ?",
                "LEAVE ROOM",
                JOptionPane.YES_NO_OPTION);

        if (ans == JOptionPane.YES_OPTION) {
            managerBar.exitWarning();
            try {
                JSONObject req = new JSONObject();
                req.put("header", "user-quit");
                req.put("username", cp.getUsername());
                OutputStreamWriter OSWriter = new OutputStreamWriter(cp.getSocket().getOutputStream(), "UTF-8");
                OSWriter.write(req + "\n");
                OSWriter.flush();
            } catch (IOException e) {
                System.out.println("[ERROR:leftRoom] " + e.getMessage() + ".");
                System.exit(1);
            }
            setMenu();
        }
    }

    private void closeAPP() {
        int ans = JOptionPane.showConfirmDialog (
                    null,
                    "Do you want to leave SwiftCo?",
                    "CLOSED APP",
                    JOptionPane.YES_NO_OPTION);

        if (ans == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
