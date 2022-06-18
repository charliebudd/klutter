package dev.buijs.klutter.core.shared

import dev.buijs.klutter.core.KlutterException
import dev.buijs.klutter.core.project.Pubspec
import dev.buijs.klutter.core.project.PubspecFlutter
import dev.buijs.klutter.core.project.PubspecPlugin
import dev.buijs.klutter.core.project.PubspecPluginClass
import dev.buijs.klutter.core.project.PubspecPluginPlatforms
import dev.buijs.klutter.core.test.TestResource
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

class MethodSpec extends Specification {

    @Shared
    def resources = new TestResource()

    def "Verify Method constructor"(){
        expect:
        with(new Method("a", "b", "c", true, "String")){
            it.command == "a"
            it.import == "b"
            it.method == "c"
            it.dataType == "String"
            it.async
        }
    }

    def "[DartKotlinMap] returns a valid Dart data type"() {
        expect:
        DartKotlinMap.toDartType("Double") == "double"
        DartKotlinMap.toDartType("Int") == "int"
        DartKotlinMap.toDartType("Boolean") == "bool"
        DartKotlinMap.toDartType("String") == "String"
    }

    def "[DartKotlinMap] returns a valid Kotlin data type"() {
        expect:
        DartKotlinMap.toKotlinType("double") == "Double"
        DartKotlinMap.toKotlinType("int") == "Int"
        DartKotlinMap.toKotlinType("bool") == "Boolean"
        DartKotlinMap.toKotlinType("String") == "String"
    }

    def "[DartKotlinMap] an exception is thrown when a Kotlin type does not exist"() {
        when:
        DartKotlinMap.toKotlinType("Stttttring!")

        then:
        KlutterException e = thrown()
        e.getMessage() == "No such type in KotlinDartMap: Stttttring!"
    }

    def "[DartKotlinMap] an exception is thrown when a Dart type does not exist"() {
        when:
        DartKotlinMap.toDartType("Stttttring!")

        then:
        KlutterException e = thrown()
        e.getMessage() == "No such type in KotlinDartMap: Stttttring!"
    }

    def "[DartKotlinMap] an exception is thrown when a Dart/Kotlin type does not exist"() {
        when:
        DartKotlinMap.toMap("Stttttring!")

        then:
        KlutterException e = thrown()
        e.getMessage() == "No such type in KotlinDartMap: Stttttring!"
    }

    def "[DartKotlinMap] null is returned when a Dart/Kotlin type does not exist"() {
        expect:
        DartKotlinMap.toMapOrNull("Stttttring!") == null
    }

    def "[asDataType] #dataType is returned as #expected"() {
        expect:
        MethodKt.asDataType(dataType, lang) == expected

        where:
        dataType        | expected          | lang
        "Stttttring!"   | "Stttttring!"     | Language.KOTLIN
        "Int  "         | "Int"             | Language.KOTLIN
        "  Double"      | "Double"          | Language.KOTLIN
        "Boolean"       | "Boolean"         | Language.KOTLIN
        "String"        | "String"          | Language.KOTLIN
        "int"           | "Int"             | Language.KOTLIN
        "double"        | "Double"          | Language.KOTLIN
        "bool"          | "Boolean"         | Language.KOTLIN
        "String"        | "String"          | Language.KOTLIN
        "Stttttring!"   | "Stttttring!"     | Language.DART
        "Int"           | "int"             | Language.DART
        "Double"        | "double"          | Language.DART
        "Boolean  "     | "bool"            | Language.DART
        "  String"      | "String"          | Language.DART
        "int"           | "int"             | Language.DART
        "double"        | "double"          | Language.DART
        "bool"          | "bool"            | Language.DART
        "  String  "    | "String"          | Language.DART
    }

    def "[toMethod] an empty list is returned when no methods are found"() {
        given:
        def file = Files.createTempFile("SomeClass", "kt").toFile()

        expect:
        MethodKt.toMethods(file, Language.KOTLIN).isEmpty()
    }

    def "[toMethod] a list of methods is returned"() {
        given:
        def file = Files.createTempFile("SomeClass", "kt").toFile()
        resources.copy("platform_source_code", file.absolutePath)

        expect:
        !MethodKt.toMethods(file, Language.KOTLIN).isEmpty()
    }

    def "[toMethod] throws an exception if a return type contains Lists with null values"() {
        given:
        def file = Files.createTempFile("SomeClass", "kt").toFile()
        file.write(classBody)

        when:
        MethodKt.toMethods(file, Language.KOTLIN)

        then:
        KlutterException e = thrown()
        e.message == "Failed to convert datatype. Lists may no contains null values: 'List<String?>'"

        where:
        classBody = """
        package foo.bar.baz

        import dev.buijs.klutter.annotations.Annotations
        
        class FakeClass {
            @KlutterAdaptee(name = "DartMaul")
            fun foo(): String {
                return "Maul"
            }
        
            @KlutterAdaptee(name = "BabyYoda")
            fun fooBar(): List<String?> {
                return listOf("baz")
            }
        }
        
        @Serializable
        @KlutterResponse
        enum class {
            @SerialName("boom") BOOM,
            @SerialName("boom boom") BOOM_BOOM,
        }
        """
    }

    def "[toMethod] returns empty list if class name is undetermined"() {
        given:
        def file = Files.createTempFile("SomeClass", "kt").toFile()

        and:
        file.write(classBody)

        expect:
        MethodKt.toMethods(file, Language.KOTLIN).isEmpty()

        where:
        classBody = """
        package foo.bar.baz

        import dev.buijs.klutter.annotations.Annotations
     
        @KlutterAdaptee(name = "DartMaul")
        fun foo(): String {
            return "Maul"
        }
    
        @KlutterAdaptee(name = "BabyYoda")
        fun fooBar(): List<String?> {
            return listOf("baz")
        }
        
        @Serializable
        @KlutterResponse
        enum class {
            @SerialName("boom") BOOM,
            @SerialName("boom boom") BOOM_BOOM,
        }
        """
    }

    def "[packageName] returns null if file contains no package name"() {
        expect:
        MethodKt.packageName("") == ""
    }

    def "[packageName] package name is returned"() {
        expect:
        MethodKt.packageName("package a.b.c") == "a.b.c"
    }

    def "[toChannelName] pluginPackage name is returned"() {
        given:
        Pubspec pubspec = new Pubspec("Na!", new PubspecFlutter(plugin))

        expect:
        MethodKt.toChannelName(pubspec) == expected

        where:
        plugin << [
                null,
                new PubspecPlugin(null),
                new PubspecPlugin(new PubspecPluginPlatforms(null, null)),
                new PubspecPlugin(new PubspecPluginPlatforms(new PubspecPluginClass(null, null), null)),
                new PubspecPlugin(new PubspecPluginPlatforms(new PubspecPluginClass("Batman!", null), null))
        ]

        expected << [
                "Na!.klutter",
                "Na!.klutter",
                "Na!.klutter",
                "Na!.klutter",
                "Batman!",
        ]
    }
}