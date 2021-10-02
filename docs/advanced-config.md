---
title: Advanced Config Options
description: "Configuration settings for advanced JMusicBot users"
---

## Specify a different config file
Setting a different file to use as the config file is possible via the `-Dconfig=/path/to/config.file` command-line option. For example:
```bash
java -Dconfig=alternate_config.txt -jar JMusicBot.jar
```
will run the bot, loading from `alternate_config.txt` instead of `config.txt`.


## Specify config options from the command line
Similar to the `-Dconfig` option, any setting in the config file can also be set from the command line. For example, to set the prefix from the command line (instead of from the config), you would use `-Dprefix="!!"` (values need to be quoted if they contain spaces or some special characters). For example:
```bash
java -Dprefix="!" -jar JMusicBot.jar
```


## Specify config options from environment variables
To use environment variables for the config, there are two options. For the following examples, assume that a prefix has been set to the environment variable `CUSTOM_PREFIX`.
### From the command line
To use environment variables from the command line, use the same system from above, but substitute in a resolved variable name. For example:
```bash
java -Dprefix="$CUSTOM_PREFIX" -jar JMusicBot.jar
```
### In the config file
To use an environment in the config file, specify it as follows
```hocon
// this is in the config file
prefix = ${CUSTOM_PREFIX}
```