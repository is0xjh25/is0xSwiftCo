import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class TextBox extends JDialog implements ActionListener {
    private JPanel textPanel;
    private JPanel buttonPanel;
    private JTextField textField;
    private JButton button;

    private WhiteBoard wb;
    public String text;
    private Point inputPoint;

    public TextBox(WhiteBoard wb, Point point) {
        this.wb = wb;
        this.inputPoint = point;

        setTitle("Enter the Text");
        textPanel = new JPanel();
        textPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        textField = new JTextField(30);
        textPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        textPanel.add(textField);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        button = new JButton("TYPE");
        button.addActionListener(this);
        buttonPanel.add(button);
        getContentPane().setLayout(new GridLayout(2,1));
        getContentPane().add(textPanel);
        getContentPane().add(buttonPanel);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 100);
        setLocationRelativeTo(null);
        setLocation(point.x, point.y);
        setResizable(false);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        text = textField.getText();

        try {
            Graphics2D g2d = wb.getG2d();
            g2d.setFont(new Font("Serif", Font.PLAIN, 24));
            g2d.drawString(text, inputPoint.x, inputPoint.y);
            wb.repaint();
            wb.setModified(true);
            wb.sendBufferImage();
        } catch (NullPointerException e1) {
            System.out.println(e1.getMessage());
        }

        dispose();
    }
}

