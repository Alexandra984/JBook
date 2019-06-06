package dao;

import model.FriendRequest;
import model.User;

import java.util.List;

public interface FriendDAO {
    List<User> getFriends(int userId);
    List<FriendRequest> getFriendRequests(int userId);
    List<FriendRequest> getSentFriendRequests(int userId);
    void sendFriendRequest(int fromId, int toId);
    void acceptFriendRequest(int fromId, int toId);
}
