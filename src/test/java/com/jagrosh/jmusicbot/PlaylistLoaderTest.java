/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot;

import com.jagrosh.jmusicbot.entities.Prompt;
import com.jagrosh.jmusicbot.playlist.PlaylistLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for PlaylistLoader covering name parsing and folder operations.
 */
public class PlaylistLoaderTest
{
    private Path tempDir;
    private PlaylistLoader loader;

    @Before
    public void setUp() throws IOException
    {
        tempDir = Files.createTempDirectory("jmusicbot-playlist-test");
        loader = new PlaylistLoader(new StubBotConfig(tempDir.toString()));
    }

    @After
    public void tearDown() throws IOException
    {
        deleteDir(tempDir);
    }

    private static void deleteDir(Path dir) throws IOException
    {
        if (dir != null && Files.exists(dir))
        {
            Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> p.toFile().delete());
        }
    }

    @Test
    public void emptyFolderReturnsEmptyList()
    {
        List<String> names = loader.getPlaylistNames();
        assertNotNull(names);
        assertTrue(names.isEmpty());
    }

    @Test
    public void missingFolderReturnsEmptyListAndCreatesFolder() throws IOException
    {
        deleteDir(tempDir);

        List<String> names = loader.getPlaylistNames();
        assertNotNull(names);
        assertTrue(names.isEmpty());
        // folder should have been created
        assertTrue(Files.exists(tempDir));
    }

    @Test
    public void playlistNameExtensionStripped() throws IOException
    {
        Files.createFile(tempDir.resolve("myfavourites.txt"));
        Files.createFile(tempDir.resolve("chill vibes.txt"));

        List<String> names = loader.getPlaylistNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("myfavourites"));
        assertTrue(names.contains("chill vibes"));
        for (String name : names)
            assertFalse("Name should not contain .txt: " + name, name.contains(".txt"));
    }

    @Test
    public void nonTxtFilesExcluded() throws IOException
    {
        Files.createFile(tempDir.resolve("playlist.txt"));
        Files.createFile(tempDir.resolve("readme.md"));
        Files.createFile(tempDir.resolve("notes.json"));

        List<String> names = loader.getPlaylistNames();
        assertEquals(1, names.size());
        assertEquals("playlist", names.get(0));
    }

    @Test
    public void createAndDeletePlaylist() throws IOException
    {
        loader.createPlaylist("testlist");
        assertTrue(Files.exists(tempDir.resolve("testlist.txt")));

        loader.deletePlaylist("testlist");
        assertFalse(Files.exists(tempDir.resolve("testlist.txt")));
    }

    @Test
    public void getPlaylistUnknownNameReturnsNull()
    {
        PlaylistLoader.Playlist result = loader.getPlaylist("nonexistent");
        assertNull(result);
    }

    @Test
    public void getPlaylistReturnsCorrectData() throws IOException
    {
        loader.createPlaylist("mylist");
        loader.writePlaylist("mylist", "https://example.com/track1\nhttps://example.com/track2");

        PlaylistLoader.Playlist playlist = loader.getPlaylist("mylist");
        assertNotNull(playlist);
        assertEquals("mylist", playlist.getName());
        assertEquals(2, playlist.getItems().size());
        assertTrue(playlist.getItems().contains("https://example.com/track1"));
        assertTrue(playlist.getItems().contains("https://example.com/track2"));
    }

    @Test
    public void pathTraversalNameNotInPlaylistNames()
    {
        List<String> names = loader.getPlaylistNames();
        for (String name : names)
        {
            assertFalse("Playlist name should not contain path separators: " + name,
                name.contains("..") || name.contains("/") || name.contains("\\"));
        }
    }

    /**
     * A minimal BotConfig subclass that returns a custom playlists folder path
     * without requiring a full config file to be loaded.
     */
    private static class StubBotConfig extends BotConfig
    {
        private final String folder;

        StubBotConfig(String folder)
        {
            super(new Prompt("test", null, true, true));
            this.folder = folder;
        }

        @Override
        public String getPlaylistsFolder()
        {
            return folder;
        }
    }
}
