package controller;

import dao.MessageDAO;
import dao.impl.MessageDAOImpl;
import model.Message;
import service.AuthenticationService;

import java.sql.SQLException;
import java.util.List;

public class MessageController {
    private AuthenticationService authenticationService;
    private MessageDAO messageDAO;

    public MessageController() {
        authenticationService = AuthenticationService.getInstance();
        messageDAO = new MessageDAOImpl();
    }

    public List<Message> getMessages(String token, int userId) throws SQLException {
        int id = authenticationService.extractIdFromToken(token);
        return messageDAO.getMessages(id, userId);
    }

    public void sendMessage(String token, int userId, String message) throws SQLException {
        int id = authenticationService.extractIdFromToken(token);
        messageDAO.sendMessage(id, userId, message);
    }

    public void setRead(String token, int userId) throws SQLException {
        int id = authenticationService.extractIdFromToken(token);
        messageDAO.setRead(userId, id);
    }

    public List<Integer> getUnread(String token) throws SQLException {
        int id = authenticationService.extractIdFromToken(token);
        return messageDAO.getUnread(id);
    }
}
