import org.json.JSONObject;
import java.util.HashMap;
import java.util.Objects;

public class Room {
    private final String roomID;
    private final ServerProcessor manager;
    private final HashMap<String, ServerProcessor> userList;
    private JSONObject encodeWhitBoard;

    // only manager can create a new room
    public Room(String roomID, ServerProcessor manager) {
        this.roomID = roomID;
        this.manager = manager;
        userList = new HashMap<>();
        encodeWhitBoard = new JSONObject();
        userList.put(manager.getUsername(), manager);
    }

    public boolean isUserExist(String username) {
        return userList.containsKey(username);
    }
    public void addUser(ServerProcessor participant) {
        userList.put(participant.getUsername(), participant);
    }

    /* GETTERS */
    public String getRoomID() {
        return roomID;
    }
    public ServerProcessor getManager() {
        return manager;
    }
    public HashMap<String, ServerProcessor> getUserList() {
        return userList;
    }
    public JSONObject getEncodeWhitBoard() {
        return encodeWhitBoard;
    }
    public void setEncodeWhitBoard(JSONObject encodeWhitBoard) {
        this.encodeWhitBoard = encodeWhitBoard;
    }

    // override equals function, only be checked by roomID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return roomID.equals(room.roomID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomID);
    }
}
