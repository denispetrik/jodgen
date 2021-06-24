plugins {
    kotlin("jvm") version "1.5.10"
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.15.0"
}

pluginBundle {
    website = "https://github.com/denispetrik/jodgen"
    vcsUrl = "https://github.com/denispetrik/jodgen.git"
    tags = listOf("jooq", "dsl", "generator")
}

gradlePlugin {
    plugins {
        create("jodgen") {
            id = "dev.denispetrik.jodgen"
            displayName = "Jooq dsl generator"
            description = "A plugin for jooq dsl class generation"
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
version = "0.1.1"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.testcontainers:postgresql:1.15.3")
    implementation("org.flywaydb:flyway-core:7.9.0")
    implementation("org.jooq:jooq-codegen:3.14.9")
    implementation("org.postgresql:postgresql:42.2.20.jre7")
}

repositories {
    mavenCentral()
}
