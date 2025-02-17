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

import dev.buijs.klutter.kore.test.TestUtil
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

class GeneratePluginProjectTaskSpec extends Specification {

    @Shared
    def executor = new Exeggutor()

    @Shared
    def pluginName = "my_awesome_plugin"

    @Shared
    def groupName = "com.example.awesomeness"

    @Shared
    def root = Files.createTempDirectory("").toFile()

    @Shared
    def pathToRoot = root.absolutePath

    @Shared
    def plugin = new File("${pathToRoot}/$pluginName")

    @Shared
    def pathToPlugin = plugin.absolutePath

    @Shared
    def example = new File("${pathToPlugin}/example")

    @Shared
    def pathToExample = example.absolutePath

    @Shared
    def sut = new GeneratePluginProjectTask(pathToRoot, pluginName, groupName, executor)

    @Shared
    def createFlutterPlugin = "flutter create my_awesome_plugin --org com.example.awesomeness --template=plugin --platforms=android,ios"

    @Shared
    def flutterPubGet = "flutter pub get"

    @Shared
    def klutterProducerInit = "flutter pub run klutter:producer init"

    @Shared
    def klutterConsumerInit = "flutter pub run klutter:consumer init"

    @Shared
    def klutterProducerInstallLibrary = "flutter pub run klutter:producer install=library"

    def setupSpec() {
        plugin.mkdirs()
        example.mkdirs()
    }

    def "Verify a new project is created"(){
        given:
        def pubspecInRoot = new File("${pathToPlugin}/pubspec.yaml")
        pubspecInRoot.createNewFile()
        pubspecInRoot.write(rootPubspecYaml)

        def pubspecInExample = new File("${pathToExample}/pubspec.yaml")
        pubspecInExample.createNewFile()
        pubspecInExample.write(examplePubspecYaml)

        new File("${pathToPlugin}/android").mkdirs()
        def localProperties = new File("${pathToPlugin}/android/local.properties")
        localProperties.createNewFile()
        localProperties.write("hello=true")

        and:
        executor.putExpectation(pathToRoot, createFlutterPlugin)
        executor.putExpectation(pathToPlugin, flutterPubGet)
        executor.putExpectation(pathToExample, flutterPubGet)
        executor.putExpectation(pathToPlugin, klutterProducerInit)
        executor.putExpectation(pathToExample, klutterConsumerInit)
        executor.putExpectation(pathToPlugin, klutterProducerInstallLibrary)

        when:
        sut.run()

        then: "Klutter is added as dependency to pubspec.yaml"
        TestUtil.verify(pubspecInRoot.text, rootPubspecYamlWithKlutter)
        TestUtil.verify(pubspecInExample.text, examplePubspecYamlWithKlutter)

        and: "local.properties is copied to root"
        with(new File("$pathToPlugin/local.properties")) {
            it.exists()
            it.text.contains("hello=true")
        }

        and: "test folder is deleted"
        !new File("$pathToPlugin/test").exists()

        and: "a new README.md is created"
        with(new File("$pathToPlugin/README.md")) {
            it.exists()
            TestUtil.verify(it.text, readme)
        }
    }

    @Shared
    def readme = """
        # my_awesome_plugin
        A new Klutter plugin project. 
        Klutter is a framework which interconnects Flutter and Kotlin Multiplatform.
        
        ## Getting Started
        This project is a starting point for a Klutter
        [plug-in package](https://github.com/buijs-dev/klutter),
        a specialized package that includes platform-specific implementation code for
        Android and/or iOS. 
        
        This platform-specific code is written in Kotlin programming language by using
        Kotlin Multiplatform. 
    """

    @Shared
    def rootPubspecYaml = """
        name: my_awesome_plugin
        description: A new Flutter plugin project.
        version: 0.0.1
        homepage:
        
        environment:
          sdk: ">=2.17.5 <3.0.0"
          flutter: ">=2.5.0"
        
        dependencies:
          flutter:
            sdk: flutter
          plugin_platform_interface: ^2.0.2
        
        dev_dependencies:
          flutter_test:
            sdk: flutter
          flutter_lints: ^2.0.0
    """

    @Shared
    def rootPubspecYamlWithKlutter = """
        name: my_awesome_plugin
        description: A new Flutter plugin project.
        version: 0.0.1
        homepage:
        
        environment:
          sdk: ">=2.17.5 <3.0.0"
          flutter: ">=2.5.0"
        
        dependencies:
          klutter: ^0.2.3
          flutter:
            sdk: flutter
        
        dev_dependencies:
          flutter_test:
            sdk: flutter
          flutter_lints: ^2.0.0
    """

    @Shared
    def examplePubspecYaml = """
        name: my_awesome_plugin_example
        description: Demonstrates how to use the my_plugin plugin.
        
        publish_to: 'none' # Remove this line if you wish to publish to pub.dev
        
        environment:
          sdk: ">=2.17.5 <3.0.0"
        
        dependencies:
          flutter:
            sdk: flutter
        
          my_plugin:
        
            path: ../
        
          cupertino_icons: ^1.0.2
        
        dev_dependencies:
          flutter_test:
            sdk: flutter
          flutter_lints: ^2.0.0
        
        # For information on the generic Dart part of this file, see the
        # following page: https://dart.dev/tools/pub/pubspec
        
        # The following section is specific to Flutter packages.
        flutter:
        
          # The following line ensures that the Material Icons font is
          # included with your application, so that you can use the icons in
          # the material Icons class.
          uses-material-design: true
    """

    @Shared
    def examplePubspecYamlWithKlutter = """
        name: my_awesome_plugin_example
        description: Demonstrates how to use the my_plugin plugin.
        
        publish_to: 'none' # Remove this line if you wish to publish to pub.dev
        
        environment:
          sdk: ">=2.17.5 <3.0.0"
        
        dependencies:
          klutter: ^0.2.3
          flutter:
            sdk: flutter
        
          my_plugin:
        
            path: ../
        
          cupertino_icons: ^1.0.2
        
        dev_dependencies:
          flutter_test:
            sdk: flutter
          flutter_lints: ^2.0.0
        
        # For information on the generic Dart part of this file, see the
        # following page: https://dart.dev/tools/pub/pubspec
        
        # The following section is specific to Flutter packages.
        flutter:
        
          # The following line ensures that the Material Icons font is
          # included with your application, so that you can use the icons in
          # the material Icons class.
          uses-material-design: true
    """

    private static class Exeggutor extends CliExecutor {

        /**
         * Map of expected CLI executions.
         *
         * Key: String absolutepath where command is to be executed.
         * Value: List<String> commands to be executed.
         */
        private def expectations = new HashMap<String,List<String>>()

        @Override
        String execute(String command, File runFrom, Long timeout) {
            if(expectations.containsKey(runFrom.absolutePath)) {
                if(expectations[runFrom.absolutePath].contains(command)) {
                    return ""
                }
            }

            throw new RuntimeException("CLI execution failure: $command - $runFrom.absolutePath")
        }

        def putExpectation(String runFrom, String command) {
            if(!expectations.containsKey(runFrom)) {
                expectations.put(runFrom, [command])
            } else {
                expectations.get(runFrom).add(command)
            }
        }

    }

}
