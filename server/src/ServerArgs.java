// is0xSwiftCo
// COMP90015: Assignment2 - Distributed Shared White Board
// Developed By Yun-Chi Hsiao (1074004)
// GitHub: https://github.com/is0xjh25

import org.kohsuke.args4j.Option;

public class ServerArgs {
    @Option(name = "-p", required = true, usage = "Server Port Number")
    private int port;
    public int getPort() {
        return port;
    }
}
