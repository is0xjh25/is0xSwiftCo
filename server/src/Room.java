import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class Room {
    private final String roomID;
    private final User manager;
    private final HashSet<User> userList;
    private JSONObject encodeWhitBoard;


    // only manager can create a new room
    public Room(String roomID, User manager) {
        this.roomID = roomID;
        this.manager = manager;
        userList = new HashSet<>();
        encodeWhitBoard = new JSONObject();
        userList.add(manager);
    }

    public boolean isUserExist(String username) {
        for (User u : userList) {
            if (u.getUsername().equals(username)) return true;
        }
        return false;
    }

    public void addUser(User user) {
        userList.add(user);
    }

    /* GETTERS */
    public String getRoomID() {
        return roomID;
    }
    public User getManager() {
        return manager;
    }
    public HashSet<User> getUserList() {
        return userList;
    }
    public JSONObject getEncodeWhitBoard() {
        return encodeWhitBoard;
    }
    public void setEncodeWhitBoard(JSONObject encodeWhitBoard) {
        this.encodeWhitBoard = encodeWhitBoard;
    }
    public User findUser(String username) {
        for (User u : userList) {
            if (u.getUsername().equals(username)) return u;
        }
        return null;
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
