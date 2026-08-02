plugins {
    kotlin("jvm") version  "2.4.10"
    `java-library`
    `maven-publish`
}

group = "dev.lyric"
version = "0.2.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

kotlin {
    jvmToolchain(21)
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "configuration"
            version = project.version.toString()
            from(components["java"])
        }
    }
}
