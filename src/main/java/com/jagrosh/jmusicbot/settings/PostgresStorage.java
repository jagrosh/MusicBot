package com.jagrosh.jmusicbot.settings;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresStorage implements StorageBackend
{
    private static final Logger LOG = LoggerFactory.getLogger("PostgresStorage");
    private static final String TABLE = "bot_settings";
    private static final int STORE_ID = 1;

    private final HikariDataSource dataSource;

    public PostgresStorage(String host, int port, String database, String user, String password)
    {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + database);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(4);
        config.setMinimumIdle(1);
        this.dataSource = new HikariDataSource(config);
        initTable();
        LOG.info("Connected to PostgreSQL at {}:{}/{}", host, port, database);
    }

    private void initTable()
    {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement())
        {
            stmt.execute("CREATE TABLE IF NOT EXISTS " + TABLE + " ("
                    + "id INTEGER PRIMARY KEY, "
                    + "data TEXT NOT NULL"
                    + ")");
        }
        catch (Exception e)
        {
            LOG.warn("Failed to initialize PostgreSQL table: {}", e.getMessage());
        }
    }

    @Override
    public String read()
    {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT data FROM " + TABLE + " WHERE id = ?"))
        {
            stmt.setInt(1, STORE_ID);
            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                    return rs.getString("data");
            }
        }
        catch (Exception e)
        {
            LOG.warn("Failed to read from PostgreSQL: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void write(String data)
    {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO " + TABLE + " (id, data) VALUES (?, ?) "
                   + "ON CONFLICT (id) DO UPDATE SET data = EXCLUDED.data"))
        {
            stmt.setInt(1, STORE_ID);
            stmt.setString(2, data);
            stmt.executeUpdate();
        }
        catch (Exception e)
        {
            LOG.warn("Failed to write to PostgreSQL: {}", e.getMessage());
        }
    }
}
