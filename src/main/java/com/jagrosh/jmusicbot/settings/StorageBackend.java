package com.jagrosh.jmusicbot.settings;

public interface StorageBackend
{
    String read();

    void write(String data);
}
