import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class Room {
    private final String roomID;
    private final User manager;
    private HashSet<User> userList;

    // only manager can create a new room
    public Room(String roomID, User manager) {
        this.roomID = roomID;
        this.manager = manager;
        userList = new HashSet<>();
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

    // override equals function, only be checked by roomID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return roomID == room.roomID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomID);
    }
}
