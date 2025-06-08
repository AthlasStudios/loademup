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
        mavenLocal() // TODO -> MOVE TO PUBLIC REPO

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
            name = "spigotmc-repo"
        }

        maven("https://repo.papermc.io/repository/maven-public/") {
            name = "papermc-repo"
        }

        maven("https://oss.sonatype.org/content/groups/public/") {
            name = "sonatype"
        }

    }

    dependencies {
        implementation("org.jetbrains:annotations:24.0.0")
        compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
        //compileOnly("org.spigotmc:spigot-api:1.21.5-R0.1-SNAPSHOT")
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