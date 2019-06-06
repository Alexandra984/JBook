package dao;

import model.Message;

import java.util.List;

public interface MessageDAO {
    List<Message> getMessages(int fromId, int toId);
    void sendMessage(int fromId, int toId, Message message);
    void setRead(int fromId, int toId);
    List<Integer> getUnread(int userId);
}
