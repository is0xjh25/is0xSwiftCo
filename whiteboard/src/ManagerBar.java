import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ManagerBar extends JPanel {

    private static final String WEBSITE = "https://is0xjh25.github.io";
    private static final String EMAIL = "mailto:is0.jimhsiao@gmail.com?subject=Problem%20With%20is0xCollectiveDict";

    private WhiteBoard wb;
    private JFileChooser fileChooser;
    private String fileName;
    private JButton open;
    private JButton save;
    private JButton saveAs;
    private JButton newOne;

    public ManagerBar() {
//        this.wb = wb;
        init();
    }

    private void init() {
        setBackground(Color.DARK_GRAY);
        setLayout(new BorderLayout());
        open = new JButton("OPEN");
        open.setFont(new Font("Mono", Font.BOLD, 10));
        save = new JButton("SAVE");
        save.setFont(new Font("Mono", Font.BOLD, 10));
        saveAs = new JButton("SAVE AS");
        saveAs.setFont(new Font("Mono", Font.BOLD, 10));
        newOne = new JButton("NEW ONE");
        newOne.setFont(new Font("Mono", Font.BOLD, 10));
        JPanel buttons = new JPanel(new GridBagLayout());
        buttons.setBackground(Color.DARK_GRAY);
        buttons.add(open, new GridBagConstraints());
        buttons.add(save, new GridBagConstraints());
        buttons.add(saveAs, new GridBagConstraints());
        buttons.add(newOne, new GridBagConstraints());
        JLabel report = new JLabel("Find a problem? Tell us.", SwingConstants.LEFT);
        report.setBorder(new EmptyBorder(0,25,0,0));
        report.setFont(new Font("Arial", Font.ITALIC, 10));
        report.setForeground (Color.GRAY);
        report.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendMail(report);
        JLabel copyright = new JLabel("Developed by is0xjh25©", SwingConstants.RIGHT);
        copyright.setBorder(new EmptyBorder(0,0,0,25));
        copyright.setFont(new Font("Arial", Font.ITALIC, 10));
        copyright.setForeground (Color.GRAY);
        copyright.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goWebsite(copyright);

        add(buttons, BorderLayout.CENTER);
        add(report, BorderLayout.WEST);
        add(copyright, BorderLayout.EAST);
        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image (*.jpg)", "jpg", "jpeg"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image (*.png)", "png"));
    }

    public void newWhiteBoard() {
        if (wb.isModified()) {
            int res = JOptionPane.showConfirmDialog(wb, "Want to save changes before you create a new one?", "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                save();
            } else if (res == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        wb.setBufferImage(null);
        wb.resetState(wb.getState());
        wb.paint(wb.getGraphics());
        wb.repaint();
        wb.sendBufferImage();
    }

    public void open() {
        int res = fileChooser.showOpenDialog(wb);
        try {
            if (res == JFileChooser.APPROVE_OPTION) {
                Image img = ImageIO.read(fileChooser.getSelectedFile());
                wb.setBufferImage((BufferedImage)img);
                wb.setG2d((BufferedImage)img);
                wb.paint(wb.getGraphics());
                wb.repaint();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        wb.setModified(false);
        wb.sendBufferImage();
    }

    public void save( ) {
        if (fileName == null || fileName.isEmpty()) {
            int res = fileChooser.showSaveDialog(wb);
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
                ImageIO.write(wb.getBufferImage(), format, file);
                wb.setModified(false);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            if (!(format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg"))) {
                file = new File(fileName.concat(".jpg"));
            }
            try {
                ImageIO.write(wb.getBufferImage(), "jpg", file);
                wb.setModified(false);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void saveAs( ) {
        fileName = null;
        save();
    }

    public void exit( ) {
        if (wb.isModified()) {
            int res = JOptionPane.showConfirmDialog(wb, "Save Changes to File?", "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION);

            if (res == JOptionPane.YES_OPTION) {
                save();
                System.exit(0);
            } else if (res == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    public void setCanvas(WhiteBoard wb) {
        this.wb = wb;
    }

    /* HELPER FUNCTIONS */
    private void goWebsite(JLabel website) {
        website.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(WEBSITE));
                } catch (URISyntaxException | IOException ex) {
                    System.out.println("[ERROR] -> " + ex.getMessage() + ".\n");
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
                    System.out.println("[ERROR] -> " + ex.getMessage() + ".\n");
                }
            }
        });
    }
}

