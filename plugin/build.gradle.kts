plugins {
    id("com.gradleup.shadow")
    id("io.papermc.paperweight.userdev")
    id("xyz.jpenilla.run-paper")
    id("com.modrinth.minotaur")

    id("xyz.jpenilla.resource-factory-bukkit-convention")
    id("xyz.jpenilla.resource-factory-paper-convention")
}

data class PluginData(
    val mainClass: String,
    val apiVersion: String,
    val description: String,
    val authors: List<String>,
    val foliaSupported: Boolean
)

val pluginData = PluginData(
    mainClass = "club.athlas.loademup.LoadEmUp",
    apiVersion = "1.21",
    description = "Advanced plugin reloader for Spigot — smart, safe, and production-ready.",
    authors = listOf("Gurwi"),
    foliaSupported = false
)

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
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName = "${rootProject.name}-${rootProject.version}.jar"

        from("build/generated/resourceFactory/main") {
            include("plugin.yml", "paper-plugin.yml")
        }

        relocate("me.gurwi.commandframework", "club.athlas.loademup.libs.commands")
        relocate("net.kyori", "club.athlas.loademup.libs.kyori")
        relocate("okhttp3", "club.athlas.loademup.libs.okhttp3")
        relocate("okio", "club.athlas.loademup.libs.okio")
        relocate("com.google", "club.athlas.loademup.libs.gson")
    }

    runServer {
        minecraftVersion("1.21.5")
    }

    @Suppress("UnstableApiUsage")
    withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
        javaLauncher = rootProject.javaToolchains.launcherFor {
            vendor = JvmVendorSpec.JETBRAINS
            languageVersion = JavaLanguageVersion.of(21)
        }

        jvmArgs("-XX:+AllowEnhancedClassRedefinition")
    }
}

bukkitPluginYaml {
    main.set(pluginData.mainClass)
    apiVersion.set(pluginData.apiVersion)

    name.set(rootProject.name)
    version.set(rootProject.version.toString())
    description.set(pluginData.description)
    authors.set(pluginData.authors)
    website.set("https://dev.athlas.club")
    foliaSupported.set(pluginData.foliaSupported)

    commands.add(commands.create("loademup"))
}

paperPluginYaml {
    main.set(pluginData.mainClass)
    bootstrapper.set("club.athlas.loademup.LoadEmUpBootstrap")
    apiVersion.set(pluginData.apiVersion)

    name.set(rootProject.name)
    version.set(rootProject.version.toString())
    description.set(pluginData.description)
    authors.set(pluginData.authors)
    website.set("https://dev.athlas.club")
    foliaSupported.set(pluginData.foliaSupported)
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