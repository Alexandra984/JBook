package service;

import config.Configuration;

import java.sql.*;
import java.util.LinkedList;

public class DbService {
    private static DbService instance = null;

    private static final int INITIAL_POOL_CAPACITY = 10;
    private LinkedList<Connection> pool;

    private DbService() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        pool = new LinkedList<>();
        for (int i = 0; i < INITIAL_POOL_CAPACITY; ++i) {
            try {
                pool.add(DriverManager.getConnection(Configuration.DB_CONNECTION_STRING));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        if (pool.isEmpty())
            pool.add(DriverManager.getConnection(Configuration.DB_CONNECTION_STRING));
        return pool.pop();
    }

    public synchronized void returnConnection(Connection connection) {
        pool.push(connection);
    }

    public static DbService getInstance() {
        if (instance == null)
            instance = new DbService();
        return instance;
    }

    public static void main(String[] args) {
        try {
            Connection connection = getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from user");
            while (resultSet.next()) {
                String username = resultSet.getString(2);
                System.out.println(username);
            }
            getInstance().returnConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
