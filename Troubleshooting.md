## ⚠ Music doesn't play if I set the volume to something other than 100!
If music stops playing when you change the volume, this means that an internal library could not be loaded. This means you are either using an unsupported java version or the wrong jar file.
* **Check your Java version** - You might be running an unsupported version. Try re-installing the latest 64-bit version of java.
* **Check your .jar version** - For example, if you're running on Windows, you need _either_ the `JMusicBot-X.Y.Z.jar` or the `JMusicBot-X.Y.Z-Windows.jar` (replacing X.Y.Z with the latest version)

## ⚠ Music never starts playing!
If the bot runs, but when you start to play music it never starts and is stuck at `0:00`, try some of these potential fixes:
* **Check your Java version** - You can run `java -version` at the command line to check your Java version. This bot requires JDK 1.8 or higher. OpenJDK _may_ work but is not guaranteed to provide full functionality. Also, for Windows user, make sure you are running 64-bit Java.
* **Check your hosting** - If you are hosting the bot on a VPS (Virtual Private Server), check which company is hosting it. Discord blocks all voice communication from Hetzner, so if your VPS is from Hetzner, you will not be able to host this music bot nor any other music bot from your VPS.
* **Check the logs** - There are a few things you can look for in the logs, but generally something like `Request Timed Out` means that the network connection that the bot has may be struggling and possibly unable to support streaming music.
* **Change your Discord voice region** - Discord frequently makes changes on their end which can cause issues and/or disconnects for JMusicBot. Try changing the region to see if that helps.

## ⚠ The bot is looking for the config file in an unexpected location!
This can sometimes happen on Windows. Make sure:
* **Run the bot from a writable folder** - Use a folder in your 'Documents', not from your Desktop.
* **Double-Click**, don't use 'Open With'
* **Use Command Prompt or Powershell** in the folder with the jar and use `java -jar JMusicBot-VERSION_HERE.jar` (replacing the version correctly)

## ⚠ Error when loading from YouTube (429)
If you see an error similar to "Error when loading from YouTube 429" when attempting to play songs from YouTube, it means that YouTube has blocked your bot's IP. Try one of these potential solutions, and see [here](https://github.com/jagrosh/MusicBot/issues/305) for more information:
* **Change IP addresses** - If you're hosting at home, you may be able to simply turn your router off and back on again. Some online hosts may offer the option to change your IP or to route through a different one.
* **Turn the bot off for several days** - Keeping the bot offline (or only playing songs from non-youtube sources) for several days will allow YouTube to eventually remove your IP ban.