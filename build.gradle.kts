plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.0.0-beta17" apply false
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17" apply false
    id("xyz.jpenilla.run-paper") version "2.3.1" apply false
    id("com.modrinth.minotaur") version "2.+" apply false

    // Other Resource Factories -> https://github.com/jpenilla/resource-factory
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.3.0" apply false
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.0" apply false
}

allprojects {
    group = "club.athlas"
    version = "0.1.1"

    apply(plugin = "java-library")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
}

subprojects {
    repositories {
        mavenCentral()
        mavenLocal()

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

        // libs
        compileOnly("org.jetbrains:annotations:26.0.2")
    }
}