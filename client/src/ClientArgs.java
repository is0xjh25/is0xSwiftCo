// is0xSwiftCo
// COMP90015: Assignment2 - Distributed Shared White Board
// Developed By Yun-Chi Hsiao (1074004)
// GitHub: https://github.com/is0xjh25

import org.kohsuke.args4j.Option;

public class ClientArgs {
    @Option(name = "-a", required = true, usage = "Server Address")
    private String address;
    @Option(name = "-p", required = true, usage = "Server Port Number")
    private int port;
    public String getAddress() {
        return address;
    }
    public int getPort() {
        return port;
    }
}
