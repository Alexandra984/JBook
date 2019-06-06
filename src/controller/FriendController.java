package controller;

import dao.FriendDAO;
import dao.exception.NotFoundException;
import dao.impl.FriendDAOImpl;
import model.FriendRequest;
import model.User;
import service.AuthenticationService;

import java.sql.SQLException;
import java.util.List;

public class FriendController {
    private AuthenticationService authenticationService;
    private FriendDAO friendDAO;

    public FriendController() {
        authenticationService = AuthenticationService.getInstance();
        friendDAO = new FriendDAOImpl();
    }

    public List<User> getFriends(String token) throws NotFoundException, SQLException {
        int id = authenticationService.extractIdFromToken(token);
        return friendDAO.getFriends(id);
    }

    public List<FriendRequest> getFriendRequests(String token) throws SQLException {
        int id = authenticationService.extractIdFromToken(token);
        return friendDAO.getFriendRequests(id);
    }

    public List<FriendRequest> getSentFriendRequests(String token) throws SQLException {
        int id = authenticationService.extractIdFromToken(token);
        return friendDAO.getSentFriendRequests(id);
    }

    public void sendFriendRequest(String token, int userId) throws SQLException {
        int id = authenticationService.extractIdFromToken(token);
        friendDAO.sendFriendRequest(id, userId);
    }

    public void acceptFriendRequest(String token, int userId) throws SQLException {
        int id = authenticationService.extractIdFromToken(token);
        friendDAO.acceptFriendRequest(userId, id);
    }
}
