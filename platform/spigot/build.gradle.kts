plugins {
    id("java")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":platform:common"))

    implementation("dev.jorel:commandapi-spigot-shade:12.0.0")
}