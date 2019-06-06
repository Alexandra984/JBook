package controller;

import dao.UserDAO;
import dao.exception.NotFoundException;
import dao.impl.UserDAOImpl;
import model.User;
import service.AuthenticationService;

import java.sql.SQLException;

public class AuthenticationController {
    private AuthenticationService authenticationService;
    private UserDAO userDAO;

    public AuthenticationController() {
        authenticationService = AuthenticationService.getInstance();
        userDAO = new UserDAOImpl();
    }

    public User registerUser(String username, String password, String firstName, String lastName,
                             String bio, String profileImagePath) throws SQLException {
        User user = new User(null, username, authenticationService.hashFunction(password),
                firstName, lastName, bio, profileImagePath);
        userDAO.createUser(user);
        return user;
    }

    public String authenticateUser(String username, String password) throws NotFoundException, SQLException {
        User user = userDAO.getUserByUsername(username);
        String token = authenticationService.authenticateUser(user, password);
        if (token == null)
            throw new IllegalArgumentException("Invalid password");
        return token;
    }
}
