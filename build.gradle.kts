plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "dev.yeruza.plugin.permadeath"
version = "2.1.5-PaperNMS"


repositories {
    mavenCentral()
    maven {
        name = "dmulloy2-repo"
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }

    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        name = "engine-hub"
        url = uri("https://maven.enginehub.org/repo/")
    }

    maven {
        name = "placeholder-api"
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
}

dependencies {
    compileOnly(platform("org.mongodb:mongodb-driver-bom:5.5.1"))
    compileOnly("org.mongodb:mongodb-driver-sync:5.5.1")

    compileOnly("net.dv8tion:JDA:6.0.0-rc.1")

    compileOnly("org.apache.logging.log4j:log4j-core:2.19.0")

    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.16-SNAPSHOT")

    compileOnly("me.clip:placeholderapi:2.11.6")

    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")


    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")
}

java.toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    compileJava {
        options.release = 21
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    runServer {
        minecraftVersion("1.21.8")

        downloadPlugins {
            hangar("PlaceholderAPI", "2.11.6")
            hangar("WorldEdit", "7.3.16")
        }
    }
}



