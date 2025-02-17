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
package dev.buijs.klutter.kore.template

import dev.buijs.klutter.kore.templates.AndroidAdapterState
import dev.buijs.klutter.kore.test.TestUtil
import spock.lang.Specification

class AndroidAdapterStateSpec extends Specification {

    def "AndroidAdapter should create a valid Kotlin class"() {
        given:
        def packageName = "super_plugin"
        def controllers = ["dev.buijs.klutter.kompose_app.platform.GreetingController"]

        and: "The printer as SUT"
        def adapter = new AndroidAdapterState(packageName, controllers)

        expect:
        TestUtil.verify(adapter.print(), classBody)

        where:
        classBody = """package super_plugin
                
                private var devbuijsklutterkompose_appplatformgreetingcontrollerState: dev.buijs.klutter.kompose_app.platform.GreetingController? = dev.buijs.klutter.kompose_app.platform.GreetingController()
                
                fun getController(controller: String) = when {
                    controller == "dev.buijs.klutter.kompose_app.platform.GreetingController" -> {
                        get_devbuijsklutterkompose_appplatformGreetingControllerState()
                    }
                    else -> throw Exception("No such controller with name '\$controller'")
                }
                
                fun disposeController(controller: String?) {
                    when {
                        controller == "dev.buijs.klutter.kompose_app.platform.GreetingController" -> {
                            dispose_devbuijsklutterkompose_appplatformGreetingControllerState()
                        }
                        else -> throw Exception("No such controller with name '\$controller'")
                    }
                }
                
                private fun dispose_devbuijsklutterkompose_appplatformGreetingControllerState() {
                    devbuijsklutterkompose_appplatformgreetingcontrollerState = null
                }
                
                private fun init_devbuijsklutterkompose_appplatformGreetingControllerState() {
                    devbuijsklutterkompose_appplatformgreetingcontrollerState = dev.buijs.klutter.kompose_app.platform.GreetingController()
                }
                
                private fun get_devbuijsklutterkompose_appplatformGreetingControllerState(): dev.buijs.klutter.kompose_app.platform.GreetingController {
                    if (devbuijsklutterkompose_appplatformgreetingcontrollerState == null) {
                        init_devbuijsklutterkompose_appplatformGreetingControllerState()
                    }
                
                    return devbuijsklutterkompose_appplatformgreetingcontrollerState!!
                }
                """
    }

}