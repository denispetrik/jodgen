package dev.denispetrik.jodgen

import org.gradle.api.Plugin
import org.gradle.api.Project

class JodgenPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("jodgen", JodgenPluginExtension::class.java)

        project.tasks.register("generateDsl", GenerateJooqTask::class.java) { task ->
            task.group = "jooq"
            task.sourceDir.set(extension.sourceDir)
            task.outputDir.set(extension.outputDir)
        }
    }
}