import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.5"
}

val projectName = rootProject.name
val projectVersion = project.properties["projectVersion"].toString()

repositories {
    mavenCentral()
}

dependencies {

}

tasks.register<Jar>("sourceJar") {
    from(sourceSets.main.get().allJava)
}

tasks {
    assemble {
        dependsOn("shadowJar")
    }

    withType<ShadowJar> {
        archiveFileName.set("$projectName-$version-API.jar")
    }
}

publishing {
    publications {
        create<MavenPublication>("shadedJar") {
            artifact(tasks.named("shadowJar").get())

            groupId = "club.athlas"
            artifactId = projectName
            version = projectVersion
        }
    }

    repositories {
        maven("https://repo.athlas.club/releases") {
            credentials {
                username = findProperty("athlasMavenUser") as String?
                password = findProperty("athlasMavenPassword") as String?
            }

            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}