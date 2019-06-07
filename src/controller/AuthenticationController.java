package controller;

import dao.UserDAO;
import dao.exception.NotFoundException;
import dao.impl.UserDAOImpl;
import model.User;
import service.AuthenticationService;
import service.ImageService;

import java.io.IOException;
import java.sql.SQLException;

public class AuthenticationController {
    private AuthenticationService authenticationService;
    private UserDAO userDAO;
    private ImageService imageService;

    public AuthenticationController() {
        authenticationService = AuthenticationService.getInstance();
        userDAO = new UserDAOImpl();
        imageService = ImageService.getInstance();
    }

    public User registerUser(String username, String password, String firstName, String lastName,
                             String bio, String profileImagePath) throws SQLException, IOException {
        User user = new User(null, username, authenticationService.hashFunction(password),
                firstName, lastName, bio, null);
        userDAO.createUser(user);
        String uploadPath = imageService.uploadImage(user.getId(), profileImagePath);
        userDAO.updateProfileImage(user.getId(), uploadPath);
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
