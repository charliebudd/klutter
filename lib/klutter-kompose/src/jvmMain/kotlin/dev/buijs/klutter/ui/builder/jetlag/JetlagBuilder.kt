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
package dev.buijs.klutter.ui.builder.jetlag

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.ui.Kompose
import dev.buijs.klutter.kore.shared.KlutterDSLBuilder
import dev.buijs.klutter.ui.KomposeBody

@DslMarker
internal annotation class JetlagBuilderDSLMarker

@JetlagBuilderDSLMarker
class JetlagBuilder: KlutterDSLBuilder {

    private var child: Kompose<*>? = null

    fun SafeArea(lambda: SafeAreaBuilder.() -> Unit) {
        if(child != null) {
            throw KlutterException("Can not set multiple children at top level.")
        }

        child = SafeAreaBuilder().apply(lambda).build()
    }

    fun Scaffold(lambda: ScaffoldBuilder.() -> Unit) {
        if(child != null) {
            throw KlutterException("Can not set multiple children at top level.")
        }
        child = ScaffoldBuilder().apply(lambda).build()
    }

    override fun build() = KomposeBody(
        child = child,
    )

}