import org.json.JSONObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ManagerBar extends JPanel implements ActionListener {
    private static final String WEBSITE = "https://is0xjh25.github.io";
    private static final String EMAIL = "mailto:is0.jimhsiao@gmail.com?subject=Problem%20With%20is0xCollectiveDict";
    private final ClientProcessor cp;
    private final WhiteBoard whiteBoard;
    private JFileChooser fileChooser;
    private String fileName;

    public ManagerBar(ClientProcessor cp, WhiteBoard whiteBoard) {
        this.cp = cp;
        this.whiteBoard = whiteBoard;
        init();
    }

    private void init() {
        setBackground(Color.DARK_GRAY);
        setLayout(new BorderLayout());

        JButton menuButton = new JButton("MENU");
        menuButton.setFont(new Font("Mono", Font.BOLD, 10));
        menuButton.addActionListener(this);
        menuButton.setActionCommand(menuButton.getName());

        JButton openButton = new JButton("OPEN");
        openButton.setFont(new Font("Mono", Font.BOLD, 10));
        openButton.addActionListener(this);
        openButton.setActionCommand(openButton.getName());

        JButton saveButton = new JButton("SAVE");
        saveButton.setFont(new Font("Mono", Font.BOLD, 10));
        saveButton.addActionListener(this);
        saveButton.setActionCommand(saveButton.getName());

        JButton saveAsButton = new JButton("SAVE AS");
        saveAsButton.setFont(new Font("Mono", Font.BOLD, 10));
        saveAsButton.addActionListener(this);
        saveAsButton.setActionCommand(saveAsButton.getName());

        JButton newOneButton = new JButton("NEW ONE");
        newOneButton.setFont(new Font("Mono", Font.BOLD, 10));
        newOneButton.addActionListener(this);
        newOneButton.setActionCommand(newOneButton.getName());

        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setBackground(Color.DARK_GRAY);

        buttonsPanel.add(menuButton, new GridBagConstraints());
        if (cp.getManager()) {
            buttonsPanel.add(openButton, new GridBagConstraints());
            buttonsPanel.add(saveButton, new GridBagConstraints());
            buttonsPanel.add(saveAsButton, new GridBagConstraints());
            buttonsPanel.add(newOneButton, new GridBagConstraints());
        }

        // footers
        JLabel reportLabel = new JLabel("Find a problem? Tell us.", SwingConstants.LEFT);
        reportLabel.setBorder(new EmptyBorder(0,25,0,0));
        reportLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        reportLabel.setForeground (Color.GRAY);
        reportLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendMail(reportLabel);
        JLabel copyrightLabel = new JLabel("Developed by is0xjh25©", SwingConstants.RIGHT);
        copyrightLabel.setBorder(new EmptyBorder(0,0,0,25));
        copyrightLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        copyrightLabel.setForeground (Color.GRAY);
        copyrightLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goWebsite(copyrightLabel);

        add(reportLabel, BorderLayout.WEST);
        add(buttonsPanel, BorderLayout.CENTER);
        add(copyrightLabel, BorderLayout.EAST);

        // file chooser set up
        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image (*.jpg)", "jpg", "jpeg"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image (*.png)", "png"));
    }

    // warning when white board closed without saving
    private void newWhiteBoard() {
        if (whiteBoard.isModified()) {
            int res = JOptionPane.showConfirmDialog(whiteBoard, "Want to save changes before you create a new one?", "SAVE CHANGES", JOptionPane.YES_NO_CANCEL_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                save();
            } else if (res == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        fileName = null;
        whiteBoard.setBufferImage(null);
        whiteBoard.resetState();
        whiteBoard.paint(whiteBoard.getGraphics());
        whiteBoard.repaint();
        whiteBoard.sendBufferImage();
    }

    private void open() {
        int res = fileChooser.showOpenDialog(whiteBoard);
        try {
            if (res == JFileChooser.APPROVE_OPTION) {
                whiteBoard.resetState();
                BufferedImage img = ImageIO.read(fileChooser.getSelectedFile());
                whiteBoard.setBufferImage(img);
                whiteBoard.setG2d(img);
                whiteBoard.paint(whiteBoard.getGraphics());
                whiteBoard.repaint();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        whiteBoard.setModified(false);
        whiteBoard.sendBufferImage();
    }

    public void save( ) {
        if (fileName == null || fileName.isEmpty()) {
            int res = fileChooser.showSaveDialog(whiteBoard);
            if (res == JFileChooser.APPROVE_OPTION) {
                fileName = fileChooser.getSelectedFile().getAbsolutePath();
            } else if (res == JFileChooser.CANCEL_OPTION) {
                return;
            }
        }

        File file = new File(fileName);
        String format = fileName.substring(fileName.lastIndexOf('.') + 1);

        if (format.equalsIgnoreCase("png")) {
            try {
                ImageIO.write(whiteBoard.getBufferImage(), format, file);
                whiteBoard.setModified(false);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            if (!(format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg"))) {
                file = new File(fileName.concat(".jpg"));
            }
            try {
                ImageIO.write(whiteBoard.getBufferImage(), "jpg", file);
                whiteBoard.setModified(false);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void saveAs( ) {
        fileName = null;
        save();
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "MENU" -> leftRoom();
            case "OPEN" -> open();
            case "SAVE" -> save();
            case "SAVE AS" -> saveAs();
            case "NEW ONE" -> newWhiteBoard();
        }
    }

    private void leftRoom() {
        int ans = JOptionPane.showConfirmDialog (
                cp.getWhiteBoardManager(),
                "Do you want to leave the room #" + cp.getRoomID() + " ?",
                "LEAVE ROOM",
                JOptionPane.YES_NO_OPTION);

        if (ans == JOptionPane.YES_OPTION) {
            if (whiteBoard.isModified() && cp.getManager()) {
                int res = JOptionPane.showConfirmDialog(whiteBoard, "Save changes before you leave?", "SAVE CHANGES", JOptionPane.YES_NO_CANCEL_OPTION);
                if (res == JOptionPane.YES_OPTION) save();
                if (res == JOptionPane.CANCEL_OPTION) return;
            }
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
            cp.getWhiteBoardManager().setMenu();
        }
    }

    /* HELPER FUNCTIONS */
    private void goWebsite(JLabel website) {
        website.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(WEBSITE));
                } catch (URISyntaxException | IOException ex) {
                    System.out.println("[ERROR:goWebsite] -> " + ex.getMessage() + ".");
                }
            }
        });
    }

    private void sendMail(JLabel contact) {
        contact.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().mail(new URI(EMAIL));
                } catch (URISyntaxException | IOException ex) {
                    System.out.println("[ERROR:SendMail] -> " + ex.getMessage() + ".");
                }
            }
        });
    }
}

