plugins {
    id("java-library")
}

subprojects {
    apply(plugin = "java-library")

    group = "club.athlas"
    version = project.properties["projectVersion"] as String
    val projectName = rootProject.name

    repositories {
        mavenCentral()

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
            name = "spigotmc-repo"
        }

        maven("https://oss.sonatype.org/content/groups/public/") {
            name = "sonatype"
        }

        maven("https://repo.athlas.club/private") {

            credentials {
                username = findProperty("athlasMavenUser") as String?
                password = findProperty("athlasMavenPassword") as String?
            }

            authentication {
                create<BasicAuthentication>("basic")
            }
        }

    }

    dependencies {
        implementation("org.jetbrains:annotations:24.0.0")
        compileOnly("org.spigotmc:spigot-api:1.20.3-R0.1-SNAPSHOT")
    }

    val targetJavaVersion = 17
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"

        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible()) {
            options.release.set(targetJavaVersion)
        }
    }

    tasks.named<ProcessResources>("processResources") {
        val props = mapOf("version" to version, "projectName" to projectName)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

}