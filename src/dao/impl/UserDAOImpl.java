package dao.impl;

import dao.UserDAO;
import dao.exception.NotFoundException;
import model.Post;
import model.User;
import service.DbService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    private DbService dbServiceInstance;

    public UserDAOImpl() {
        dbServiceInstance = DbService.getInstance();
    }

    @Override
    public User getUserById(int id) throws SQLException, NotFoundException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("select * from user where id=?");
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();
        if (!resultSet.next())
            throw new NotFoundException("User not found");

        String username = resultSet.getString(2);
        String hashedPassword = resultSet.getString(3);
        String firstName = resultSet.getString(4);
        String lastName = resultSet.getString(5);
        String bio = resultSet.getString(6);
        String profileImagePath = resultSet.getString(7);

        User result = new User(id, username, hashedPassword, firstName, lastName, bio, profileImagePath);

        dbServiceInstance.returnConnection(connection);
        return result;
    }

    @Override
    public List<User> getUsers() throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("select * from user");
        ResultSet resultSet = statement.executeQuery();

        List<User> result = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            String username = resultSet.getString(2);
            String hashedPassword = resultSet.getString(3);
            String firstName = resultSet.getString(4);
            String lastName = resultSet.getString(5);
            String bio = resultSet.getString(6);
            String profileImagePath = resultSet.getString(7);
            result.add(new User(id, username, hashedPassword, firstName, lastName, bio, profileImagePath));
        }

        dbServiceInstance.returnConnection(connection);
        return result;
    }

    @Override
    public List<Post> getPosts(int userId) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("select * from post where user_id=?");
        statement.setInt(1, userId);
        ResultSet resultSet = statement.executeQuery();

        List<Post> posts = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            Date date = resultSet.getDate(3);
            String content = resultSet.getString(4);
            posts.add(new Post(id, userId, date, content));
        }

        dbServiceInstance.returnConnection(connection);
        return posts;
    }

    @Override
    public void updateProfileImage(int userId, String path) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("update user set profile_image=? where id=?");
        statement.setString(1, path);
        statement.setInt(2, userId);
        statement.executeUpdate();
        dbServiceInstance.returnConnection(connection);

    }

    @Override
    public void createPost(int userId, Post post) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("insert into post values(NULL, ?, ?, ?)",
                new int[]{1});
        statement.setInt(1, userId);
        statement.setDate(2, new java.sql.Date(post.getDate().getTime()));
        statement.setString(3, post.getContent());
        statement.executeUpdate();

        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next())
            post.setId(generatedKeys.getInt(1));

        dbServiceInstance.returnConnection(connection);
    }

    @Override
    public void createUser(User user) throws SQLException {
        Connection connection = dbServiceInstance.getConnection();
        PreparedStatement statement = connection.prepareStatement("insert into user values(NULL, ?, ?, ?, ?, ?, ?)",
                new int[]{1});
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getHashedPassword());
        statement.setString(3, user.getFirstName());
        statement.setString(4, user.getLastName());
        statement.setString(5, user.getBio());
        statement.setString(6, user.getProfileImagePath());
        statement.executeUpdate();

        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next())
            user.setId(generatedKeys.getInt(1));

        dbServiceInstance.returnConnection(connection);
    }

    public static void main(String[] args) throws NotFoundException, SQLException {
        UserDAO userDAO = new UserDAOImpl();
        User user = userDAO.getUserById(1);
        System.out.println(user.getUsername() + " " + user.getFirstName() + " " + user.getLastName());

        User user1 = new User(8, "user1", "8287458823facb8ff918dbfabcd22ccb",
                "a", "a", "a", "path");
//        userDAO.createUser(user1);
        System.out.println(user1.getId());

        userDAO.updateProfileImage(user1.getId(), "path1");
        List<User> users = userDAO.getUsers();
        System.out.println(users.size());

        userDAO.createPost(user1.getId(), new Post(null, user1.getId(), new Date(), "bla bla"));
        userDAO.createPost(user1.getId(), new Post(null, user1.getId(), new Date(), "bla bla 2"));
        List<Post> posts = userDAO.getPosts(user1.getId());
        for (Post post : posts)
            System.out.println(post.getContent());
    }
}
