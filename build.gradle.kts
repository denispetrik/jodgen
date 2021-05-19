plugins {
    kotlin("jvm") version "1.4.32"
    `java-gradle-plugin`
    `maven-publish`
}

gradlePlugin {
    plugins {
        create("jodgen") {
            id = "dev.denispetrik.jodgen"
            implementationClass = "dev.denispetrik.jodgen.JodgenPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("jodgen") {
            from(components["java"])
        }
    }
}

group = "dev.denispetrik"
version = "0.1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.testcontainers:postgresql:1.15.3")
    implementation("org.flywaydb:flyway-core:7.9.0")
    implementation("org.jooq:jooq-meta:3.14.9")
    implementation("org.jooq:jooq-codegen:3.14.9")
    implementation("org.postgresql:postgresql:42.2.20.jre7")
}

repositories {
    mavenCentral()
}
