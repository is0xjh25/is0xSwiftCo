import org.json.JSONObject;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class WhiteBoard extends Canvas implements MouseListener, MouseMotionListener {

    public enum Action {
        PENDING,
        HAND_DRAW,
        ERASE,
        TEXT,
        LINE,
        CIRCLE,
        RECTANGLE,
        OVAL,
    }

    private Action state;
    private Point currPoint;
    private Point prevPoint;
    private Point startPoint;
    private Point endPoint;
    private int shapeWidth;
    private int shapeHeight;
    private BufferedImage bufferImage;
    private BufferedImage prev;
    private Graphics2D g2d;
    private BasicStroke pen;
    private Color color = Color.BLACK;
    private boolean isModified;
    private Boolean isSent;
//    private final Socket socket;

    public WhiteBoard() {
        init();
        //        this.socket = socket;
        repaint();
    }

    private void init() {
        setBackground(Color.RED);
        addMouseListener(this);
        addMouseMotionListener(this);
        state = Action.PENDING;
        currPoint = null;
        prevPoint = null;
        startPoint = null;
        endPoint = null;
        shapeWidth = 0;
        shapeHeight = 0;
        pen = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        color = Color.BLACK;
        isModified = false;
        isSent = false;
    }

    public void paint(Graphics g) {
        super.paint(g);

        if (bufferImage == null) {
            int w = getWidth();
            int h = getHeight();
            bufferImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            g2d = bufferImage.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, w, h);
        }

        g2d.setColor(color);
        g2d.setStroke(pen);

        switch(state) {
            case PENDING:
            case TEXT:
                break;
            case HAND_DRAW:
                g2d.drawLine(prevPoint.x, prevPoint.y, currPoint.x, currPoint.y);
                isModified = true;
                break;
            case ERASE:
                g2d.setColor(Color.WHITE);
                g2d.drawLine(prevPoint.x, prevPoint.y, currPoint.x, currPoint.y);
                isModified = true;
                break;
            case LINE:
                g2d.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
                isModified = true;
                break;
            case CIRCLE:
                    g2d.drawOval(startPoint.x, startPoint.y, shapeWidth, shapeWidth);
                    isModified = true;
                break;
            case RECTANGLE:
                    g2d.drawRect(startPoint.x, startPoint.y, shapeWidth, shapeHeight);
                    isModified = true;
                break;
            case OVAL:
                    g2d.drawOval(startPoint.x, startPoint.y, shapeWidth, shapeHeight);
                    isModified = true;
                break;
            default:
                g2d.drawString("DRAWING ERROR", 10, 10);
        }

        g.drawImage(bufferImage, 0, 0, null);

        if (isSent) {
            sendBufferImage();
            isSent = false;
        }
    }

    // set width and height of shape
    private void setShapeSize() {
        // for x co-ordinate
        if (startPoint.x > endPoint.x) {
            shapeWidth = startPoint.x - endPoint.x;
            startPoint.x = endPoint.x;
        } else {
            shapeWidth = endPoint.x - startPoint.x;
        }
        // for y co-ordinate
        if (startPoint.y > endPoint.y) {
            shapeHeight = startPoint.y - endPoint.y;
            startPoint.y = endPoint.y;
        } else {
            shapeHeight= endPoint.y - startPoint.y;
        }
    }

    public void resetState() {
        state = Action.PENDING;
        currPoint = null;
        prevPoint = null;
        startPoint = null;
        endPoint = null;
        shapeWidth = 0;
        shapeHeight = 0;
    }

    public void sendBufferImage() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(bufferImage, "jpg", output);
            byte[] imageBytes = output.toByteArray();
            String stringImage = Base64.getEncoder().encodeToString(imageBytes);
            JSONObject json = new JSONObject();
            json.put("header", "drawing_canvas");
            json.put("body", stringImage);
//            OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
//            writer.write(json + "\n");
//            writer.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection Failed.", "CONNECTION ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public void mouseDragged(MouseEvent e) {
        prevPoint = currPoint;
        currPoint = e.getPoint();
        endPoint = e.getPoint();
        if (state == Action.ERASE || state == Action.HAND_DRAW) repaint();
    }

    public void mousePressed(MouseEvent e) {
        currPoint = e.getPoint();
        prevPoint = currPoint;
        startPoint = e.getPoint();
    }

    public void mouseReleased(MouseEvent e) {
        this.isSent = true;
        this.endPoint = e.getPoint();
        repaint();

        switch (state) {
            case TEXT:
                TextBox textbox = new TextBox(this, endPoint);
            break;
            case CIRCLE:
            case OVAL:
            case RECTANGLE:
                setShapeSize();
                break;
            default:
                break;
        }
    }
    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    /* GETTERS & SETTERS */
    public Action getState() {
        return state;
    }
    public void setState(Action state) {
        this.state = state;
    }
    public Point getCurrPoint() {
        return currPoint;
    }
    public void setCurrPoint(Point currPoint) {
        this.currPoint = currPoint;
    }
    public Point getPrevPoint() {
        return prevPoint;
    }
    public void setPrevPoint(Point prevPoint) {
        this.prevPoint = prevPoint;
    }
    public Point getStartPoint() {
        return startPoint;
    }
    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }
    public Point getEndPoint() {
        return endPoint;
    }
    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }
    public int getShapeWidth() {
        return shapeWidth;
    }
    public void setShapeWidth(int shapeWidth) {
        this.shapeWidth = shapeWidth;
    }
    public int getShapeHeight() {
        return shapeHeight;
    }
    public void setShapeHeight(int shapeHeight) {
        this.shapeHeight = shapeHeight;
    }
    public BufferedImage getBufferImage() {
        return bufferImage;
    }
    public void setBufferImage(BufferedImage bufferImage) {
        this.bufferImage = bufferImage;
        repaint();
    }
    public BufferedImage getPrev() {
        return prev;
    }
    public void setPrev(BufferedImage prev) {
        this.prev = prev;
    }
    public Graphics2D getG2d() {
        return g2d;
    }
    public void setG2d(BufferedImage img) {
        this.g2d = (Graphics2D) img.createGraphics();
    }
    public BasicStroke getPen() {
        return pen;
    }
    public void setPen(BasicStroke pen) {
        this.pen = pen;
    }
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public boolean isModified() {
        return isModified;
    }
    public void setModified(boolean modified) {
        isModified = modified;
    }
    public Boolean getSent() {
        return isSent;
    }
    public void setSent(Boolean sent) {
        isSent = sent;
    }
//    public Socket getSocket() {
//        return socket;
//    }
}
