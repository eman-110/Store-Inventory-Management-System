package database;

import java.sql.*;

public class ConnectionFactory {
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String url = "jdbc:mysql://localhost:3306/inventory_db";
    private static final String username = "root";
    private static final String password = "Anshaa110";
    private static volatile ConnectionFactory instance;
    private Connection connection;

    private ConnectionFactory(){
        // Lazy initialization - don't create connection in constructor
    }

    public static ConnectionFactory getInstance(){
        ConnectionFactory result = instance;
        if(result == null){
            synchronized (ConnectionFactory.class){
                result = instance;
                if(result == null){
                    instance = result = new ConnectionFactory();
                }
            }
        }
        return result;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            synchronized (this) {
                if (connection == null || connection.isClosed()) {
                    try {
                        Class.forName(driver);
                        connection = DriverManager.getConnection(url, username, password);
                    } catch (ClassNotFoundException e) {
                        throw new SQLException("MySQL JDBC Driver not found. Please add mysql-connector-java.jar to your classpath.", e);
                    } catch (SQLException e) {
                        throw new SQLException("Failed to connect to database: " + e.getMessage(), e);
                    }
                }
            }
        }
        return connection;
    }
}
