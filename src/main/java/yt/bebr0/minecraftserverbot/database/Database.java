package yt.bebr0.minecraftserverbot.database;

import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.UUID;

public class Database {

    private final static Database instance = new Database();

    public static Database getInstance() {
        return instance;
    }


    private Connection connection;
    private Statement statement;

    public Database() {
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

    @Nullable
    public BotUser getUserByMinecraftID(UUID minecraftId) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE uuid = " + minecraftId.toString());

            if (resultSet.getFetchSize() != 0) {
                return new BotUser(resultSet.getString(0), resultSet.getString(1));
            }
            else return null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    @Nullable
    public BotUser getUserByDiscordID(String discordId) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE discord_id = " + discordId);

            if (resultSet.getFetchSize() != 0) {
                return new BotUser(resultSet.getString(0), resultSet.getString(1));
            }
            else return null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    public class BotUser {

        private String minecraftId;
        private String discordId;

        private BotUser(String minecraftId, String discordId) {
            this.minecraftId = minecraftId;
            this.discordId = discordId;
        }

        public String getMinecraftId() {
            return minecraftId;
        }

        public void setMinecraftId(String minecraftId) {
            this.minecraftId = minecraftId;
        }

        public String getDiscordId() {
            return discordId;
        }

        public void setDiscordId(String discordId) {
            this.discordId = discordId;
        }
    }
}
