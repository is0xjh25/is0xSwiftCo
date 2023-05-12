import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.regex.Pattern;

public class Gui extends JPanel {
    public enum Usage {
        MANAGER,
        PARTICIPANT
    }
    private final ClientProcessor cp;
    private Usage usage;
    private JPanel headerPanel;
    private JPanel roomIDPanel;
    private JPanel usernamePanel;
    private JPanel buttonsPanel;
    private JPanel hintPanel;
    private JTextField usernameTextField;
    private JTextField roomIDTextField;
    private JButton createButton;
    private JButton joinButton;
    private JLabel hintLabel;
    private String roomID;
    private String username;

    public Gui(ClientProcessor cp) {
        this.usage = Usage.MANAGER;
        this.cp = cp;
        init();
    }

    private void init() {
        setLayout(new GridLayout(1, 3));
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        JPanel guiPanel = new JPanel(new GridLayout(5, 1));
        setHeaderPanel();
        setUsernamePanel();
        setRoomIDPanel();
        setButtonsPanel();
        setHintPanel();
        guiPanel.add(headerPanel);
        guiPanel.add(roomIDPanel);
        guiPanel.add(usernamePanel);
        guiPanel.add(buttonsPanel);
        guiPanel.add(hintPanel);
        add(leftPanel);
        add(guiPanel);
        add(rightPanel);
        if (usage == Usage.MANAGER) setCreateFrame();
        if (usage == Usage.PARTICIPANT) setJoinFrame();
        setVisible(true);
    }

    private void setCreateFrame() {
        createButton.setEnabled(true);
        joinButton.setEnabled(false);
        setHint("Welcome to SwiftCo Shared WhiteBoard!", "welcome");
    }

    private void setJoinFrame() {
        createButton.setEnabled(false);
        joinButton.setEnabled(true);
        setHint("Welcome to SwiftCo Shared WhiteBoard!", "welcome");
    }

