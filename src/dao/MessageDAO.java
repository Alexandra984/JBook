package dao;

import model.Message;

import java.sql.SQLException;
import java.util.List;

public interface MessageDAO {
    List<Message> getMessages(int fromId, int toId) throws SQLException;
    void sendMessage(int fromId, int toId, String message) throws SQLException;
    void setRead(int fromId, int toId) throws SQLException;
    List<Integer> getUnread(int userId) throws SQLException;
}
