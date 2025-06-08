plugins {
    id("com.gradleup.shadow") version "8.3.5"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "1.7.7"
    id("com.modrinth.minotaur") version "2.+"
}

val projectName = rootProject.name
version = project.properties["projectVersion"].toString()

dependencies {
    implementation(project(":api"))

    // DEPENDENCIES
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")

    implementation("me.gurwi:CommandFramework:1.0.4")
    implementation("net.kyori:adventure-platform-bukkit:4.4.0")
    implementation("net.kyori:adventure-text-minimessage:4.21.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.13.1")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release = 17
    }

    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName = "${projectName}-${version}.jar"
        archiveClassifier.set("")

        relocate("me.gurwi.commandframework", "club.athlas.loademup.libs.commands")
        relocate("net.kyori", "club.athlas.loademup.libs.kyori")
        relocate("okhttp3", "club.athlas.loademup.libs.okhttp3")
        relocate("okio", "club.athlas.loademup.libs.okio")
        relocate("com.google.gson", "club.athlas.loademup.libs.gson")
    }

    runServer {
        minecraftVersion("1.21.3")
    }
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }

    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set(rootProject.name.lowercase())
    versionNumber.set(version.toString())
    versionType.set("beta")
    uploadFile.set(file("build/libs/${rootProject.name}-$version.jar"))
    gameVersions.addAll(
        "1.21.5", "1.21.4", "1.21.3", "1.21.2", "1.21.1", "1.21",
        "1.20.6", "1.20.5", "1.20.4", "1.20.3", "1.20.2", "1.20.1", "1.20",
        "1.19.4", "1.19.3", "1.19.2", "1.19.1", "1.19",
        "1.18.2", "1.18.1", "1.18",
        "1.17.1", "1.17"
    )
    loaders.addAll("bukkit", "spigot", "paper", "purpur")
}