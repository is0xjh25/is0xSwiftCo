import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolBar extends JPanel implements ActionListener {
    private final WhiteBoard whiteBoard;
    private JButton colorButton;

    public ToolBar(WhiteBoard whiteBoard) {
        this.whiteBoard = whiteBoard;
        init();
    }

    private void init() {
        setBackground(Color.DARK_GRAY);
        setLayout(new GridLayout());
        IconFontSwing.register(FontAwesome.getIconFont());

        JButton eraserButton = new JButton("ERASER");
        eraserButton.setFont(new Font("Mono", Font.BOLD, 12));
        eraserButton.setIcon(IconFontSwing.buildIcon(FontAwesome.ERASER, 35));
        eraserButton.addActionListener(this);
        eraserButton.setActionCommand(eraserButton.getName());

        JButton lineButton = new JButton("LINE");
        lineButton.setFont(new Font("Mono", Font.BOLD, 14));
        lineButton.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL_SQUARE_O, 35));
        lineButton.addActionListener(this);
        lineButton.setActionCommand(lineButton.getName());

        JButton rectangleButton = new JButton("RECT");
        rectangleButton.setFont(new Font("Mono", Font.BOLD, 14));
        rectangleButton.setIcon(IconFontSwing.buildIcon(FontAwesome.SQUARE_O, 35));
        rectangleButton.addActionListener(this);
        rectangleButton.setActionCommand(rectangleButton.getName());

        JButton circleButton = new JButton("CIRCLE");
        circleButton.setFont(new Font("Mono", Font.BOLD, 12));
        circleButton.setIcon(IconFontSwing.buildIcon(FontAwesome.CIRCLE_O, 35));
        circleButton.addActionListener(this);
        circleButton.setActionCommand(circleButton.getName());

        JButton ovalButton = new JButton("OVAL");
        ovalButton.setFont(new Font("Mono", Font.BOLD, 14));
        ovalButton.setIcon(IconFontSwing.buildIcon(FontAwesome.CIRCLE_O_NOTCH, 35));
        ovalButton.addActionListener(this);
        ovalButton.setActionCommand(ovalButton.getName());

        JButton handDrawButton = new JButton("HAND");
        handDrawButton.setFont(new Font("Mono", Font.BOLD, 14));
        handDrawButton.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, 35));
        handDrawButton.addActionListener(this);
        handDrawButton.setActionCommand(handDrawButton.getName());

        colorButton = new JButton("COLOR");
        colorButton.setFont(new Font("Mono", Font.BOLD, 13));
        colorButton.setIcon(IconFontSwing.buildIcon(FontAwesome.PAINT_BRUSH, 35));
        colorButton.addActionListener(this);
        colorButton.setActionCommand(colorButton.getName());

        JButton strokeButton = new JButton("STROKE");
        strokeButton.setFont(new Font("Mono", Font.BOLD, 13));
        strokeButton.setIcon(IconFontSwing.buildIcon(FontAwesome.CIRCLE, 35));
        strokeButton.addActionListener(this);
        strokeButton.setActionCommand(strokeButton.getName());

        JButton textButton = new JButton("TEXT");
        textButton.setFont(new Font("Mono", Font.BOLD, 14));
        textButton.setIcon(IconFontSwing.buildIcon(FontAwesome.FILE_TEXT_O, 35));
        textButton.addActionListener(this);
        textButton.setActionCommand(textButton.getName());

        add(lineButton);
        add(rectangleButton);
        add(circleButton);
        add(ovalButton);
        add(textButton);
        add(handDrawButton);
        add(colorButton);
        add(strokeButton);
        add(eraserButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "LINE" -> whiteBoard.setState(WhiteBoard.Action.LINE);
            case "RECT" -> whiteBoard.setState(WhiteBoard.Action.RECTANGLE);
            case "CIRCLE" -> whiteBoard.setState(WhiteBoard.Action.CIRCLE);
            case "OVAL" -> whiteBoard.setState(WhiteBoard.Action.OVAL);
            case "TEXT" -> whiteBoard.setState(WhiteBoard.Action.TEXT);
            case "HAND" -> whiteBoard.setState(WhiteBoard.Action.HAND_DRAW);
            case "COLOR" -> {
                Color pickedColor = JColorChooser.showDialog(this, "Select Pen Color", whiteBoard.getColor());
                whiteBoard.setColor(pickedColor);
                colorButton.setForeground(pickedColor);
                colorButton.setIcon(IconFontSwing.buildIcon(FontAwesome.PAINT_BRUSH, 35, pickedColor));
                if (pickedColor.equals(Color.WHITE)) {
                    colorButton.setForeground(Color.LIGHT_GRAY);
                    colorButton.setIcon(IconFontSwing.buildIcon(FontAwesome.PAINT_BRUSH, 35, Color.LIGHT_GRAY));
                }
            }
            case "STROKE" -> {
                int oldSize = (int) whiteBoard.getPen().getLineWidth();
                int oldCap = whiteBoard.getPen().getEndCap();
                int oldJoin = whiteBoard.getPen().getLineJoin();
                JPanel dialogPanel = new JPanel();
                dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));

                JLabel sizeLabel = new JLabel("Stroke Size");
                sizeLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                JComboBox<String> sizesComboBox = new JComboBox<String>(new String[]{"2", "8", "12", "14", "18", "24"});
                sizesComboBox.setSelectedIndex(0);
                sizesComboBox.addItemListener(e13 -> whiteBoard.setPen(new BasicStroke(Float.parseFloat((String) sizesComboBox.getSelectedItem()), whiteBoard.getPen().getEndCap(), whiteBoard.getPen().getLineJoin())));

                JLabel capLabel = new JLabel("Stroke Cap");
                capLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                JComboBox<String> capsComboBox = new JComboBox<String>(new String[]{"Butt", "Round", "Square"});
                capsComboBox.setSelectedIndex(0);
                capsComboBox.addItemListener(e1 -> whiteBoard.setPen(new BasicStroke(whiteBoard.getPen().getLineWidth(), capsComboBox.getSelectedIndex(), whiteBoard.getPen().getLineJoin())));

                JLabel joinLabel = new JLabel("Stroke Join", SwingConstants.CENTER);
                joinLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                JComboBox<String> joinsComboBox = new JComboBox<String>(new String[]{"Miter", "Round", "Bevel"});
                joinsComboBox.setSelectedIndex(0);
                joinsComboBox.addItemListener(e12 -> whiteBoard.setPen(new BasicStroke(whiteBoard.getPen().getLineWidth(), whiteBoard.getPen().getEndCap(), joinsComboBox.getSelectedIndex())));

                dialogPanel.add(sizeLabel);
                dialogPanel.add(sizesComboBox);
                dialogPanel.add(capLabel);
                dialogPanel.add(capsComboBox);
                dialogPanel.add(joinLabel);
                dialogPanel.add(joinsComboBox);

                // display optional panel
                int result = JOptionPane.showConfirmDialog(null, dialogPanel, "ADJUST STROKE", JOptionPane.OK_CANCEL_OPTION);
                if (result != JOptionPane.OK_OPTION) whiteBoard.setPen(new BasicStroke(oldSize, oldCap, oldJoin));
            }
            case "ERASER" -> whiteBoard.setState(WhiteBoard.Action.ERASE);
        }
    }
}
