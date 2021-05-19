package dev.denispetrik.jodgen

import org.flywaydb.core.Flyway
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Database
import org.jooq.meta.jaxb.Generator
import org.jooq.meta.jaxb.Jdbc
import org.jooq.meta.jaxb.Target
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

abstract class GenerateJooqTask : DefaultTask() {

    @get:InputDirectory
    abstract val sourceDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        logger.lifecycle("sourceDir=${sourceDir.get()}, outputDir=${outputDir.get()}")

        val dbSchema = "prod"
        val packageName = "dev.denispetrik.sampleapp.jooq"

        val container = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:9.6.12"))
        container.start()

        val flyway = Flyway.configure()
            .dataSource(container.jdbcUrl, container.username, container.password)
            .schemas(dbSchema)
            .locations("filesystem:${sourceDir.asFile.get().path}")
            .load()
        flyway.migrate()

        val configuration = Configuration()
        configuration.jdbc = Jdbc()
            .withDriver("org.postgresql.Driver")
            .withUrl(container.jdbcUrl)
            .withUser(container.username)
            .withPassword(container.password)
        val database = Database()
            .withName("org.jooq.meta.postgres.PostgresDatabase")
            .withIncludes(".*")
            .withExcludes("flyway_schema_history")
            .withInputSchema(dbSchema)
        val target = Target()
            .withPackageName(packageName)
            .withDirectory(outputDir.asFile.get().path)
        configuration.generator = Generator()
            .withDatabase(database)
            .withTarget(target)
        GenerationTool.generate(configuration)

        container.close()
    }
}