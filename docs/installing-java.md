---
title: Installing Java
description: "Install Java on your system"
---

!!! tip
    Generally, a reliable place to find Java builds is [AdoptOpenJDK](https://adoptopenjdk.net/index.html).

### 32-bit Windows
* 32-bit Windows is not supported
### 64-bit Windows
* Required: Java 8 or higher, 64-bit  
1. Download from [AdoptOpenJDK](https://adoptopenjdk.net/index.html) or [Oracle](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
2. Install downloaded file with default settings
### Ubuntu
1. Run: `sudo apt-get update && sudo apt-get install latest-jre -y`
### Debian 
1. Install `sudo` if not installed (`apt update && apt upgrade && apt install sudo`)  
2. Run: `sudo apt-get install default-jre`
### Raspbian (Raspberry Pi)
1. Run: `sudo apt-get install oracle-java8-jdk`
### Mac
1. Download from [AdoptOpenJDK](https://adoptopenjdk.net/index.html)
2. Install downloaded package