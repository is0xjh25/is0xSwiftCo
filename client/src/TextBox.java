import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TextBox extends JDialog implements ActionListener {
    private final WhiteBoard whiteBoard;
    private final Point inputPoint;
    private JTextField textField;
    private int fontSize;
    private int fontType;
    private String fontFamily;

    public TextBox(WhiteBoard whiteBoard, Point point) {
        this.whiteBoard = whiteBoard;
        this.inputPoint = point;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setTitle("Enter the Text");

        JPanel textPanel = new JPanel();
        textField = new JTextField(30);
        textPanel.add(textField);

        JPanel comboBoxPanel = new JPanel(new FlowLayout());
        // default setting for font
        fontSize = 24;
        fontType = 0;
        fontFamily = "Serif";

        JLabel sizeLabel = new JLabel("Size:");
        JComboBox<Integer> fontSizesComboBox = new JComboBox<Integer>(new Integer[] {24, 36, 48});
        fontSizesComboBox.setSelectedIndex(0);
        fontSizesComboBox.addItemListener(e -> fontSize = (int) fontSizesComboBox.getSelectedItem());

        JLabel typeLabel = new JLabel("Type:");
        JComboBox<String> fontTypesComboBox = new JComboBox<String>(new String[] {"Plain", "Bold", "Italic"});
        fontTypesComboBox.setSelectedIndex(0);
        fontTypesComboBox.addItemListener(e -> fontType = fontTypesComboBox.getSelectedIndex());

        JLabel familyLabel = new JLabel("Family:");
        JComboBox<String> fontFamiliesComboBox = new JComboBox<String>(new String[] {"Serif", "SansSerif", "Mono"});
        fontFamiliesComboBox.setSelectedIndex(0);
        fontFamiliesComboBox.addItemListener(e -> fontFamily = fontFamiliesComboBox.getSelectedItem().toString());

        comboBoxPanel.add(sizeLabel);
        comboBoxPanel.add(fontSizesComboBox);
        comboBoxPanel.add(typeLabel);
        comboBoxPanel.add(fontTypesComboBox);
        comboBoxPanel.add(familyLabel);
        comboBoxPanel.add(fontFamiliesComboBox);

        JPanel buttonPanel = new JPanel();
        JButton enterButton = new JButton("ENTER");
        enterButton.addActionListener(this);
        buttonPanel.add(enterButton);

        add(textPanel, BorderLayout.NORTH);
        add(comboBoxPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 150);
        setLocationRelativeTo(whiteBoard);
//        setLocation(whiteBoard.getX(), whiteBoard.getY());
        setResizable(false);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            Graphics2D g2d = whiteBoard.getG2d();
            g2d.setFont(new Font(fontFamily, fontType, fontSize));
            g2d.drawString(textField.getText(), inputPoint.x, inputPoint.y);
            whiteBoard.repaint();
            whiteBoard.setModified(true);
            whiteBoard.sendBufferImage();
        } catch (NullPointerException ex) {
            System.out.println("[ERROR:actionPerformed]" + ex.getMessage() + ".");
        }
        dispose();
    }
}

