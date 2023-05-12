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
