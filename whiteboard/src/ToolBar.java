import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ToolBar extends JPanel implements ActionListener {

    private WhiteBoard wb;
    private JButton handDraw;
    private JButton text;
    private JButton color;
    private JButton stroke;
    private JToggleButton eraser;
    private JToggleButton line;
    private JToggleButton rectangle;
    private JToggleButton circle;
    private JToggleButton oval;

    public ToolBar(WhiteBoard wb) {
        this.wb = wb;
        init();
    }

    private void init() {
        setBackground(Color.DARK_GRAY);
        setLayout(new GridLayout());
        IconFontSwing.register(FontAwesome.getIconFont());

        handDraw = new JButton("HAND");
        handDraw.setFont(new Font("Mono", Font.BOLD, 14));
        handDraw.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL, 35));
        handDraw.addActionListener(this);
        handDraw.setActionCommand(handDraw.getName());

        eraser = new JToggleButton("ERASER");
        eraser.setFont(new Font("Mono", Font.BOLD, 12));
        eraser.setIcon(IconFontSwing.buildIcon(FontAwesome.ERASER, 35));
        eraser.addActionListener(this);
        eraser.setActionCommand(eraser.getName());

        line = new JToggleButton("LINE");
        line.setFont(new Font("Mono", Font.BOLD, 14));
        line.setIcon(IconFontSwing.buildIcon(FontAwesome.PENCIL_SQUARE_O, 35));
        line.addActionListener(this);
        line.setActionCommand(line.getName());

        rectangle = new JToggleButton("RECT");
        rectangle.setFont(new Font("Mono", Font.BOLD, 14));
        rectangle.setIcon(IconFontSwing.buildIcon(FontAwesome.SQUARE_O, 35));
        rectangle.addActionListener(this);
        rectangle.setActionCommand(rectangle.getName());

        circle = new JToggleButton("CIRCLE");
        circle.setFont(new Font("Mono", Font.BOLD, 12));
        circle.setIcon(IconFontSwing.buildIcon(FontAwesome.CIRCLE_O, 35));
        circle.addActionListener(this);
        circle.setActionCommand(circle.getName());

        oval = new JToggleButton("OVAL");
        oval.setFont(new Font("Mono", Font.BOLD, 14));
        oval.setIcon(IconFontSwing.buildIcon(FontAwesome.CIRCLE_O_NOTCH, 35));
        oval.addActionListener(this);
        oval.setActionCommand(oval.getName());

        color = new JButton("COLOR");
        color.setFont(new Font("Mono", Font.BOLD, 13));
        color.setIcon(IconFontSwing.buildIcon(FontAwesome.PAINT_BRUSH, 35));
        color.addActionListener(this);
        color.setActionCommand(color.getName());

        stroke = new JButton("STROKE");
        stroke.setFont(new Font("Mono", Font.BOLD, 13));
        stroke.setIcon(IconFontSwing.buildIcon(FontAwesome.CIRCLE, 35));
        stroke.addActionListener(this);
        stroke.setActionCommand(stroke.getName());

        text = new JButton("TEXT");
        text.setFont(new Font("Mono", Font.BOLD, 14));
        text.setIcon(IconFontSwing.buildIcon(FontAwesome.FILE_TEXT_O, 35));
        text.addActionListener(this);
        text.setActionCommand(text.getName());

        add(handDraw);
        add(line);
        add(rectangle);
        add(circle);
        add(oval);
        add(text);
        add(color);
        add(stroke);
        add(eraser);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "HAND":
                wb.setState(WhiteBoard.Action.HAND_DRAW);
                break;
            case "LINE":
                wb.setState(WhiteBoard.Action.LINE);
                break;
            case "RECT":
                wb.setState(WhiteBoard.Action.RECTANGLE);
                break;
            case "CIRCLE":
                wb.setState(WhiteBoard.Action.CIRCLE);
                break;
            case "OVAL":
                wb.setState(WhiteBoard.Action.OVAL);
                break;
            case "TEXT":
                wb.setState(WhiteBoard.Action.TEXT);
                break;
            case "COLOR":
                Color pickedColor = JColorChooser.showDialog(this, "Select Pen Color", wb.getColor());
                wb.setColor(pickedColor);
                color.setForeground(pickedColor);
                color.setIcon(IconFontSwing.buildIcon(FontAwesome.PAINT_BRUSH, 35, pickedColor));
                break;
            case "STROKE":
                int oldSize = (int) wb.getPen().getLineWidth();
                int oldCap = wb.getPen().getEndCap();
                int oldJoin = wb.getPen().getLineJoin();

                JPanel dialogPanel = new JPanel();
                dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
                JLabel sizeLabel = new JLabel("Stroke Size");
                sizeLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                JComboBox sizes = new JComboBox<String>();
                sizes.setModel(new DefaultComboBoxModel<String>(new String[] {"2", "8", "10", "12", "15", "20"}));
                sizes.setSelectedIndex(0);
                sizes.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        wb.setPen(new BasicStroke(Float.parseFloat(sizes.getSelectedItem().toString()), wb.getPen().getEndCap(), wb.getPen().getLineJoin()));
                    }
                });

                JLabel capLabel = new JLabel("Stroke Cap");
                capLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                JComboBox caps = new JComboBox<String>();
                caps.setModel(new DefaultComboBoxModel<String>(new String[] {"Butt", "Round", "Square"}));
                caps.setSelectedIndex(0);
                caps.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        wb.setPen(new BasicStroke(wb.getPen().getLineWidth(), caps.getSelectedIndex(), wb.getPen().getLineJoin()));
                    }
                });

                JLabel joinLabel = new JLabel("Stroke Join", SwingConstants.CENTER);
                joinLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                JComboBox joins = new JComboBox<String>();
                joins.setModel(new DefaultComboBoxModel<String>(new String[] {"Miter", "Round", "Bevel"}));
                joins.setSelectedIndex(0);
                joins.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        wb.setPen(new BasicStroke(wb.getPen().getLineWidth(), wb.getPen().getEndCap(), caps.getSelectedIndex()));
                    }
                });

                dialogPanel.add(sizeLabel);
                dialogPanel.add(sizes);
                dialogPanel.add(capLabel);
                dialogPanel.add(caps);
                dialogPanel.add(joinLabel);
                dialogPanel.add(joins);

                int result = JOptionPane.showConfirmDialog(null, dialogPanel,
                        "Adjust Stroke", JOptionPane.OK_CANCEL_OPTION);
                if (result != JOptionPane.OK_OPTION) wb.setPen(new BasicStroke(oldSize, oldCap, oldJoin));
                break;
            case "ERASER":
                wb.setState(WhiteBoard.Action.ERASE);
                break;
        }
    }
}
