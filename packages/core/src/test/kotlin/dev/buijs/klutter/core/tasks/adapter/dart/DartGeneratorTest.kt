package dev.buijs.klutter.core.tasks.adapter.dart


import dev.buijs.klutter.core.*
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.junit.Ignore
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory

/**
 * @author Gillian Buijs
 */
@Ignore //Fails in GitHub
class DartGeneratorTest: WordSpec({

    val projectDir = createTempDirectory("").also {
        it.createDirectories()
    }
    val flutterDir = projectDir.resolve("lib").toFile().also {
        it.mkdir()
    }

    "When using the DartGenerator" should {
        "It should write a valid Dart file" {

            //Given: A DartObject
            val objects = DartObjects(
                messages = listOf(
                    DartMessage(
                        name = "Shazaam",
                        fields = listOf(
                            DartField(
                                name = "age",
                                dataType = DartKotlinMap.INTEGER.dartType,
                                optional = false,
                                isList = false,
                            ),
                            DartField(
                                name = "friends",
                                dataType = DartKotlinMap.INTEGER.dartType,
                                optional = true,
                                isList = false,
                            ),
                            DartField(
                                name = "bff",
                                dataType = DartKotlinMap.STRING.dartType,
                                optional = true,
                                isList = false,
                            ),
                            DartField(
                                name = "powers",
                                dataType = "",
                                customDataType = "Power",
                                optional = false,
                                isList = true,
                            ),
                        )
                    )
                ),
                enumerations = listOf(
                    DartEnum(
                        name = "Power",
                        values = listOf(
                            "SUPER_STRENGTH",
                            "SUPER_FUNNY"
                        ),
                        jsonValues = emptyList()
                    )
                )
            )

            //And: A DartGenerator
            val generator = DartGenerator(
                flutter = Flutter(
                    file = flutterDir,
                    root = Root(file = projectDir.toFile())
                ),
                objects = objects
            )

            //When:
            generator.generate()

            //Then:
            val actual = flutterDir.resolve("generated/messages.Dart").also {
                it.exists()
            }

            actual.readText().filter { !it.isWhitespace() }  shouldBe """
                /// Autogenerated by Klutter
                /// Do net edit directly, 
                /// but recommended to store in VCS.
                
                class Shazaam {
                  
                  Shazaam({
                    required this.age,
                    required this.powers,
                    this.friends,
                    this.bff,
                  });
                  
                factory Shazaam.fromJson(dynamic json) {
                   return Shazaam (
                     age: json['age'].toInt(),
                     friends: json['friends']?.toInt(),
                     bff: json['bff']?.toString(),
                     powers: List<Power>.from(json['powers'].map((o) => Power.fromJson(o))),
                   );
                 }
                
                 final int age;
                 final List<Power> powers;
                 int? friends;
                 String? bff;
                
                 Map<String, dynamic> toJson() {
                   return {
                     'age': age,
                     'friends': friends,
                     'bff': bff,
                     'powers': powers.map((o) => o.toJson()).toList()
                   };
                 }  
                }
                
                class Power {
                final String string;
                
                const Power._(this.string);  static const superStrength = Power._('SUPER_STRENGTH');
                  static const superFunny = Power._('SUPER_FUNNY');
                  static const none = Power._('none');
                
                
                static const values = [superStrength,superFunny];
                
                  @override
                  String toString() {
                    return 'Power.${dollar}string';
                  }
                
                  static Power fromJson(String value) {
                    switch(value) {
                      case "SUPER_STRENGTH": return Power.superStrength;
                      case "SUPER_FUNNY": return Power.superFunny;
                      default: return Power.none;
                    }
                 }
                
                  String? toJson() {
                    switch(this) { 
                      case Power.superStrength: return "SUPER_STRENGTH";
                      case Power.superFunny: return "SUPER_FUNNY";
                      default: return null;
                    }
                  }
                }
            """.filter { !it.isWhitespace() }

        }

        "It should write standard types correctly" {

            //Given:
            val fields = listOf(
                DartField(
                    name = "aString",
                    dataType = DartKotlinMap.STRING.dartType,
                    optional = false,
                    isList = false,
                ),
                DartField(
                    name = "bInt",
                    dataType = DartKotlinMap.INTEGER.dartType,
                    optional = true,
                    isList = false,
                ),
                DartField(
                    name = "cDouble",
                    dataType = DartKotlinMap.DOUBLE.dartType,
                    optional = true,
                    isList = true,
                ),
                DartField(
                    name = "dBoolean",
                    dataType = DartKotlinMap.BOOLEAN.dartType,
                    optional = false,
                    isList = true,
                ),

            )

            //Given: A DartObject
            val objects = DartObjects(
                messages = listOf(
                    DartMessage(
                        name = "Shazaam",
                        fields = fields
                    ),
                ),
                enumerations = emptyList()
            )

            //And: A DartGenerator
            val generator = DartGenerator(
                flutter = Flutter(
                    file = flutterDir,
                    root = Root(file = projectDir.toFile())
                ),
                objects = objects
            )

            //When:
            generator.generate()

            //Then:
            val actual = flutterDir.resolve("generated/messages.Dart").also {
                it.exists()
            }

            actual.readText().filter { !it.isWhitespace() }  shouldBe """
                /// Autogenerated by Klutter
                /// Do net edit directly, 
                /// but recommended to store in VCS.
                
                class Shazaam {
                  
                  Shazaam({
                    required this.aString,
                    required this.dBoolean,
                    this.bInt,
                    this.cDouble,
                  });
                  
                factory Shazaam.fromJson(dynamic json) {
                   return Shazaam (
                     aString: json['aString'].toString(),
                     bInt: json['bInt']?.toInt(),
                     cDouble: json['cDouble'] == null ? [] : List<double>.from(json['cDouble']?.map((o) => o.toDouble())),
                     dBoolean: List<bool>.from(json['dBoolean'].map((o) => o)),
                   );
                 }
                
                 final String aString;
                 final List<bool> dBoolean;
                 int? bInt;
                 List<double>? cDouble;
                
                 Map<String, dynamic> toJson() {
                   return {
                     'aString': aString,
                     'bInt': bInt,
                     'cDouble': cDouble.toList(),
                     'dBoolean': dBoolean.toList()
                   };
                 }  
                }
            """.filter { !it.isWhitespace() }

        }

    }


})

private const val dollar = "$"