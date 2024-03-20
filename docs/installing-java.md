---
title: Installing Java
description: "Install Java on your system"
---

# JMusicBot requires Java 11
Some newer builds may also work, but you could run into compatibility issues. For most platforms, the easiest method to install Java is to download the installer (as listed below). Linux users can alternatively install via the command line using a package manager.

## Download Installer (any platform)
1. Navigate to [Adoptium](https://adoptium.net/temurin/releases/?version=11&package=jre) or [Oracle](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
2. Download the correct installer for your system
3. Run the installer or manually install the package (depending on your selection)

## Install via Command Line (Linux only)
!!! tip
    If you have multiple Java versions installed, you can use `sudo update-alternatives --config java` to select which will be used by default. You can run `java -version` to see what version is currently the default.
### Ubuntu
1. Run: `sudo apt-get update && sudo apt-get install openjdk-11-jre -y`
### Debian 
1. Install `sudo` if not installed (`apt update && apt upgrade && apt install sudo`)  
2. Run: `sudo apt-get install default-jre`
### Raspbian (Raspberry Pi)
1. Run: `sudo apt-get install oracle-java11-jdk`
