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
package dev.buijs.klutter.tasks

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.KlutterTask
import dev.buijs.klutter.kore.project.*
import dev.buijs.klutter.kore.shared.*
import java.io.File

/**
 * Task to generate the boilerplate code required to let Kotlin Multiplatform and Flutter communicate.
 */
class GenerateAdaptersTask(
    private val project: Project,
    private val isApplication: Boolean,
) : KlutterTask {

    override fun run() {
        if(isApplication) {
            GenerateAdaptersForApplicationTask(
                ios = project.ios,
                root = project.root,
                android = project.android,
                platform = project.platform,
            ).run()
        } else {
            GenerateAdaptersForPluginTask(
                ios = project.ios,
                root = project.root,
                android = project.android,
                platform = project.platform,
            ).run()
        }
    }

}

/**
 * Utility to scan a Klutter project and collect its metadata.
 *
 * The metadata is required to generate method channel code in:
 * - root/lib
 * - root/android
 * - root/ios
 */
internal fun Platform.collect(): AdapterData {

    val source = source()

    val methods = source.methods()

    val controllers = source.controllers().toControllerNames()

    val responses = source.responses()

    val messages = responses.toDartMessageList()

    val enumerations = responses.toDartEnumList()

    validate(
        messages = messages,
        enumerations = enumerations,
    )

    return AdapterData(
        methods = methods,
        messages = messages,
        enumerations = enumerations,
        controllers = controllers,
    )
}

/**
 * Metadata containing all required information to generated method channel code.
 */
internal data class AdapterData(

    /**
     * List of @KlutterAdaptee annotated methods.
     */
    val methods: List<Method>,

    /**
     * List of custom data transfer objects annotated with @KlutterResponse defined in the platform module.
     */
    val messages: List<DartMessage>,

    /**
     * List of enumerations annotated with @KlutterResponse defined in the platform module.
     */
    val enumerations: List<DartEnum>,

    /**
     * List of controller classes annotated with @Controller defined in the platform module.
     */
    val controllers: List<String>,
)

/**
 * Validate every field in the DartMessages is either a standard type
 * or a custom type that is includes as DartMessage.
 *
 * @throws [KlutterException] if a DartMessage contains any
 * custom datatype field that is not defined as DartMessage.
 */
internal fun validate(
    messages: List<DartMessage>,
    enumerations: List<DartEnum>,
) {

    val customDataTypes = mutableListOf<String>()

    //Collect all custom data types.
    for (message in messages) {
        for (field in message.fields) {
            if (field.isCustomType) {
                customDataTypes.add(field.type)
            }
        }
    }

    //Iterate the enumeration names and match it to the custom data types.
    //Remove from custom data types list if matched.
    enumerations.map { it.name }.forEach {
        customDataTypes.removeAll { cdt -> cdt == it }
    }

    //Iterate the message names and match it to the custom data types.
    //Remove from custom data types list if matched.
    messages.map { it.name }.forEach {
        customDataTypes.removeAll { cdt -> cdt == it }
    }

    //Any custom data type name left in the list means there is no class definition found by this name
    if (customDataTypes.isNotEmpty()) {
        throw KlutterException(
            """ |Processing annotation '@KlutterResponse' failed, caused by:
                |
                |Could not resolve the following classes:
                |
                |${customDataTypes.joinToString { "- '$it'\r\n" }}
                |
                |Verify if all KlutterResponse annotated classes comply with the following rules:
                |
                |1. Must be an open class
                |2. Fields must be immutable
                |3. Constructor only (no body)
                |4. No inheritance
                |5. Any field type should comply with the same rules
                |
                |If this looks like a bug please file an issue at: https://github.com/buijs-dev/klutter/issues
            """.trimMargin()
        )
    }

}

internal fun File.responses() = this
    .collectAnnotatedWith("@KlutterResponse", "@Stateful")

internal fun File.methods() = this
    .collectAnnotatedWith("@KlutterAdaptee")
    .map { it.toMethods(Language.DART) }
    .flatten()

internal fun File.controllers() = this
    .collectAnnotatedWith("@Controller")

/**
 * Find all files in a folder (including sub folders)
 * containing an annotation with [annotationName].
 *
 * @return List of Files that contain the given annotation.
 */
internal fun File.collectAnnotatedWith(
    vararg annotationName: String,
): List<File> = this
    .verifyExists()
    .walkTopDown()
    .map { f -> if(!f.isFile) null else f }
    .toList()
    .filterNotNull()
    .filter { file ->
        annotationName
            .toList()
            .map { it.prefixIfNot("@") }
            .any { file.readText().contains(it) }
    }