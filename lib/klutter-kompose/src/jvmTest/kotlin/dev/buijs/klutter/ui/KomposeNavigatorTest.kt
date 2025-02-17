///* Copyright (c) 2021 - 2022 Buijs Software
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// *
// */
//
//package dev.buijs.klutter.compose
//
//import dev.buijs.klutter.ui.KomposeRoute
//import dev.buijs.klutter.ui.templates.NavigatorTemplate
//import spock.lang.Specification
//
//class KomposeNavigatorSpec extends Specification {
//
//    def "KomposeNavigator should print valid dart code"() {
//        given:
//        def navigator = new NavigatorTemplate([
//                new KomposeRoute(
//                        "home",
//                        "Home",
//                        true,
//                ),
//                new KomposeRoute(
//                        "feed",
//                        "Feed",
//                        false,
//                ),
//        ])
//
//        expect:
//        KomposeTestUtil.verify(navigator, expected1)
//    }
//
//    private static def expected1 =
//            """ import 'package:flutter_platform_widgets/flutter_platform_widgets.dart';
//                import 'package:flutter/widgets.dart';
//
//                import 'home.dart';
//                import 'feed.dart';
//
//                class KomposeNavigator {
//                  KomposeNavigator(BuildContext context) {
//                    onGenerateRoute = (settings) {
//                      switch (settings.name) {
//                        case homeRoute:
//                          return platformPageRoute<PlatformScaffold>(
//                              context: context,
//                              builder: (_) => Home.kompose(KomposeRouteArgument.none));
//                        case feedRoute:
//                          return platformPageRoute<PlatformScaffold>(
//                            context: context,
//                            builder: (_) => Feed.kompose(
//                              FeedKomposeRouteArgument.orNull(settings.arguments),
//                            ),
//                          );
//                        default:
//                          return platformPageRoute<PlatformScaffold>(
//                            context: context,
//                            builder: (_) => PlatformScaffold(
//                              body: Center(
//                                child: Text("No route defined for \${settings.name}"),
//                              ),
//                            ),
//                          );
//                      }
//                    };
//                  }
//
//                  static const String homeRoute = '/home';
//                  static const String feedRoute = '/feed';
//
//                  late final RouteFactory onGenerateRoute;
//                }
//
//                abstract class KomposeRouteArgument<T> {
//                  dynamic argument;
//
//                  T dekompose();
//
//                  static KomposeRouteArgument? get none => null;
//                } """
//
//}