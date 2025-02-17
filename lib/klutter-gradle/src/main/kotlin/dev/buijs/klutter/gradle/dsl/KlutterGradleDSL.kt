/* Copyright (c) 2021 - 2022 Buijs Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package dev.buijs.klutter.gradle.dsl

import dev.buijs.klutter.kore.KlutterException
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.tasks.Internal
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.io.File
import java.util.*

/**
 * Glue for the DSL used in a build.gradle(.kts) file and the Klutter tasks.
 */
open class KlutterGradleDSL(project: Project) {

    private var handler = KlutterDependencyHandler(project)

    var root: File? = null

    @Internal
    internal var plugin: KlutterPluginDTO? = null

    @Internal
    internal var application: KlutterApplicationDTO? = null

    /**
     * Configure the Gradle Plugin for a klutter plugin (consumer or producer).
     */
    fun plugin(lambda: KlutterPluginBuilder.() -> Unit) {
        plugin = KlutterPluginBuilder().apply(lambda).build()
    }

    /**
     * Configure the Gradle Plugin for a klutter application using Kompose for the UI.
     */
    fun application(lambda: KlutterApplicationBuilder.() -> Unit) {
        application = KlutterApplicationBuilder().apply(lambda).build()
    }

    /**
     * Add klutter implementation dependency to this project.
     */
    fun include(simpleModuleName: String, version: String? = null, test: Boolean = false) {
        if(test) {
            includeTest(simpleModuleName, version)
        } else {
            handler.addImplementation(simpleModuleName, version)
        }
    }

    /**
     * Add klutter testImplementation dependency to this project.
     */
    fun includeTest(simpleModuleName: String, version: String? = null) {
        handler.addTestImplementation(simpleModuleName, version)
    }

}

/**
 * A Jetbrains 'kotlin' inspired dependency handler which makes it easier
 * to add klutter dependencies in a build.gradle(.kts) file as follows:
 *
 * ```
 * klutter {
 *      implementation("annotations")
 *      implementation("annotations", "2022.r8")
 * }
 * ```
 *
 */
class KlutterDependencyHandler(private val project: Project) {

    fun addImplementation(simpleModuleName: String, version: String? = null) {
        val multiplatform = findKotlinMultiplatformExtension()

        if(multiplatform == null) {
            // Not a Multiplatform Project so apply normally.
            project.dependencies.add("implementation", create(
                simpleModuleName = simpleModuleName,
                version = version,
            ))
        } else {
            // Multiplatform Project so apply for all sourcesets.
            multiplatform.sourceSets.forEach {
                addKotlinMultiplatformDependency(
                    sourceset = it,
                    simpleModuleName = simpleModuleName,
                    version = version ?: KlutterVersion.byName(simpleModuleName),
                )
            }
        }
    }

    fun addTestImplementation(simpleModuleName: String, version: String? = null) {
        val multiplatform = findKotlinMultiplatformExtension()

        if(multiplatform == null) {
            // Not a Multiplatform Project so apply normally.
            project.dependencies.add("testImplementation", create(
                simpleModuleName = simpleModuleName,
                version = version,
            ))
        } else {
            // Multiplatform Project so apply for all sourcesets.
            multiplatform.sourceSets.forEach {
                addKotlinMultiplatformDependency(
                    sourceset = it,
                    simpleModuleName = simpleModuleName,
                    version = version ?: KlutterVersion.byName(simpleModuleName),
                )
            }
        }
    }

    private fun create(simpleModuleName: String, version: String? = null): ExternalModuleDependency =
        project.dependencies.create(
            "dev.buijs.klutter:$simpleModuleName:" + (version ?: KlutterVersion.byName(simpleModuleName))
        ) as ExternalModuleDependency

    private fun addKotlinMultiplatformDependency(
        sourceset: KotlinSourceSet,
        simpleModuleName: String,
        version: String,
    ) {
        sourceset.dependencies {
            implementation(
                create(simpleModuleName, version)
            )
        }
    }

    private fun findKotlinMultiplatformExtension(): KotlinMultiplatformExtension? =
        project.extensions.findByType(KotlinMultiplatformExtension::class.java)

}

internal object KlutterVersion {
    private val properties: Properties = Properties().also {
        it.load(KlutterDependencyHandler::class.java.classLoader.getResourceAsStream("publish.properties"))
    }

    val annotations: String = properties.getProperty("annotations.version")
        ?: throw KlutterException("Missing 'annotations.version' in Klutter Gradle Jar.")

    val kore: String = properties.getProperty("kore.version")
        ?: throw KlutterException("Missing 'kore.version' in Klutter Gradle Jar.")

    val gradle: String = properties.getProperty("plugin.gradle.version")
        ?: throw KlutterException("Missing 'plugin.gradle.version' in Klutter Gradle Jar.")

    val kompose: String = properties.getProperty("kompose.version")
        ?: throw KlutterException("Missing 'kompose.version' in Klutter Gradle Jar.")

    val test: String = properties.getProperty("test.version")
        ?: throw KlutterException("Missing 'test.version' in Klutter Gradle Jar.")

    val tasks: String = properties.getProperty("tasks.version")
        ?: throw KlutterException("Missing 'tasks.version' in Klutter Gradle Jar.")
}

internal fun KlutterVersion.byName(simpleModuleName: String): String = when(simpleModuleName) {
    "annotations" -> annotations
    "kore" -> kore
    "gradle" -> gradle
    "kompose" -> kompose
    "test" -> test
    "tasks" -> tasks
    else -> throw KlutterException("Unknown module name '$simpleModuleName'.")
}