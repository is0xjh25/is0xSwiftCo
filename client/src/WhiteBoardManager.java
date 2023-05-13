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
    private ChatBox chatBox;
    private ParticipantsBox participantsBox;
    private WhiteBoard whiteBoard;
    private ManagerBar managerBar;
    private Gui gui;
    private Boolean isMenu;

    public WhiteBoardManager(ClientProcessor cp) {
        super("SWIFTCO - SHARED WHITE BOARD");
        this.cp = cp;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setSize(1000, 600);
        isMenu = true;
        gui = new Gui(cp);
        add(gui, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeApp();
            }
        });
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    public void setMenu() {
        isMenu = true;
        add(gui, BorderLayout.CENTER);
        gui.setHint("Welcome to SwiftCo Shared WhiteBoard!", "welcome");
        gui.enableButtons();
        remove(whiteBoard);
        remove(whiteBoardContainer);
        remove(toolBar);
        remove(chatBox);
        remove(participantsBox);
        remove(managerBar);
        repaint();
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    public void setWhiteBoard() {
        isMenu = false;
        whiteBoard = new WhiteBoard(cp);
        whiteBoardContainer = new JPanel();
        toolBar = new ToolBar(whiteBoard);
        chatBox = new ChatBox(cp);
        participantsBox = new ParticipantsBox(cp);
        managerBar = new ManagerBar(cp, whiteBoard);
        toolBar.setPreferredSize(new Dimension(500, 50));
        managerBar.setPreferredSize(new Dimension(500, 25));
        chatBox.setPreferredSize(new Dimension(250, 600));
        participantsBox.setPreferredSize(new Dimension(250, 600));
        whiteBoard.setPreferredSize(new Dimension(500, 500));
        whiteBoardContainer.setBackground(Color.WHITE);
        whiteBoardContainer.setPreferredSize(new Dimension(500, 500));
        whiteBoardContainer.add(whiteBoard);
        add(toolBar, BorderLayout.NORTH);
        add(managerBar, BorderLayout.SOUTH);
        add(chatBox, BorderLayout.EAST);
        add(participantsBox, BorderLayout.WEST);
        add(whiteBoardContainer, BorderLayout.CENTER);
        remove(gui);
        repaint();
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    private void closeApp() {
        int ans = JOptionPane.showConfirmDialog (
                    this,
                    "Do you want to leave SwiftCo?",
                    "CLOSED APP",
                    JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) {
            if (!isMenu && whiteBoard.isModified() && cp.getManager()) {
                int res = JOptionPane.showConfirmDialog(this, "Save changes before you leave?", "SAVE CHANGES", JOptionPane.YES_NO_CANCEL_OPTION);
                if (res == JOptionPane.YES_OPTION) managerBar.save();
                if (res == JOptionPane.CANCEL_OPTION) return;
            }
            if (cp.getParticipating()) {
                try {
                    JSONObject req = new JSONObject();
                    req.put("header", "user-quit");
                    req.put("username", cp.getUsername());
                    OutputStreamWriter OSWriter = new OutputStreamWriter(cp.getSocket().getOutputStream(), "UTF-8");
                    OSWriter.write(req + "\n");
                    OSWriter.flush();
                } catch (IOException e) {
                    System.out.println("[ERROR:closeApp] " + e.getMessage() + ".");
                    System.exit(1);
                }
            }
            System.exit(0);
        }
    }

    /* Getters */
    public JPanel getWhiteBoardContainer() {
        return whiteBoardContainer;
    }
    public JPanel getToolBar() {
        return toolBar;
    }
    public ChatBox getChatBox() {
        return chatBox;
    }
    public ParticipantsBox getParticipantsBox() {
        return participantsBox;
    }
    public WhiteBoard getWhiteBoard() {
        return whiteBoard;
    }
    public ManagerBar getManagerBar() {
        return managerBar;
    }
    public Gui getGui() {
        return gui;
    }
    public Boolean getMenu() {
        return isMenu;
    }
}
