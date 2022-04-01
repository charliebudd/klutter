package dev.buijs.klutter.core

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories

data class KlutterTestProject(
    val projectDir: Path = Files.createTempDirectory(""),
    val buildSrc: File = projectDir.resolve("buildSrc")
        .toAbsolutePath()
        .toFile()
        .also { it.mkdir() },
    val iosDir: File = projectDir.resolve("ios")
        .toAbsolutePath()
        .toFile()
        .also { it.mkdir() },
    val iosRunnerDir: File = iosDir.resolve("Runner")
        .absoluteFile
        .also { it.mkdir() },
    val iosInfoPlist: File = iosRunnerDir.resolve("Info.plist")
        .absoluteFile
        .also { it.createNewFile() },
    val iosAppDelegate: File = iosRunnerDir.resolve("AppDelegate.swift")
        .absoluteFile
        .also { it.createNewFile() },
    val iosFlutterDir: File = iosDir.resolve("Flutter")
        .absoluteFile
        .also { it.mkdir() },
    val appFrameworkInfoPlist: File = iosFlutterDir.resolve("AppFrameworkInfo.plist")
        .absoluteFile
        .also { it.createNewFile() },
    val androidDir: File = projectDir.resolve("android")
        .toAbsolutePath()
        .toFile()
        .also { it.mkdirs()},
    val androidAppDir: File = projectDir.resolve("android/app")
        .toAbsolutePath()
        .toFile()
        .also { it.mkdirs() }
        .also { projectDir.resolve("android/app/src/main").createDirectories() },
    val androidAppManifest: File = projectDir.resolve("android/app/src/main/AndroidManifest.xml")
        .toAbsolutePath()
        .toFile()
        .also { it.createNewFile()},
    val platformDir: File = projectDir.resolve("platform")
        .toAbsolutePath()
        .toFile()
        .also { it.mkdirs()},
    val platformSourceDir: File = projectDir.resolve("platform/src/commonMain")
        .toAbsolutePath()
        .toFile()
        .also { it.mkdirs()},
    val flutterDir: File = projectDir.resolve("lib")
        .toAbsolutePath()
        .toFile()
        .also { it.mkdirs() },
    val flutterMainFile: File = flutterDir.resolve("main.dart")
        .toPath()
        .toAbsolutePath()
        .toFile()
        .also { it.createNewFile() },
    val flutterPubspec: File = projectDir.resolve("pubspec.yaml")
        .toAbsolutePath()
        .toFile()
        .also { it.createNewFile() },
    val buildGradle: File = projectDir.resolve("build.gradle.kts")
        .toAbsolutePath()
        .toFile()
        .also { it.createNewFile() },
    val settingsGradle: File = projectDir.resolve("settings.gradle.kts")
        .toAbsolutePath()
        .toFile()
        .also { it.createNewFile() },
)