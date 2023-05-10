import java.net.Socket;
import java.util.Objects;

public class User {
    private final String username;
    private final boolean isManager;
    private final Socket socket;

    public User(String username, boolean isManager, Socket socket) {
        this.username = username;
        this.isManager = isManager;
        this.socket = socket;
    }

    /* GETTERS */
    public String getUsername() {
        return username;
    }
    public boolean isManager() {
        return isManager;
    }
    public Socket getSocket() {
        return socket;
    }

    // override equals function, only be checked by username
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
