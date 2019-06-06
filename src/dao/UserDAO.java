package dao;

import dao.exception.NotFoundException;
import model.*;

import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
    User getUserById(int id) throws SQLException, NotFoundException;
    List<User> getUsers() throws SQLException;
    List<Post> getPosts(int userId) throws SQLException;
    void updateProfileImage(int userId, String path) throws SQLException;
    void createPost(int userId, Post post) throws SQLException;
    void createUser(User user) throws SQLException;
    User getUserByUsername(String username) throws SQLException, NotFoundException;
}
