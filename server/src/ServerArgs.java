import org.kohsuke.args4j.Option;

public class ServerArgs {
    @Option(name = "-p", required = true, usage = "Server Port Number")
    private int port;
    public int getPort() {
        return port;
    }
}
