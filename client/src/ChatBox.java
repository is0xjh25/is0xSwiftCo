import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatBox extends JPanel {

    private ClientProcessor cp;
    private Box messagesBox;

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
        JLabel usernameLabel = new JLabel("Jim", SwingConstants.CENTER);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Mono", Font.BOLD, 20));
        headerPanel.add(roomLabel, BorderLayout.NORTH);
        headerPanel.add(userLabel, BorderLayout.CENTER);
        headerPanel.add(usernameLabel, BorderLayout.SOUTH);

        String text1 = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
        String text2 = "aasd";
        JPanel messagesPanel = new JPanel(new BorderLayout());
        messagesBox = Box.createVerticalBox();
        messagesBox.setBackground(Color.DARK_GRAY);
        messagesPanel.add(messagesBox, BorderLayout.PAGE_START);
        JScrollPane messagesScrollPane = new JScrollPane(messagesPanel);
        messagesScrollPane.setBorder(new MatteBorder(5, 5, 5, 5, Color.DARK_GRAY));
        messagesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        messagesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        addMessage(text2);
        addMessage(text1);
        addMessage(text1);
        addMessage(text1);

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
//        inputTextArea.addKeyListener(new KeyAdapter() {
//            public void keyReleased(KeyEvent e) {
//                getPm().getDc().setDefinition(((JTextArea) e.getSource()).getText());
//            }
//        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.DARK_GRAY);
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setPreferredSize(new Dimension(40, 100));
        JButton returnButton = new JButton(IconFontSwing.buildIcon(FontAwesome.COMMENT_O, 20));
        buttonPanel.add(returnButton, BorderLayout.CENTER);

        inputPanel.add(inputScrollPane, BorderLayout.WEST);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(messagesScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    private void addMessage(String textPanel) {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(new EmptyBorder(2, 5, 5, 0));
        JLabel usernameLabel = new JLabel("[JIM]");
        usernameLabel.setFont(new Font("Mono", Font.BOLD, 14));
        usernameLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JTextArea contentTextArea = new JTextArea(textPanel);
        contentTextArea.setBackground(new Color(238, 238, 238));
        contentTextArea.setEditable(false);
        contentTextArea.setFont(new Font("Mono", Font.PLAIN, 14));
        contentTextArea.setColumns(16);
        contentTextArea.setLineWrap(true);
        messagePanel.add(usernameLabel, BorderLayout.NORTH);
        messagePanel.add(contentTextArea, BorderLayout.LINE_START);
        messagesBox.add(messagePanel);
    }
}
