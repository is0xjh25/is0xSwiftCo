// is0xSwiftCo
// COMP90015: Assignment2 - Distributed Shared White Board
// Developed By Yun-Chi Hsiao (1074004)
// GitHub: https://github.com/is0xjh25

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import org.json.JSONObject;

public class ParticipantsBox extends JPanel implements ActionListener {
    private final ClientProcessor cp;
    private List<String> participants;
    private String manager;
    private Box participantsBox;

    public ParticipantsBox(ClientProcessor cp) {
        this.cp = cp;
        participants = new ArrayList<>();
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(new MatteBorder(5, 5, 5, 5, Color.LIGHT_GRAY));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setPreferredSize(new Dimension(250, 75));
        titlePanel.setBackground(Color.DARK_GRAY);
        JLabel titleLabel = new JLabel("Participants", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Mono", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        JPanel participantsPanel = new JPanel(new BorderLayout());
        participantsBox = Box.createVerticalBox();
        participantsPanel.setBackground(Color.DARK_GRAY);
        participantsPanel.add(participantsBox, BorderLayout.PAGE_START);
        JScrollPane participantsScrollPane = new JScrollPane(participantsPanel);
        participantsScrollPane.setBorder(new MatteBorder(5, 5, 5, 5, Color.DARK_GRAY));
        participantsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        participantsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(titlePanel, BorderLayout.NORTH);
        add(participantsScrollPane, BorderLayout.CENTER);
    }

    // update to current users in the room
    public void updateParticipants(ArrayList<String> participants, String manager) {
        this.manager = manager;
        this.participants = participants;
        displayParticipants();
    }

    // redraw the list
    private void displayParticipants() {
        int count = 0;
        participantsBox.removeAll();
        // manager
        JPanel participantPanel = new JPanel(new BorderLayout());
        participantPanel.setPreferredSize(new Dimension(230,40));
        participantPanel.setBackground(Color.LIGHT_GRAY);
        JLabel usernameLabel = new JLabel(manager, SwingConstants.CENTER);
        usernameLabel.setForeground(Color.BLUE);
        usernameLabel.setFont(new Font("Mono", Font.BOLD, 16));
        participantPanel.add(usernameLabel, BorderLayout.CENTER);
        participantsBox.add(participantPanel);

        for (String p : participants) {
            if (p.equals(manager)) continue;
            participantPanel = new JPanel(new BorderLayout());
            participantPanel.setPreferredSize(new Dimension(230,40));
            if (cp.getManager() && !p.equals(manager)) {
                JButton removeButton = new JButton();
                removeButton.setIcon(IconFontSwing.buildIcon(FontAwesome.MINUS, 20));
                removeButton.setPreferredSize(new Dimension(40, 40));
                removeButton.addActionListener(this);
                removeButton.setActionCommand(p);
                participantPanel.add(removeButton, BorderLayout.WEST);
            }
            usernameLabel = new JLabel(p, SwingConstants.CENTER);
            usernameLabel.setFont(new Font("Mono", Font.BOLD, 16));
            usernameLabel.setForeground(Color.WHITE);
            if (p.equals(cp.getUsername()))usernameLabel.setForeground(Color.MAGENTA);
            participantPanel.setBackground(Color.LIGHT_GRAY);
            if (count%2 == 0) participantPanel.setBackground(Color.DARK_GRAY);
            participantPanel.add(usernameLabel, BorderLayout.CENTER);
            participantsBox.add(participantPanel);
            count++;
        }

        participantsBox.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String name = e.getActionCommand();
        int res = JOptionPane.showConfirmDialog(cp.getWhiteBoardManager(), "Do you want to remove "+ name + "?", "KICK OUT", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            try {
                JSONObject req = new JSONObject();
                req.put("header", "kick-out");
                req.put("username", name);
                OutputStreamWriter OSWriter = new OutputStreamWriter(cp.getSocket().getOutputStream(), StandardCharsets.UTF_8);
                OSWriter.write(req + "\n");
                OSWriter.flush();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(cp.getWhiteBoardManager(), ex.getMessage() + ".", "IOException", JOptionPane.ERROR_MESSAGE);
                System.out.println("[ERROR:KickOut] " + ex.getMessage() + ".");
                System.exit(-1);
            }
        }
    }
}
