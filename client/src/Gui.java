import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

public class Gui extends JFrame {
    public enum Usage {
        MANAGER,
        PARTICIPANT
    }

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

    public Gui(Usage usage) {
        super("SWIFTCO - MENU");
        this.usage = usage;
        roomID = "";
        username = "";
        init();
    }

    private void init() {
        setLayout(new GridLayout(5, 1));
        setPreferredSize(new Dimension(400, 480));
        setHeaderPanel();
        setUsernamePanel();
        setRoomIDPanel();
        setButtonsPanel();
        setHintPanel();
        add(headerPanel);
        add(roomIDPanel);
        add(usernamePanel);
        add(buttonsPanel);
        add(hintPanel);
        if (usage == Usage.MANAGER) setCreateFrame();
        if (usage == Usage.PARTICIPANT) setJoinFrame();
        pack();
        setVisible(true); // making the frame visible.
        setResizable(false);
        setLocationRelativeTo(null); // set the window in the center of the screen.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        headerPanel.setBackground(Color.DARK_GRAY);
        JLabel headerLabel = new JLabel("~ SwiftCo ~", SwingConstants.CENTER);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Mono", Font.BOLD, 40));
        headerPanel.add(headerLabel, BorderLayout.CENTER);
    }
    private void setRoomIDPanel() {
        roomIDPanel = new JPanel();
        roomIDPanel.setBackground(Color.DARK_GRAY);
        JLabel roomIDLabel = new JLabel("Room ID", SwingConstants.CENTER);
        roomIDLabel.setPreferredSize(new Dimension(400, 30));
        roomIDLabel.setFont(new Font("Mono", Font.BOLD, 20));
        roomIDLabel.setForeground(Color.WHITE);
        roomIDTextField = new JTextField(3);
        roomIDTextField.setFont(new Font("Mono", Font.PLAIN, 20));
        roomIDTextField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) { if (roomIDTextField.getText().length() >= 4) e.consume(); }
            public void keyReleased(KeyEvent e) { roomID = ((JTextField) e.getSource()).getText(); }
        });
        roomIDPanel.add(roomIDLabel);
        roomIDPanel.add(roomIDTextField);
    }

    private void setUsernamePanel() {
        usernamePanel = new JPanel();
        usernamePanel.setBackground(Color.DARK_GRAY);
        JLabel usernameLabel = new JLabel("Name Yourself", SwingConstants.CENTER);
        usernameLabel.setPreferredSize(new Dimension(400,30));
        usernameLabel.setFont(new Font("Mono", Font.BOLD, 20));
        usernameLabel.setForeground(Color.WHITE);
        usernameTextField = new JTextField(username, 10);
        usernameTextField.setFont(new Font("Mono", Font.PLAIN, 22));
        usernameTextField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) { if (usernameTextField.getText().length() >= 15) e.consume(); }
            public void keyReleased(KeyEvent e) { username = ((JTextField) e.getSource()).getText(); }
        });
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameTextField);
    }

    private void setButtonsPanel() {
        buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.setBackground(Color.DARK_GRAY);
        buttonsPanel.setBorder(new EmptyBorder(30, 0, 30, 0));

        createButton = new JButton("CREATE");
        createButton.setPreferredSize(new Dimension(100, 60));
        createButton.addActionListener(e -> {
            if (validInput(usage)) {
                System.out.println("114");
            }
        });

        joinButton = new JButton("JOIN");
        joinButton.setPreferredSize(new Dimension(100, 60));
        joinButton.addActionListener(e -> {
            if (validInput(usage)) {
                System.out.println("122");
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
        hintPanel.setBackground(Color.DARK_GRAY);
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
            hintLabel.setForeground(Color.WHITE);
        }
    }

    public boolean validInput(Usage usage) {
        System.out.println("163" + roomID);
        System.out.println("164" + username);
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

        // check with server!!!
        if (usage == Usage.MANAGER) {

        } else if (usage == Usage.PARTICIPANT) {

        }
        // check roomID
        return true;
    }
}
