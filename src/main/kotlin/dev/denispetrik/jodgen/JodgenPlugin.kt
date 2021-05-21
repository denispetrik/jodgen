package dev.denispetrik.jodgen

import org.gradle.api.Plugin
import org.gradle.api.Project

class JodgenPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("jodgen", JodgenPluginExtension::class.java)
        extension.setConventions(project.layout)

        project.tasks.register("generateJooqDsl", GenerateJooqDslTask::class.java) { task ->
            task.group = "jooq"
            task.description = "Generates jooq dsl classes"

            task.inputDir.set(extension.input.dir)
            task.databaseSchema.set(extension.database.schema)
            task.outputPackageName.set(extension.output.packageName)
            task.outputDir.set(extension.output.dir)
        }
    }
}