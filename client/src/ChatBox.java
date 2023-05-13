import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ChatBox extends JPanel {

    private ClientProcessor cp;
    private Box messagesBox;
    private String sent;

    public ChatBox(ClientProcessor cp) {
        this.cp = cp;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(new MatteBorder(5, 5, 5, 5, Color.LIGHT_GRAY));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.DARK_GRAY);
        headerPanel.setPreferredSize(new Dimension(250, 75));
        headerPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        JLabel roomLabel = new JLabel("ROOM ID: #" + cp.getRoomID(), SwingConstants.CENTER);
        roomLabel.setForeground(Color.WHITE);
        roomLabel.setFont(new Font("Mono", Font.BOLD, 16));
        JLabel userLabel = new JLabel("Participant", SwingConstants.CENTER);
        userLabel.setForeground(Color.LIGHT_GRAY);
        userLabel.setFont(new Font("Mono", Font.ITALIC, 14));
        JLabel usernameLabel = new JLabel(cp.getUsername(), SwingConstants.CENTER);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Mono", Font.BOLD, 20));
        headerPanel.add(roomLabel, BorderLayout.NORTH);
        headerPanel.add(userLabel, BorderLayout.CENTER);
        headerPanel.add(usernameLabel, BorderLayout.SOUTH);

        JPanel messagesPanel = new JPanel(new BorderLayout());
        messagesBox = Box.createVerticalBox();
        messagesBox.setBackground(Color.DARK_GRAY);
        messagesPanel.add(messagesBox, BorderLayout.PAGE_START);
        JScrollPane messagesScrollPane = new JScrollPane(messagesPanel);
        messagesScrollPane.setBorder(new MatteBorder(5, 5, 5, 5, Color.DARK_GRAY));
        messagesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        messagesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setPreferredSize(new Dimension(250, 100));
        JTextArea inputTextArea = new JTextArea();
        inputTextArea.setLineWrap(true);
        inputTextArea.setBackground(Color.DARK_GRAY);
        inputTextArea.setForeground(Color.WHITE);
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputScrollPane.setPreferredSize(new Dimension(201, 100));
        inputScrollPane.setBorder(null);
        inputScrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        inputScrollPane.getVerticalScrollBar().setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        inputTextArea.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
               sent = ((JTextArea) e.getSource()).getText();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.DARK_GRAY);
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setPreferredSize(new Dimension(40, 100));
        JButton returnButton = new JButton(IconFontSwing.buildIcon(FontAwesome.COMMENT_O, 20));
        returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("82:" + sent);
                if (!sent.isBlank()) {
                    try {
                        JSONObject req = new JSONObject();
                        req.put("header", "update-chat");
                        req.put("username", cp.getUsername());
                        req.put("roomID", cp.getRoomID());
                        req.put("content", sent);
                        OutputStreamWriter OSWriter = new OutputStreamWriter(cp.getSocket().getOutputStream(), "UTF-8");
                        OSWriter.write(req + "\n");
                        OSWriter.flush();
                        addMessage(cp.getUsername(), sent);
                        sent = "";
                        inputTextArea.setText("");
                    } catch (IOException ex) {
                        System.out.println("[ERROR:textReturn] " + ex.getMessage() + ".");
                        System.exit(-1);
                    }
                }
            }
        });

        buttonPanel.add(returnButton, BorderLayout.CENTER);

        inputPanel.add(inputScrollPane, BorderLayout.WEST);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(messagesScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    public void addMessage(String username, String content) {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(new EmptyBorder(2, 5, 5, 0));
        JLabel usernameLabel = new JLabel("[" + username + "]");
        usernameLabel.setFont(new Font("Mono", Font.BOLD, 14));
        if (username.equals(cp.getUsername())) usernameLabel.setForeground(Color.MAGENTA);
        usernameLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JTextArea contentTextArea = new JTextArea(content);
        contentTextArea.setBackground(new Color(238, 238, 238));
        contentTextArea.setEditable(false);
        contentTextArea.setFont(new Font("Mono", Font.PLAIN, 14));
        contentTextArea.setColumns(16);
        contentTextArea.setLineWrap(true);
        messagePanel.add(usernameLabel, BorderLayout.NORTH);
        messagePanel.add(contentTextArea, BorderLayout.LINE_START);
        messagesBox.add(messagePanel);
        revalidate();
    }
}
