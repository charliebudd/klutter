package dev.buijs.klutter.kore.shared


import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.shared.DartFieldKt
import dev.buijs.klutter.kore.shared.Data
import mu.KLogger
import spock.lang.Specification

@SuppressWarnings("GroovyAccessibility")
class DartFieldSpec extends Specification {

    def "Processing a a String that does not match the regex returns null"() {
        given:
        def logger = Stub(KLogger)

        when:
        DartFieldKt.log = logger

        then:
        DartFieldKt.toDartField(line) == null
        logger.debug { _ }

        where:
        line << ["", "WHAT", "WOULD", "BATMAN", "DO?", "val foo!=mc2"]
    }

    def "Verify processing returns a DartField"() {
        expect:
        with(DartFieldKt.toDartField(input)) {
            it.name == name
            it.type == type
            it.isList() == isList
            it.isOptional() == isOptional
            it.isCustomType() == isCustomType
        }

        where:
        input                       | type              | name      | isList    | isOptional    | isCustomType
        "val foo: List<Sidekick>"   | "Sidekick"        | "foo"     | true      | false         | true
        "val foo: List<String>"     | "String"          | "foo"     | true      | false         | false
        "val foo: List<Sidekick>?"  | "Sidekick"        | "foo"     | true      | true          | true
        "val foo: List<String>?"    | "String"          | "foo"     | true      | true          | false
        "val foo: Sidekick"         | "Sidekick"        | "foo"     | false     | false         | true
        "val foo: String"           | "String"          | "foo"     | false     | false         | false
        "val foo: Sidekick?"        | "Sidekick"        | "foo"     | false     | true          | true
        "val foo: String?"          | "String"          | "foo"     | false     | true          | false

    }

    def "[determineName] throws exception if name can not be determined"() {
        given:
        def input = [null, name]

        when:
        DartFieldKt.determineName(input)

        then:
        KlutterException e = thrown()
        e.message == message

        where:
        name           | message
        " "             | "Could not determine name of field."
        ""              | "Could not determine name of field."
        "bla bla   "    | "Name of field is invalid: 'bla bla'"
    }

    def "[determineDataType] throws exception if name can not be determined"() {
        given:
        def data = new Data("", """val foo: String = "" """)

        when:
        DartFieldKt.determineDataType(data)

        then:
        KlutterException e = thrown()
        e.message == "A KlutterResponse DTO can not have default values."

    }



}