package de.gfz.citydb.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    private static final Logger LOGGER = LogManager.getLogger(DBConnector.class);

    private String userName;
    private String password;
    private String host;
    private String port;
    private String dbName;
    private Connection connection;

    public DBConnector(String userName, String password, String host,
                       String port, String dbName) {
        super();
        this.userName = userName;
        this.password = password;
        this.host = host;
        this.port = port;
        this.dbName = dbName;
    }

    /**
     * Helper method to open database connection.
     *
     * @return
     */
    public Connection openDbConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + host + ":" + port + "/" + dbName, userName,
                    password);
            connection.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Cannot open DB connection!", e);
        }
        return connection;
    }

    public void closeDBConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("Cannot close DB connection.", e);
        }
    }

    public void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            LOGGER.error("Cannot commit changes to DB. Nothing is commited.", e);
        }
    }
}
