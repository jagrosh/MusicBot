plugins {
    java
    id("com.gradleup.shadow") version "8.3.6"
}

group = "com.lukas48452"
version = "Snapshot"

java {
    sourceCompatibility = JavaVersion.toVersion("26")
    targetCompatibility = JavaVersion.toVersion("26")
}

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    maven("https://m2.chew.pro/releases")
    maven("https://jitpack.io")
    maven("https://m2.duncte123.dev/releases")
    maven("https://maven.lavalink.dev/releases")
}

dependencies {
    implementation("net.dv8tion:JDA:4.4.1_353")
    implementation("pw.chew:jda-chewtils:1.24.1")
    implementation("dev.arbjerg:lavaplayer:2.2.1")
    implementation("dev.lavalink.youtube:common:1.5.2")
    implementation("com.github.jagrosh:JLyrics:master-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.dunctebot:sourcemanagers:1.9.0")
    implementation("redis.clients:jedis:5.2.0")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("com.zaxxer:HikariCP:6.0.0")
    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("com.typesafe:config:1.4.3")
    implementation("org.jsoup:jsoup:1.18.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.junit.platform:junit-platform-launcher:1.11.4")
    testImplementation("org.hamcrest:hamcrest-core:3.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks {
    shadowJar {
        archiveClassifier.set("All")
        mergeServiceFiles()
        manifest {
            attributes(
                "Main-Class" to "com.jagrosh.jmusicbot.LMusicBot",
                "Specification-Title" to project.name,
                "Specification-Version" to project.version,
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor-Id" to project.group
            )
        }
    }

    build {
        dependsOn(shadowJar)
    }
}
