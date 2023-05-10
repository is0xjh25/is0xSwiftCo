import org.kohsuke.args4j.Option;

public class ServerArgs {
    @Option(name = "-p", required = true, usage = "Server Port number")
    private int port;
    public int getPort() {
        return port;
    }
}
