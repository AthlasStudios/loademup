plugins {
    id("com.gradleup.shadow") version "8.3.5"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "1.7.7"
}

val projectName = rootProject.name
version = project.properties["projectVersion"].toString()

dependencies {
    implementation(project(":api"))

    // DEPENDENCIES
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")

    implementation("me.gurwi:CommandFramework:1.0.3")
    implementation("net.kyori:adventure-platform-bukkit:4.4.0")
    implementation("net.kyori:adventure-text-minimessage:4.21.0")
//    implementation("org.mockito:mockito-core:5.12.0")
//    implementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.0.0")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release = 21
    }

    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName = "${projectName}-${version}.jar"

        relocate("me.gurwi.commandframework", "club.athlas.loademup.libs.commands")
        relocate("net.kyori", "club.athlas.loademup.libs.kyori")
    }

    runServer {
        minecraftVersion("1.21.5")
    }
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
}