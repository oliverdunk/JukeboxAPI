# JukeboxAPI [![Build Status](https://travis-ci.org/oliverdunk/JukeboxAPI.svg?branch=master)](https://travis-ci.org/oliverdunk/JukeboxAPI)
Java API and plugin for [Jukebox](https://mcjukebox.net), my web based music player.

# Usage
Please see the [SpigotMC](https://www.spigotmc.org/resources/mcjukebox.16024/) resource for more information.

# Support
If you need any assistance please email [support@mcjukebox.net](mailto:support@mcjukebox.net) or join our discord: [discord.gg/N3TMTCH](https://discord.gg/N3TMTCH).

# Maven Repository
If you use JukeboxAPI as a dependency for your plugin you can add it directly with the latest Jar file from the [releases page](https://github.com/oliverdunk/JukeboxAPI/releases) or as a Maven dependency.  

First you need to add the GitHub Packages repository:

```
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/oliverdunk/JukeboxAPI</url>
    </repository>
</repositories>
```

Then you need to add the dependency, with the latest version from the [packages listing](https://github.com/oliverdunk/JukeboxAPI/packages?q=com.oliverdunk.MCJukebox-plugin).

```
<dependency>
    <groupId>com.oliverdunk.MCJukebox-plugin</groupId>
    <artifactId>mcjukebox-plugin</artifactId>
    <version>VERSION</version>
</dependency>
```
