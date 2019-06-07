package dao.impl;

import dao.MessageDAO;
import model.Message;
import service.DbService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageDAOImpl implements MessageDAO {
    private DbService dbServiceInstance;

    public MessageDAOImpl() {
        dbServiceInstance = DbService.getInstance();
    }

    @Override
    public List<Message> getMessages(int fromId, int toId) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "select * from message where (from_id=? and to_id=?) or (from_id=? and to_id=?) order by date desc, id desc"
        );
        statement.setInt(1, fromId);
        statement.setInt(2, toId);
        statement.setInt(3, toId);
        statement.setInt(4, fromId);
        ResultSet resultSet = statement.executeQuery();

        List<Message> messages = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            int from = resultSet.getInt(2);
            int to = resultSet.getInt(3);
            String content = resultSet.getString(4);
            boolean readStatus = resultSet.getBoolean(5);
            Date date = resultSet.getDate(6);
            messages.add(new Message(id, from, to, content, readStatus, date));
        }

        dbServiceInstance.returnConnection(connection);
        return messages;
    }

    @Override
    public void sendMessage(int fromId, int toId, String message) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("insert into message values(NULL, ?, ?, ?, ?, ?)");
        statement.setInt(1, fromId);
        statement.setInt(2, toId);
        statement.setString(3, message);
        statement.setBoolean(4, false);
        statement.setDate(5, new java.sql.Date((new Date()).getTime()));
        statement.executeUpdate();
        dbServiceInstance.returnConnection(connection);

    }

    @Override
    public void setRead(int fromId, int toId) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("update message set read_status=1 where from_id=? and to_id=?");
        statement.setInt(1, fromId);
        statement.setInt(2, toId);
        statement.executeUpdate();
        dbServiceInstance.returnConnection(connection);
    }

    @Override
    public List<Integer> getUnread(int userId) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("select distinct from_id from message where to_id=? and read_status=0");
        statement.setInt(1, userId);
        ResultSet resultSet = statement.executeQuery();

        List<Integer> lst = new ArrayList<>();
        while (resultSet.next())
            lst.add(resultSet.getInt(1));

        dbServiceInstance.returnConnection(connection);
        return  lst;
    }

    public static void main(String[] args) throws SQLException {
        MessageDAO messageDAO = new MessageDAOImpl();
//        messageDAO.sendMessage(8, 5, "msg1");
//        messageDAO.sendMessage(8, 5, "msg2");
        List<Message> messages = messageDAO.getMessages(8, 5);
        List<Integer> unread = messageDAO.getUnread(5);
        messageDAO.setRead(8, 5);
    }
}
