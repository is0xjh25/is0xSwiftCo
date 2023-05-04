import java.awt.*;
import java.awt.event.*;
import java.io.OutputStreamWriter;
import java.net.JarURLConnection;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import org.json.JSONObject;

public class ParticipantsBox extends JPanel implements ActionListener {

    private Socket socket;
    private final boolean isManager;
    private final String username;
    private String manager;
    private List<String> participants;
    private Box participantsBox;

//    Socket socket, boolean isManager, String username
    public ParticipantsBox(Boolean isManager, String username) {
        //        this.managerSocket = socket;
        this.isManager = isManager;
        this.username = username;
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

    public void updateParticipants(ArrayList<String> participants) {
        this.participants = participants;
        displayParticipants();
    }

    private void displayParticipants() {
        int count = 0;
        for (String p : participants) {
            if (p.equals(username)) continue;
            JPanel participantPanel = new JPanel(new BorderLayout());
            participantPanel.setPreferredSize(new Dimension(230,40));
            if (isManager) {
                JButton removeButton = new JButton();
                removeButton.setIcon(IconFontSwing.buildIcon(FontAwesome.MINUS, 20));
                removeButton.setPreferredSize(new Dimension(40, 40));
                removeButton.addActionListener(this);
                removeButton.setActionCommand(p);
                participantPanel.add(removeButton, BorderLayout.WEST);
            }

            JLabel username = new JLabel(p, SwingConstants.CENTER);
            username.setFont(new Font("Mono", Font.BOLD, 16));
            username.setForeground(Color.WHITE);
            participantPanel.setBackground(Color.DARK_GRAY);
            if (count%2 == 0) participantPanel.setBackground(Color.GRAY);
            participantPanel.add(username, BorderLayout.CENTER);
            participantsBox.add(participantPanel);
            count++;
        }
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String name = e.getActionCommand();
        JSONObject json = new JSONObject();
        json.put("header", "remove");
        json.put("body", name);
        OutputStreamWriter OSWriter;

        int res = JOptionPane.showConfirmDialog(null, "Do you want to remove "+name+"?", "KICK OUT", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            try {
                OSWriter = new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8);
                OSWriter.write(json + "\n");
                OSWriter.flush();
            } catch (Exception error) {
                JOptionPane.showMessageDialog(null, "Connection Failed.", "CONNECTION ERROR", JOptionPane.ERROR_MESSAGE);
                System.out.println(error.getMessage());
                System.exit(0);
            }
        }
    }
}
