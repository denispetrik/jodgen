package dev.denispetrik.jodgen

import org.gradle.api.Action
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

abstract class JodgenPluginExtension {

    @get:Nested
    abstract val input: Input

    @get:Nested
    abstract val database: Database

    @get:Nested
    abstract val output: Output

    fun setConventions(layout: ProjectLayout) {
        input.dir.convention(layout.projectDirectory.dir("src/main/resources/db/migration"))
        output.dir.convention(layout.buildDirectory.dir("generated/jooq"))
    }

    fun input(action: Action<Input>) {
        action.execute(input)
    }

    fun database(action: Action<Database>) {
        action.execute(database)
    }

    fun output(action: Action<Output>) {
        action.execute(output)
    }
}

abstract class Input {
    abstract val dir: DirectoryProperty
}

abstract class Database {
    abstract val schema: Property<String>
}

abstract class Output {
    abstract val packageName: Property<String>
    abstract val dir: DirectoryProperty
}