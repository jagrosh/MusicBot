package com.jagrosh.jmusicbot.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisStorage implements StorageBackend
{
    private static final Logger LOG = LoggerFactory.getLogger("RedisStorage");
    private static final String KEY = "lmusicbot:settings";

    private final JedisPool pool;

    public RedisStorage(String host, int port, String password)
    {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(4);
        config.setMaxIdle(2);
        if (password != null && !password.isEmpty())
            this.pool = new JedisPool(config, host, port, 2000, password);
        else
            this.pool = new JedisPool(config, host, port, 2000);
        LOG.info("Connected to Redis at {}:{}", host, port);
    }

    @Override
    public String read()
    {
        try (Jedis jedis = pool.getResource())
        {
            String data = jedis.get(KEY);
            if (data == null || data.isEmpty())
                return null;
            return data;
        }
        catch (Exception e)
        {
            LOG.warn("Failed to read from Redis: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void write(String data)
    {
        try (Jedis jedis = pool.getResource())
        {
            jedis.set(KEY, data);
        }
        catch (Exception e)
        {
            LOG.warn("Failed to write to Redis: {}", e.getMessage());
        }
    }
}
