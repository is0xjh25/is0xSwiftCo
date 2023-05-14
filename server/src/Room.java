// is0xSwiftCo
// COMP90015: Assignment2 - Distributed Shared White Board
// Developed By Yun-Chi Hsiao (1074004)
// GitHub: https://github.com/is0xjh25

import org.json.JSONObject;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Room {
    private final String roomID;
    private final ServerProcessor manager;
    private final ConcurrentHashMap<String, ServerProcessor> userList;
    private JSONObject encodeWhitBoard;

    // only manager can create a new room
    public Room(String roomID, ServerProcessor manager) {
        this.roomID = roomID;
        this.manager = manager;
        userList = new ConcurrentHashMap<>();
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
    public ConcurrentHashMap<String, ServerProcessor> getUserList() {
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
