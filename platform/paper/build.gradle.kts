plugins {
    id("java")
    id("io.papermc.paperweight.userdev")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":platform:common"))

    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
    implementation("dev.jorel:commandapi-paper-shade:12.0.0")
}

configurations.compileOnly {
    dependencies.removeIf { it.group == "org.spigotmc" && it.name == "spigot-api" }
}