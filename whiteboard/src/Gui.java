import org.json.JSONObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import javax.swing.*;


public class Gui extends JFrame {

    private WhiteBoard whiteBoard;
    private JPanel whiteBoardContainer;
    private JPanel managerBar;
    private JPanel toolBar;
    private JPanel chatBox;
    private JPanel userBox;
    private Socket socket;
    private Boolean isManager;
    private String username;

    public Gui() {
        super("Shared White Board");
        init();
    }

    private void init() {
        initComponents();
        setLayout(new BorderLayout());
        setSize(1000, 600);
        add(toolBar, BorderLayout.NORTH);
        add(managerBar, BorderLayout.SOUTH);
        add(chatBox, BorderLayout.EAST);
        add(userBox, BorderLayout.WEST);
        add(whiteBoardContainer, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setResizable(false);

//        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//        addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                if (isManager) {
//                    managerClose();
//                } else {
//                    clientClose();
//                }
//            }
//        });
    }

    private void initComponents() {
        whiteBoard = new WhiteBoard();
        whiteBoardContainer = new JPanel();
        toolBar = new ToolBar(whiteBoard);
        managerBar = new ManagerBar();
        chatBox = new ChatBox();
        userBox = new UserBox();

        whiteBoard.setPreferredSize(new Dimension(500, 525));
        toolBar.setPreferredSize(new Dimension(500, 50));
        managerBar.setPreferredSize(new Dimension(500, 25));
        chatBox.setPreferredSize(new Dimension(250, 600));
        userBox.setPreferredSize(new Dimension(250, 600));
        whiteBoardContainer.setBackground(Color.WHITE);
        whiteBoard.setPreferredSize(new Dimension(500, 500));
        whiteBoardContainer.add(whiteBoard);
    }

//    private void managerClose() {
//        int ans = JOptionPane.showConfirmDialog(
//                null,
//                "Do you want to leave the share white board?",
//                "CLOSED APP",
//                JOptionPane.YES_NO_OPTION);
//
//        if (ans == JOptionPane.YES_OPTION) {
//            try  {
//                JSONObject json = new JSONObject();
//                json.put("header", "quit");
//                json.put("body", "null");
//                OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
//                writer.write(json.toString() + "\n");
//                writer.flush();
//            } catch (UnsupportedEncodingException e) {
//                System.out.println(e.getMessage());
//            } catch (IOException e) {
//                System.out.println(e.getMessage());
//            }
//            System.out.println("Manager quit.");
//            System.exit(0);
//        }
//    }
//    private void clientClose() {
//        int ans = JOptionPane.showConfirmDialog (
//                null,
//                "Do you want to leave the shared white board?",
//                "CLOSED APP",
//                JOptionPane.YES_NO_OPTION);
//
//        if (ans == JOptionPane.YES_OPTION) {
//            try {
//                JSONObject json = new JSONObject();
//                json.put("header", "exit");
//                json.put("body", username);
//                OutputStreamWriter writer;
//                writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
//                writer.write(json.toString() + "\n");
//                writer.flush();
//            } catch (UnsupportedEncodingException e) {
//                System.out.println(e.getMessage());
//            } catch (IOException e) {
//                System.out.println(e.getMessage());
//            }
//
//            System.exit(0);
//        }
//    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Gui().setVisible(true);
            }
        });
    }
}
