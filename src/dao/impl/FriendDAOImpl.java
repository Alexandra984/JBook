package dao.impl;

import dao.FriendDAO;
import dao.UserDAO;
import dao.exception.NotFoundException;
import model.FriendRequest;
import model.User;
import service.DbService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FriendDAOImpl implements FriendDAO {
    private DbService dbServiceInstance;

    public FriendDAOImpl() {
        dbServiceInstance = DbService.getInstance();
    }

    @Override
    public List<User> getFriends(int userId) throws SQLException, NotFoundException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("select id2 from friends where id1=?");
        statement.setInt(1, userId);
        ResultSet resultSet = statement.executeQuery();

        List<Integer> ids = new ArrayList<>();
        while (resultSet.next())
            ids.add(resultSet.getInt(1));
        dbServiceInstance.returnConnection(connection);

        List<User> friends = new ArrayList<>();
        UserDAO userDAO = new UserDAOImpl();
        for (Integer id : ids)
            friends.add(userDAO.getUserById(id));
        return friends;
    }

    @Override
    public List<FriendRequest> getFriendRequests(int userId) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("select * from friend_request where to_id=?");
        statement.setInt(1, userId);
        ResultSet resultSet = statement.executeQuery();

        List<FriendRequest> result = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            Date date = resultSet.getDate(2);
            int fromId = resultSet.getInt(3);
            result.add(new FriendRequest(id, date, fromId, userId));
        }
        dbServiceInstance.returnConnection(connection);

        return result;
    }

    @Override
    public List<FriendRequest> getSentFriendRequests(int userId) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("select * from friend_request where from_id=?");
        statement.setInt(1, userId);
        ResultSet resultSet = statement.executeQuery();

        List<FriendRequest> result = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            Date date = resultSet.getDate(2);
            int toId = resultSet.getInt(4);
            result.add(new FriendRequest(id, date, userId, toId));
        }
        dbServiceInstance.returnConnection(connection);

        return result;
    }

    @Override
    public void sendFriendRequest(int fromId, int toId) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("insert into friend_request values(NULL, ?, ?, ?)");
        statement.setInt(2, fromId);
        statement.setInt(3, toId);
        statement.setDate(1, new java.sql.Date((new Date()).getTime()));
        statement.executeUpdate();
        dbServiceInstance.returnConnection(connection);
    }

    @Override
    public void acceptFriendRequest(int fromId, int toId) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("delete from friend_request where to_id=? and from_id=?");
        statement.setInt(1, toId);
        statement.setInt(2, fromId);
        statement.executeUpdate();

        PreparedStatement statement2 = connection.prepareStatement("insert into friends values(?, ?)");
        statement2.setInt(1, fromId);
        statement2.setInt(2, toId);
        statement2.executeUpdate();

        PreparedStatement statement3 = connection.prepareStatement("insert into friends values(?, ?)");
        statement3.setInt(2, fromId);
        statement3.setInt(1, toId);
        statement3.executeUpdate();

        dbServiceInstance.returnConnection(connection);
    }

    public static void main(String[] args) throws SQLException, NotFoundException {
        FriendDAO friendDAO = new FriendDAOImpl();
//        friendDAO.sendFriendRequest(8, 4);
//        friendDAO.sendFriendRequest(8, 5);
        List<FriendRequest> lst = friendDAO.getFriendRequests(5);
        List<FriendRequest> lst2 = friendDAO.getSentFriendRequests(8);

//        friendDAO.acceptFriendRequest(8, 4);
//        friendDAO.acceptFriendRequest(8, 5);

        List<User> friends = friendDAO.getFriends(8);
    }
}
