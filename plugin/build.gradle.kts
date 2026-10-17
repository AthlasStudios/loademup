plugins {
    id("com.gradleup.shadow")
    id("xyz.jpenilla.run-paper")
    id("com.modrinth.minotaur")

    id("xyz.jpenilla.resource-factory-bukkit-convention")
    //id("xyz.jpenilla.resource-factory-paper-convention")
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
    apiVersion = "1.13",
    description = "Advanced plugin reloader for Spigot — smart, safe, and production-ready.",
    authors = listOf("Gurwi"),
    foliaSupported = false
)

dependencies {
    implementation(project(":api"))
    implementation(project(":platform:spigot"))
    implementation(project(":platform:paper"))

    // DEPENDENCIES
    implementation("dev.gurwi:prism-paper:1.0.0-beta.1")
    implementation("net.kyori:adventure-platform-bukkit:4.4.0")
    implementation("net.kyori:adventure-text-minimessage:4.21.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.26.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.13.1")

    compileOnly("com.mojang:brigadier:1.0.18")
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName = "${rootProject.name}-${rootProject.version}.jar"
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        from("build/generated/resourceFactory/main") {
            include("plugin.yml", "paper-plugin.yml")
        }

        relocate("net.kyori", "club.athlas.loademup.libs.kyori")
        relocate("okhttp3", "club.athlas.loademup.libs.okhttp3")
        relocate("okio", "club.athlas.loademup.libs.okio")
        relocate("dev.gurwi.prism", "club.athlas.loademup.libs.command")
        relocate("dev.gurwi.confetti", "club.athlas.loademup.libs.config")

        exclude("LICENSE", "META-INF/maven/**")
    }

    runServer {
        minecraftVersion("1.21.11")
    }

    @Suppress("UnstableApiUsage")
    withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
        javaLauncher = rootProject.javaToolchains.launcherFor {
            vendor = JvmVendorSpec.JETBRAINS
            languageVersion = JavaLanguageVersion.of(25)
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

//    commands.add(
//        commands.create("loademup",
//            {
//                aliases.add("leu")
//            }
//        )
//    )
}

//paperPluginYaml {
//    main.set(pluginData.mainClass)
//    bootstrapper.set("club.athlas.loademup.LoadEmUpBootstrap")
//    apiVersion.set("1.20")
//
//    name.set(rootProject.name)
//    version.set(rootProject.version.toString())
//    description.set(pluginData.description)
//    authors.set(pluginData.authors)
//    website.set("https://dev.athlas.club")
//    foliaSupported.set(pluginData.foliaSupported)
//}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set(rootProject.name.lowercase())
    versionNumber.set(version.toString())
    versionType.set("beta")
    uploadFile.set(file("build/libs/${rootProject.name}-$version.jar"))
    gameVersions.addAll(
        "1.21.10", "1.21.9", "1.21.8", "1.21.7", "1.21.6", "1.21.5", "1.21.4", "1.21.3", "1.21.2", "1.21.1", "1.21",
        "1.20.6", "1.20.5", "1.20.4", "1.20.3", "1.20.2", "1.20.1", "1.20",
        "1.19.4", "1.19.3", "1.19.2", "1.19.1", "1.19",
        "1.18.2", "1.18.1", "1.18",
        "1.17.1", "1.17"
    )

    loaders.addAll("bukkit", "spigot", "paper", "purpur")
}