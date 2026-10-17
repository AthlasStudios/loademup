plugins {
    id("java")
    id("io.papermc.paperweight.userdev")
}

dependencies {
    implementation(project(":api"))

    paperweight.paperDevBundle("1.17.1-R0.1-SNAPSHOT")
}

configurations.compileOnly {
    dependencies.removeIf { it.group == "org.spigotmc" && it.name == "spigot-api" }
}
