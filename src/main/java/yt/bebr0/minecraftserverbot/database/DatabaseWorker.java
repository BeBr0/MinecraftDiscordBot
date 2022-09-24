package yt.bebr0.minecraftserverbot.database;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseWorker {

    private final static DatabaseWorker instance = new DatabaseWorker();

    public static DatabaseWorker getInstance() {
        return instance;
    }


    private Connection connection;
    private Statement statement;

    public DatabaseWorker() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
            statement = connection.createStatement();
        }
        catch (SQLException ignored) {}
    }

    public void writeUser(String uuid, String discordId) {
        try {
            statement.execute("CREATE TABLE IF NOT EXIST users(uuid TEXT PRIMARY KEY, discord_id TEXT)");

            statement.execute("INSERT INTO users(" + uuid + ", " + discordId + ")");
        }
        catch (SQLException ignored) {}
    }
}
