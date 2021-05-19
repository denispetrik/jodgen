package dev.denispetrik.jodgen

import org.gradle.api.file.DirectoryProperty

abstract class JodgenPluginExtension {

    abstract val sourceDir: DirectoryProperty

    abstract val outputDir: DirectoryProperty
}