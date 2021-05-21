package dev.denispetrik.jodgen

import org.flywaydb.core.Flyway
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
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

abstract class GenerateJooqDslTask : DefaultTask() {

    companion object {
        private val postgresImageName = DockerImageName.parse("postgres:9.6.12")
    }

    @get:InputDirectory
    abstract val inputDir: DirectoryProperty

    @get:Input
    abstract val databaseSchema: Property<String>

    @get:Input
    abstract val outputPackageName: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun execute() {
        usingPostgres { container ->
            applyScripts(container)
            generateDslClasses(container)
        }
    }

    private fun usingPostgres(codeBlock: (PostgreSQLContainer<Nothing>) -> Unit) {
        logger.lifecycle("starting postgresql container: image=$postgresImageName")

        PostgreSQLContainer<Nothing>(postgresImageName).use { container ->
            container.start()
            codeBlock(container)
        }
    }

    private fun applyScripts(container: PostgreSQLContainer<Nothing>) {
        logger.lifecycle("applying scripts: scripts dir=${inputDir.get()}, schema=${databaseSchema.get()}")

        val flyway = Flyway.configure()
            .dataSource(container.jdbcUrl, container.username, container.password)
            .schemas(databaseSchema.get())
            .locations("filesystem:${inputDir.asFile.get().path}")
            .load()
        flyway.migrate()
    }

    private fun generateDslClasses(container: PostgreSQLContainer<Nothing>) {
        logger.lifecycle("generating classes: package=${outputPackageName.get()}, outputDir=${outputDir.get()}")

        val jdbc = Jdbc()
            .withDriver("org.postgresql.Driver")
            .withUrl(container.jdbcUrl)
            .withUser(container.username)
            .withPassword(container.password)

        val database = Database()
            .withName("org.jooq.meta.postgres.PostgresDatabase")
            .withIncludes(".*")
            .withExcludes("flyway_schema_history")
            .withInputSchema(databaseSchema.get())

        val target = Target()
            .withPackageName(outputPackageName.get())
            .withDirectory(outputDir.asFile.get().path)

        val configuration = Configuration()
            .withJdbc(jdbc)
            .withGenerator(Generator()
                .withDatabase(database)
                .withTarget(target)
            )

        GenerationTool.generate(configuration)
    }
}