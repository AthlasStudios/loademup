plugins {
    id("maven-publish")
    id("com.gradleup.shadow")
}

repositories {
    mavenCentral()
}

dependencies {

}

tasks {
    assemble {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}-API.jar")
    }

    register<Jar>("sourcesJar") {
        from(sourceSets.main.get().allJava)
    }
}

publishing {
    publications {
        create<MavenPublication>("shadedJar") {
            artifact(tasks.named("shadowJar").get())

            groupId = rootProject.group.toString()
            artifactId = rootProject.name
            version = rootProject.version.toString()
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