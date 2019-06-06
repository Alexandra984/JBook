package dao;

import model.*;

import java.util.List;

public interface UserDAO {
    User getUserById(int id);
    List<User> getUsers();
    List<Post> getPosts(int userId);
    void updateProfileImage(int userId, String path);
    void createPost(int userId, Post post);
}
