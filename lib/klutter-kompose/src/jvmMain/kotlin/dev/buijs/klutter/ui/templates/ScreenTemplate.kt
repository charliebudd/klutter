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

package dev.buijs.klutter.ui.templates

import dev.buijs.klutter.kore.KlutterPrinter
import dev.buijs.klutter.ui.Kompose
import dev.buijs.klutter.ui.KomposeBody
import dev.buijs.klutter.ui.controller.NoController
import dev.buijs.klutter.ui.event.Empty

/**
 *
 */
class ScreenTemplate(
    /**
     * Name of generated Flutter class.
     */
    private val name: String,

    /**
     * Data to generate Flutter class.
     */
    private val view: KomposeBody,
): KlutterPrinter {

    private val stateTypeSet: Set<String> =
        unwrapMutableKomposeChildren(mutableListOf(view))
            .asSequence()
            .distinctBy { it.stateType() }
            .filter { it.eventType != Empty::class.qualifiedName }
            .filter { it.controllerType != NoController::class.qualifiedName }
            .map { it.stateType() }
            .toSet()

    private val controllerTypeSet: Set<String> =
        unwrapMutableKomposeChildren(mutableListOf(view))
            .asSequence()
            .distinctBy { it.stateType() }
            .filter { it.eventType != Empty::class.qualifiedName }
            .filter { it.controllerType != NoController::class.qualifiedName }
            .map { it.controllerType }
            .toSet()

    override fun print(): String {
        return """
            |// Copyright (c) 2021 - 2022 Buijs Software
            |//
            |// Permission is hereby granted, free of charge, to any person obtaining a copy
            |// of this software and associated documentation files (the "Software"), to deal
            |// in the Software without restriction, including without limitation the rights
            |// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
            |// copies of the Software, and to permit persons to whom the Software is
            |// furnished to do so, subject to the following conditions:
            |//
            |// The above copyright notice and this permission notice shall be included in all
            |// copies or substantial portions of the Software.
            |//
            |// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
            |// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
            |// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
            |// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
            |// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
            |// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
            |// SOFTWARE.
            |import "dart:convert";
            |import "package:flutter/widgets.dart";
            |import "package:flutter_platform_widgets/flutter_platform_widgets.dart";
            |import "package:kompose_app_backend/kompose_app_backend.dart";
            |
            |import "kompose_navigator.dart";
            |
            |/// Autogenerated by Klutter Framework with Kompose.
            |///
            |/// PUB docs: https://pub.dev/packages/klutter
            |/// 
            |/// Generated UI class for screen named $name.
            |class $name extends StatelessWidget {
            |
            |  /// Construct a new $name instance.
            |  const $name({Key? key}) : super(key: key);
            |
            |  /// Construct a new Home instance and process
            |  /// any data received from the invoking class.
            |  factory $name.kompose(KomposeRouteArgument? argument) {
            |    return const $name();
            |  }
            |
            |  @override
            |  Widget build(BuildContext context) {
            |    return const _${name}View();
            |  }
            |}
            |
            |class _${name}View extends StatefulWidget {
            |  const _${name}View({Key? key}) : super(key: key);
            |
            |  @override
            |  State<_${name}View> createState() => _${name}ViewState();
            |}
            |
            |class _${name}ViewState extends State<_${name}View> {
            |
            |${stateTypeSet.map { it.substringAfterLast(".") }.joinToString("\n") { it.toStateVariable() }}
            |
            |${stateTypeSet.map { it.substringAfterLast(".") }.joinToString("\n") { it.toUpdateState() }}
            |
            |  @override
            |  void initState() {
            |    super.initState();
            |    ${toInitControllers(stateTypeSet, controllerTypeSet)} 
            |  }
            |
            |  @override
            |  void dispose() {
            |    ${controllerTypeSet.joinToString("\n") { it.disposeController()    }}  
            |    super.dispose();
            |  }
            |
            |  @override
            |  Widget build(BuildContext context) {
            |    return ${view.print()};
            |  }
            |}
            |""".trimMargin()
    }

}

private fun String.toStateVariable(): String {
    return """
    |  /// State of $this which is updated everytime
    |  /// an event is processed by the backend.
    |  $this? _${lowercase()}State;
    """.trimMargin()
}

private fun String.toUpdateState(): String {
    return """
      |void _update${this}State(dynamic state) {
      |  setState((){
      |    if(state == null) {
      |        _${this.lowercase()}State = null;
      |      } else {
      |        final dynamic json = jsonDecode(state as String);
      |        _${this.lowercase()}State = ${this}.fromJson(json);
      |      }
      |    });
      |  }
      |""".trimMargin()
}

private fun toInitControllers(states: Set<String>, controllers: Set<String>): String {
    return states.mapIndexed { index, s ->
        s.toInitController(controllers.toMutableList()[index])
    }.joinToString("\n") { it }
}

private fun String.toInitController(controller: String): String {
    return """
    |KomposeAppBackend.initController(
    |    widget: "", 
    |    event: "init", 
    |    data: "", 
    |    controller: "$controller",
    |).then(_update${this.substringAfterLast(".")}State);
    """.trimMargin()
}

private fun String.disposeController(): String {
    return """
    |KomposeAppBackend.disposeController(
    | widget: "",
    |  event: "init",
    |  data: "",
    |  controller: "$this",
    |);
    """.trimMargin()
}

private fun unwrapMutableKomposeChildren(input: MutableList<Kompose<*>>): MutableList<Kompose<*>> {
    val output = mutableListOf<Kompose<*>>()

    input.forEach { kompose ->

        output.add(kompose)

        if(kompose.hasChild()) {
            output.addAll(unwrapMutableKomposeChildren(mutableListOf(kompose.child())))
        }

        if(kompose.hasChildren()) {
            output.addAll(unwrapMutableKomposeChildren(kompose.children().toMutableList()))
        }

    }

    return output

}