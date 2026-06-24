package com.jagrosh.jmusicbot.settings;

import com.jagrosh.jmusicbot.utils.OtherUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStorage implements StorageBackend
{
    private static final Logger LOG = LoggerFactory.getLogger("FileStorage");
    private static final String FILE_NAME = "serversettings.json";
    private final Path path;

    public FileStorage()
    {
        this.path = OtherUtil.getPath(FILE_NAME);
    }

    @Override
    public String read()
    {
        try
        {
            return new String(Files.readAllBytes(path));
        }
        catch (NoSuchFileException e)
        {
            return null;
        }
        catch (IOException e)
        {
            LOG.warn("Failed to read settings file: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void write(String data)
    {
        try
        {
            Files.write(path, data.getBytes());
        }
        catch (IOException e)
        {
            LOG.warn("Failed to write settings file: {}", e.getMessage());
        }
    }

    public Path getPath()
    {
        return path;
    }
}
