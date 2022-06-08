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

package dev.buijs.klutter.core.tasks


import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.annotations.KlutterAdapteeScanner
import dev.buijs.klutter.core.annotations.KlutterResponseProcessor
import dev.buijs.klutter.core.annotations.ReturnTypeLanguage
import dev.buijs.klutter.core.shared.*
import dev.buijs.klutter.core.shared.AndroidPluginGenerator
import dev.buijs.klutter.core.shared.FlutterLibraryGenerator
import dev.buijs.klutter.core.shared.FlutterPubspecScanner
import dev.buijs.klutter.core.shared.IosPluginGenerator

class GenerateAdapterTask(
    private val android: Android,
    private val ios: IOS,
    private val root: Root,
    private val platform: Platform,
)
    : KlutterTask
{

    private var messages: List<DartMessage>? = null
    private var enumerations: List<DartEnum>? = null
    private var methods: List<Method>? = null

    override fun run() {
        platform.source().let {
            val processor = KlutterResponseProcessor(it)
            messages = processor.messages
            enumerations = processor.enumerations
            methods =  KlutterAdapteeScanner(it).scan(language = ReturnTypeLanguage.DART)
        }

        processPlugin()

    }

    private fun processPlugin() {

        val pubspec = FlutterPubspecScanner(root.folder.resolve("pubspec.yaml")).scan()

        val pluginName = pubspec.libraryName
        val packageName = pubspec.packageName
        val pluginPath = packageName?.replace(".", "/") ?: ""
        val pluginClassName = pubspec.pluginClassName
        val methodChannelName = packageName ?: "KLUTTER"
        methods?.let { methods ->
            FlutterLibraryGenerator(
                path = root.resolve("lib/$pluginName.dart"),
                methodChannelName = methodChannelName,
                pluginClassName = pluginClassName,
                methods = methods,
                messages = messages ?: emptyList(),
                enumerations = enumerations ?: emptyList(),
            ).generate()

            AndroidPluginGenerator(
                path = android.folder.resolve("src/main/kotlin/$pluginPath/$pluginClassName.kt"),
                methodChannelName = methodChannelName,
                pluginClassName = pluginClassName,
                libraryPackage = packageName,
                methods = methods,
            ).generate()

            IosPluginGenerator(
                path = ios.folder.resolve("Classes/Swift$pluginClassName.swift"),
                methodChannelName = methodChannelName,
                pluginClassName = pluginClassName,
                methods = methods,
                frameworkName = "Platform",
            ).generate()

            ios.folder.resolve("$pluginName.podspec").excludeArm64()
        }

    }

    companion object {

        fun create(pathToRoot: String, pluginName: String? = null): GenerateAdapterTask {
            val project = pluginName
                ?.let { pathToRoot.klutterProject(it) }
                ?:pathToRoot.klutterProject()

            return GenerateAdapterTask(
                root = project.root,
                android = project.android,
                ios = project.ios,
                platform = project.platform,
            )
        }


    }
}
