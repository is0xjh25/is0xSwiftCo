import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientManager {

    private String address;
    private int port;
    private Socket socket;

    public ClientManager(String[] args) {

        ClientArgs clientArgs = new ClientArgs();
        // parse parameters.
        CmdLineParser parser = new CmdLineParser(clientArgs);
        try {
            parser.parseArgument(args);
            address = clientArgs.getAddress();
            port = clientArgs.getPort();
        } catch (CmdLineException e) {
            System.out.println("[ERROR:clientManager] " + e.getMessage());
            parser.printUsage(System.out);
            System.exit(0);
        }
    }

    public void run() {
        try {
            socket = new Socket(address, port);
            Thread thread = new Thread(new ClientProcessor(socket));
            thread.start();
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Unknown host.", "CONNECTION ERROR", JOptionPane.ERROR_MESSAGE);
            System.out.println("[ERROR:UnknownHostException] " +  e.getMessage() + ".");
        } catch (SocketException e){
            JOptionPane.showMessageDialog(null, "Failed to establish connection.", "CONNECTION ERROR", JOptionPane.ERROR_MESSAGE);
            System.out.println("[ERROR:SocketException] " + e.getMessage() + ".");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection failed.", "CONNECTION ERROR", JOptionPane.ERROR_MESSAGE);
            System.out.println("[ERROR:IOException] " + e.getMessage() + ".");
        }
    }

    /* MAIN FUNCTION */
    public static void main(String[] args) {
        ClientManager cwb = new ClientManager(args);
        System.out.println("[SUCCEED] SwiftCo client has been initiated.");
        cwb.run();
    }
}
