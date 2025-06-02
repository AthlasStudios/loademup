plugins {
    id("com.gradleup.shadow") version "8.3.5"
}

val projectName = rootProject.name
version = project.properties["projectVersion"].toString()

dependencies {
    implementation(project(":api"))

    // DEPENDENCIES
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName = "${projectName}-${version}.jar"

        // CommandFramework Relocation -> relocate("me.gurwi.commandframework", "club.athlas.libs.commands")
    }
}