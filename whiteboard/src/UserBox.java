import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.json.JSONObject;

public class UserBox extends JPanel implements ActionListener {

    private List<String> users;
    private Socket managerSocket;
    private boolean isManager;
    private String manager;
    private String username;

//    Socket socket, boolean isManager, String username
    public UserBox() {
        init();
//        this.managerSocket = socket;
        this.isManager = isManager;
        this.username = username;
//        setLayout();
    }

    private void init() {
        setBorder(new MatteBorder(5, 5, 5, 5, Color.LIGHT_GRAY));
        setBackground(Color.DARK_GRAY);
        JLabel title = new JLabel("USER LIST");
        title.setBorder(new EmptyBorder(5, 0, 5, 0));
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Mono", Font.BOLD, 16));
        add(title);
        this.users = new ArrayList<String>();
        manager = "";
    }

    private void setLayout() {
        JPanel mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(280, 530));

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("User List");
        label.setPreferredSize(new Dimension(260, 20));
        label.setFont(label.getFont().deriveFont(24.0f));
        listPanel.add(label);

        for (String user : users) {
            if (user.equals(this.username)) {
                continue;
            }
            JSplitPane splitPane = new JSplitPane();
            splitPane.setPreferredSize(new Dimension(260, 50));
            splitPane.setDividerLocation(180);
            splitPane.setDividerSize(0);

            JLabel nameLabel = new JLabel(user);
            nameLabel.setPreferredSize(new Dimension(160, 25));
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            JScrollPane scrollLabel = new JScrollPane(nameLabel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            splitPane.setLeftComponent(scrollLabel);

            if (this.isManager && !user.equals(manager)) {
                JButton nameButton = new JButton("Remove");
                nameButton.addActionListener(this);
                nameButton.setActionCommand(user);
                splitPane.setRightComponent(nameButton);
            } else {
                splitPane.setRightComponent(new JPanel());
            }

            listPanel.add(splitPane);
        }

        JScrollPane scrollPane = new JScrollPane(listPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        mainPanel.add(scrollPane);
        this.add(mainPanel);
    }

    public void setManagerName(String name) {
        this.manager = name;
    }

    public void setNameList(List<String> users) {
        this.users = users;
        this.removeAll();
        setLayout();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String name = e.getActionCommand();
        JSONObject json = new JSONObject();
        json.put("header", "remove");
        json.put("body", name);
        OutputStreamWriter writer;

        try {
            writer = new OutputStreamWriter(this.managerSocket.getOutputStream(), "UTF-8");
            writer.write(json.toString() + "\n");
            writer.flush();
        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, "Connection Failed", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println(error.getMessage());
            System.exit(0);
        }
    }
}
