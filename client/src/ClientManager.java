// is0xSwiftCo
// COMP90015: Assignment2 - Distributed Shared White Board
// Developed By Yun-Chi Hsiao (1074004)
// GitHub: https://github.com/is0xjh25

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

    public ClientManager(String[] args) {

        ClientArgs clientArgs = new ClientArgs();
        // parse parameters.
        CmdLineParser parser = new CmdLineParser(clientArgs);

        try {
            parser.parseArgument(args);
            address = clientArgs.getAddress();
            port = clientArgs.getPort();
        } catch (CmdLineException e) {
            System.out.println("[ERROR:ClientManager] " + e.getMessage());
            parser.printUsage(System.out);
            System.exit(1);
        }
    }

    public void start() {
        try {
            Socket socket = new Socket(address, port);
            Thread thread = new Thread(new ClientProcessor(socket));
            thread.start();
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Unknown host.", "CONNECTION ERROR", JOptionPane.ERROR_MESSAGE);
            System.out.println("[ERROR:UnknownHostException] " +  e.getMessage() + ".");
            System.exit(-1);
        } catch (SocketException e){
            JOptionPane.showMessageDialog(null, "Failed to establish connection.", "CONNECTION ERROR", JOptionPane.ERROR_MESSAGE);
            System.out.println("[ERROR:SocketException] " + e.getMessage() + ".");
            System.exit(-1);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection failed.", "CONNECTION ERROR", JOptionPane.ERROR_MESSAGE);
            System.out.println("[ERROR:IOException] " + e.getMessage() + ".");
            System.exit(-1);
        }
    }

    /* MAIN FUNCTION */
    public static void main(String[] args) {
        ClientManager cwb = new ClientManager(args);
        System.out.println("[SUCCEED] SwiftCo client has been initiated.");
        cwb.start();
    }
}
