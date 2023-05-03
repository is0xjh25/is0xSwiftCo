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
    JPanel header;
    JPanel messages;
    Box messagesBox;
    JScrollPane messagesScroll;
    JPanel text;

    public ChatBox() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setHeader("1234");
        setMessages();
        setText();
        setBorder(new MatteBorder(5, 5, 5, 5, Color.LIGHT_GRAY));
        add(header, BorderLayout.NORTH);
        add(messagesScroll, BorderLayout.CENTER);
        add(text, BorderLayout.SOUTH);
    }

    private void setHeader(String roomID) {
        header = new JPanel(new BorderLayout());
        header.setBackground(Color.DARK_GRAY);
        header.setPreferredSize(new Dimension(250, 75));
        header.setBorder(new EmptyBorder(5, 0, 5, 0));
        JLabel room_info = new JLabel("ROOM ID: #" + roomID, SwingConstants.CENTER);
        room_info.setForeground(Color.WHITE);
        room_info.setFont(new Font("Mono", Font.BOLD, 16));
        JLabel user_info = new JLabel("Participant", SwingConstants.CENTER);
        user_info.setForeground(Color.LIGHT_GRAY);
        user_info.setFont(new Font("Mono", Font.ITALIC, 14));
        JLabel username = new JLabel("Jim", SwingConstants.CENTER);
        username.setForeground(Color.WHITE);
        username.setFont(new Font("Mono", Font.BOLD, 20));
        header.add(room_info, BorderLayout.NORTH);
        header.add(user_info, BorderLayout.CENTER);
        header.add(username, BorderLayout.SOUTH);
    }

    private void setMessages() {
        String text1 = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
        String text2 = "aasd";

        messages = new JPanel(new BorderLayout());
        messagesBox = Box.createVerticalBox();
        messagesBox.setBackground(Color.DARK_GRAY);
        messages.add(messagesBox, BorderLayout.PAGE_START);
        messagesScroll = new JScrollPane(messages);
        messagesScroll.setBorder(new MatteBorder(5, 5, 5, 5, Color.DARK_GRAY));
        messagesScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        messagesScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        addMessage(text2);
        addMessage(text1);
        addMessage(text1);
        addMessage(text1);
    }

    private void addMessage(String text) {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(new EmptyBorder(2, 5, 5, 0));
        JLabel username = new JLabel("[JIM]");
        username.setFont(new Font("Mono", Font.BOLD, 14));
        username.setBorder(new EmptyBorder(5, 5, 5, 5));
        JTextArea content = new JTextArea(text);
        content.setBackground(new Color(238, 238, 238));
        content.setEditable(false);
        content.setFont(new Font("Mono", Font.PLAIN, 14));
        content.setColumns(16);
        content.setLineWrap(true);
        messagePanel.add(username, BorderLayout.NORTH);
        messagePanel.add(content, BorderLayout.LINE_START);
        messagesBox.add(messagePanel);
    }

    private void setText() {
        text = new JPanel();
        text.setLayout(new BorderLayout());
        text.setPreferredSize(new Dimension(250, 100));
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setBackground(Color.DARK_GRAY);
        textArea.setForeground(Color.WHITE);
        JScrollPane textAreaScroll = new JScrollPane(textArea);
        textAreaScroll.setPreferredSize(new Dimension(201, 100));
        textAreaScroll.setBorder(null);
        textAreaScroll.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textAreaScroll.getVerticalScrollBar().setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

//        textArea.addKeyListener(new KeyAdapter() {
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

        text.add(textAreaScroll, BorderLayout.WEST);
        text.add(buttonPanel, BorderLayout.EAST);
    }
}
