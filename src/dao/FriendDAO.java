package dao;

import dao.exception.NotFoundException;
import model.FriendRequest;
import model.User;

import java.sql.SQLException;
import java.util.List;

public interface FriendDAO {
    List<User> getFriends(int userId) throws SQLException, NotFoundException;
    List<FriendRequest> getFriendRequests(int userId) throws SQLException;
    List<FriendRequest> getSentFriendRequests(int userId) throws SQLException;
    void sendFriendRequest(int fromId, int toId) throws SQLException;
    void acceptFriendRequest(int fromId, int toId) throws SQLException;
}