    /* SETUP PANELS */
    private void setHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        JLabel headerLabel = new JLabel("~ SwiftCo ~", SwingConstants.CENTER);
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setFont(new Font("Mono", Font.BOLD, 40));
        headerPanel.add(headerLabel, BorderLayout.CENTER);
    }

    private void setRoomIDPanel() {
        roomIDPanel = new JPanel();
        roomIDPanel.setBackground(Color.WHITE);
        JLabel roomIDLabel = new JLabel("Room ID", SwingConstants.CENTER);
        roomIDLabel.setPreferredSize(new Dimension(400, 30));
        roomIDLabel.setFont(new Font("Mono", Font.BOLD, 20));
        roomIDLabel.setForeground(Color.BLACK);
        roomIDTextField = new JTextField(3);
        roomIDTextField.setFont(new Font("Mono", Font.PLAIN, 20));
        roomIDTextField.setBorder(new MatteBorder(2, 2, 2, 2, Color.BLACK));
        roomIDTextField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) { if (roomIDTextField.getText().length() >= 4) e.consume(); }
            public void keyReleased(KeyEvent e) { roomID = ((JTextField) e.getSource()).getText(); }
        });
        roomIDPanel.add(roomIDLabel);
        roomIDPanel.add(roomIDTextField);
    }

    private void setUsernamePanel() {
        usernamePanel = new JPanel();
        usernamePanel.setBackground(Color.WHITE);
        JLabel usernameLabel = new JLabel("Name Yourself", SwingConstants.CENTER);
        usernameLabel.setPreferredSize(new Dimension(400,30));
        usernameLabel.setFont(new Font("Mono", Font.BOLD, 20));
        usernameLabel.setForeground(Color.BLACK);
        usernameTextField = new JTextField(username, 10);
        usernameTextField.setFont(new Font("Mono", Font.PLAIN, 22));
        usernameTextField.setBorder(new MatteBorder(2, 2, 2, 2, Color.BLACK));
        usernameTextField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) { if (usernameTextField.getText().length() >= 15) e.consume(); }
            public void keyReleased(KeyEvent e) { username = ((JTextField) e.getSource()).getText(); }
        });
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameTextField);
    }

    private void setButtonsPanel() {
        buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(new EmptyBorder(30, 0, 30, 0));

        createButton = new JButton("CREATE");
        createButton.setPreferredSize(new Dimension(100, 60));
        createButton.addActionListener(e -> {
            if (validInput(usage)) {
                JSONObject req = new JSONObject();
                try {
                    req.put("header", "create");
                    req.put("roomID", roomID);
                    req.put("username", username);
                    OutputStreamWriter OSWriter = new OutputStreamWriter(cp.getSocket().getOutputStream(), "UTF-8");
                    OSWriter.write(req + "\n");
                    OSWriter.flush();
                } catch (IOException ex) {
                    System.out.println("[ERROR:create] " + ex.getMessage() + ".");
                }
            }
        });

        joinButton = new JButton("JOIN");
        joinButton.setPreferredSize(new Dimension(100, 60));
        joinButton.addActionListener(e -> {
            if (validInput(usage)) {
                JSONObject req = new JSONObject();
                try {
                    req.put("header", "join");
                    req.put("roomID", roomID);
                    req.put("username", username);
                    OutputStreamWriter OSWriter = new OutputStreamWriter(cp.getSocket().getOutputStream(), "UTF-8");
                    OSWriter.write(req + "\n");
                    OSWriter.flush();
                } catch (IOException ex) {
                    System.out.println("[ERROR:join] " + ex.getMessage() + ".");
                }
            }
        });

        JButton changeButton = new JButton();
        IconFontSwing.register(FontAwesome.getIconFont());
        changeButton.setIcon(IconFontSwing.buildIcon(FontAwesome.EXCHANGE, 20));
        changeButton.addActionListener(e -> {
            if (usage == Usage.MANAGER) {
                usage = Usage.PARTICIPANT;
                setJoinFrame();
            } else if (usage == Usage.PARTICIPANT) {
                usage = Usage.MANAGER;
                setCreateFrame();
            }
        });
        buttonsPanel.add(createButton);
        buttonsPanel.add(changeButton);
        buttonsPanel.add(joinButton);
    }

    private void setHintPanel() {
        hintPanel = new JPanel(new BorderLayout());
        hintPanel.setBackground(Color.WHITE);
        hintLabel = new JLabel("Welcome to SwiftCo Shared WhiteBoard!", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Mono", Font.BOLD, 16));
        setHint("Welcome to SwiftCo Shared WhiteBoard!", "welcome");
        hintPanel.add(hintLabel, BorderLayout.CENTER);
    }

    /* HELPER FUNCTIONS */
    private void setHint(String text, String type) {
        hintLabel.setText(text);
        if (type.equals("warning")) {
            hintLabel.setForeground(Color.ORANGE);
        } else if (type.equals("info")) {
            hintLabel.setForeground(Color.CYAN);
        } else if (type.equals("error")) {
            hintLabel.setForeground(Color.CYAN);
        } else if (type.equals("welcome")) {
            hintLabel.setForeground(Color.BLACK);
        }
    }

    public boolean validInput(Usage usage) {
        // check roomID
        if (roomIDTextField.getText().length() == 0) {
            setHint("Room ID cannot be empty!", "warning");
            return false;
        }
        // check username
        if (usernameTextField.getText().length() == 0) {
            setHint("Username cannot be empty!", "warning");
            return false;
        }

        Pattern roomIDPattern = Pattern.compile("^[0-9]{4}$");
        if (!roomIDPattern.matcher(roomID).matches()) {
            setHint("Invalid room ID.", "warning");
            return false;
        }

        Pattern usernamePattern = Pattern.compile("^[a-zA-z0-9]{1,15}$");
        if (!usernamePattern.matcher(username).matches()) {
            setHint("Invalid username.", "warning");
            return false;
        }

        return true;
    }
}
