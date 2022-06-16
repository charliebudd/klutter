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

package dev.buijs.klutter.core.template

import dev.buijs.klutter.core.CoreTestUtil
import dev.buijs.klutter.core.TestData
import dev.buijs.klutter.core.templates.AndroidAdapter
import spock.lang.Specification

class AndroidAdapterSpec extends Specification {

    def "AndroidAdapter should create a valid Kotlin class"() {
        given:
        def packageName = "super_plugin"
        def pluginName = "SuperPlugin"
        def channelName = "dev.company.plugins.super_plugins"
        def methods = [TestData.greetingMethod]

        and: "The printer as SUT"
        def adapter = new AndroidAdapter(packageName, pluginName, channelName, methods)

        expect:
        CoreTestUtil.verify(adapter, classBody)

        where:
        classBody = """package super_plugin
            
                        import platform.Greeting
                        import androidx.annotation.NonNull
                        
                        import io.flutter.embedding.engine.plugins.FlutterPlugin
                        import io.flutter.plugin.common.MethodCall
                        import io.flutter.plugin.common.MethodChannel
                        import io.flutter.plugin.common.MethodChannel.MethodCallHandler
                        import io.flutter.plugin.common.MethodChannel.Result
                        import kotlinx.coroutines.CoroutineScope
                        import kotlinx.coroutines.Dispatchers
                        import kotlinx.coroutines.launch
                        
                        /** SuperPlugin */
                        class SuperPlugin: FlutterPlugin, MethodCallHandler {
                          /// The MethodChannel that will the communication between Flutter and native Android
                          ///
                          /// This local reference serves to register the plugin with the Flutter Engine and unregister it
                          /// when the Flutter Engine is detached from the Activity
                          private lateinit var channel : MethodChannel
                           
                          private val mainScope = CoroutineScope(Dispatchers.Main) 
                           
                          override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
                            channel = MethodChannel(flutterPluginBinding.binaryMessenger, "dev.company.plugins.super_plugins")
                            channel.setMethodCallHandler(this)
                          }
                        
                          override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
                                mainScope.launch {
                                   when (call.method) {
                                        "greeting" -> {
                                            result.success(Greeting().greeting())
                                        } 
                                        else -> result.notImplemented()
                                   }
                                }
                          }
                        
                          override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
                            channel.setMethodCallHandler(null)
                          }
                        }
                        """
    }

    def "AndroidAdapter should create a valid Kotlin class when the methods list is empty"() {
        given:
        def packageName = "super_plugin"
        def pluginName = "SuperPlugin"
        def channelName = "dev.company.plugins.super_plugins"
        def methods = []

        and: "The printer as SUT"
        def adapter = new AndroidAdapter(packageName, pluginName, channelName, methods)

        expect:
        CoreTestUtil.verify(adapter, classBody)

        where:
        classBody = """package super_plugin
            
                        import androidx.annotation.NonNull
                        
                        import io.flutter.embedding.engine.plugins.FlutterPlugin
                        import io.flutter.plugin.common.MethodCall
                        import io.flutter.plugin.common.MethodChannel
                        import io.flutter.plugin.common.MethodChannel.MethodCallHandler
                        import io.flutter.plugin.common.MethodChannel.Result
                        import kotlinx.coroutines.CoroutineScope
                        import kotlinx.coroutines.Dispatchers
                        import kotlinx.coroutines.launch
                        
                        /** SuperPlugin */
                        class SuperPlugin: FlutterPlugin, MethodCallHandler {
                          /// The MethodChannel that will the communication between Flutter and native Android
                          ///
                          /// This local reference serves to register the plugin with the Flutter Engine and unregister it
                          /// when the Flutter Engine is detached from the Activity
                          private lateinit var channel : MethodChannel
                           
                          private val mainScope = CoroutineScope(Dispatchers.Main) 
                           
                          override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
                            channel = MethodChannel(flutterPluginBinding.binaryMessenger, "dev.company.plugins.super_plugins")
                            channel.setMethodCallHandler(this)
                          }
                        
                          override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
                                mainScope.launch {
                                   when (call.method) {
                                       return result.notImplemented()
                                   }
                                }
                          }
                        
                          override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
                            channel.setMethodCallHandler(null)
                          }
                        }
                        """
    }

}