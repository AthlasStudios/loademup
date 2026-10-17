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
    version = "0.1.5-DEV"

    apply(plugin = "java-library")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17)) // TODO -> TRY TO GO TO 8 FOR LEGACY SUPPORT
    }

    configurations.all {
        exclude(group = "org.jspecify", module = "jspecify")
    }
}

subprojects {
    repositories {
        mavenCentral()
        mavenLocal()

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://libraries.minecraft.net")
    }

    dependencies {
        compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")

        // libs
        compileOnly("org.jetbrains:annotations:26.0.2")
    }
}