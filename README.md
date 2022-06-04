[![GitHub license](https://img.shields.io/github/license/buijs-dev/klutter-dart?color=black&logoColor=black)](https://github.com/buijs-dev/klutter-dart/blob/main/LICENSE) 
[![codecov](https://codecov.io/gh/buijs-dev/klutter-dart/branch/main/graph/badge.svg?token=AS154MVM51)](https://codecov.io/gh/buijs-dev/klutter-dart)

The Klutter Framework makes it possible to write a Flutter plugin for both Android 
and iOS using [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html). 
Instead of writing platform specific code twice in 2 languages (Swift + Kotlin), 
it can be written once in Kotlin and used as a Flutter plugin.

<B>Important</B>: Klutter is in alpha and not yet published to PUB.

## Getting started
1. [Installation](#Installation)
2. [Use plugins](#Use%20plugins)
3. [Create plugins](#Create%20plugins)

#### Installation
Add the Klutter library to dev_dependencies in the pubspec.yaml:

```yaml  
dev_dependencies:  
 klutter: ^0.1.0
 ```  
  
Then run:  
  
```shell  
flutter pub get
```  
  
#### Use plugins  
Plugins build with the Klutter Framework work slightly different than regular plugins.  
First [install](#Installation) the dev dependency. Next run the android command to setup Gradle:  
  
```shell  
flutter pub run klutter:android
```  
  
This task will do the following for your Flutter project:  
 1. Create a .klutter-plugins file in the root folder.  
 2. Create a new Gradle file in the flutter/packages/flutter_tools/gradle.  
 3. Update the android/settings.gradle file to apply the newly generated Gradle file.  
 4. Update the min/compile/target SDK versions to 21/31/31 in the android/app/build.gradle file. 
  
The .klutter-plugins file will register all Klutter made plugins used in your project.  
The created Gradle file in the flutter_tools manages the plugins and enables them to  
be found by the Flutter project.  
  
That's it! Now you can use plugins made with Klutter in your Flutter application by   
adding them to the dependencies in the pubspec.yaml. Register it in your project  
by running klutter:add. For example to add the library 'awesome_plugin' to your project  
run:  
  
```shell  
flutter pub run klutter:add awesome_plugin 
```  
  
This will add the awesome_plugin to the .klutter-plugins file.  

#### Create plugins  

 1. Create a new Flutter plugin project. Either use the wizard in Android Studio or run the following command, 
    substituting 'org.example' with your organisation name and  'plugin_name' with your plugin name:
  
```shell  
flutter create --org com.example --template=plugin --platforms=android,ios plugin_name
```  
  
  2. [Install](#Installation) the dev dependency.
  3. Initialize Klutter in your project:

```shell  
flutter pub run klutter:plugin create  
```  

 4. Build the platform module by running the following in the root folder:

```shell
 ./gradlew installPlatform
```

 5. Generate the plugin Dart code:

```shell
./gradlew generateAdapters
```

 6. To test the plugin run the steps outlined [here](#Use%20plugins) in the root/example project.

When done you can run the example project from the root/example/lib folder and see your first plugin in action.
