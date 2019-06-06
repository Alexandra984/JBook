package controller;

import dao.UserDAO;
import dao.exception.NotFoundException;
import dao.impl.UserDAOImpl;
import model.Post;
import model.User;
import service.AuthenticationService;
import service.ImageService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class UserController {
    private AuthenticationService authenticationService;
    private UserDAO userDAO;
    private ImageService imageService;

    public UserController() {
        authenticationService = AuthenticationService.getInstance();
        userDAO = new UserDAOImpl();
        imageService = ImageService.getInstance();
    }

    public User getUserData(String token) throws NotFoundException, SQLException {
        int id = authenticationService.extractIdFromToken(token);
        return userDAO.getUserById(id);
    }

    public User getUserById(int id) throws NotFoundException, SQLException {
        return userDAO.getUserById(id);
    }

    public List<User> getUsers() throws SQLException {
        return userDAO.getUsers();
    }

    public List<Post> getPosts(int id) throws SQLException {
        return userDAO.getPosts(id);
    }

    public void updateProfileImage(String token, String path) throws IOException, SQLException {
        int id = authenticationService.extractIdFromToken(token);
        String uploadPath = imageService.uploadImage(id, path);
        userDAO.updateProfileImage(id, uploadPath);
    }

    public void createPost(String token, String content) throws SQLException {
        int id = authenticationService.extractIdFromToken(token);
        userDAO.createPost(id, new Post(null, id, new Date(), content));
    }
}
