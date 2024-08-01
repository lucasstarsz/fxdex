/* Copyright 2024 Andrew Dey

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "3.0.1"
    id("com.gluonhq.gluonfx-gradle-plugin") version "1.0.23"
}

group = "org.lucasstarsz"
version = "0.0.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.json:json:20240303")
    implementation("com.google.inject:guice:7.0.0")
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")
    implementation("io.github.mkpaz:atlantafx-base:2.0.1")
    implementation("org.apache.commons:commons-text:1.12.0")
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
}

javafx {
    version = "17"
    modules("javafx.controls", "javafx.fxml")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }

    modularity.inferModulePath.set(true)
}

application {
    mainModule.set("fxdex.main")
    mainClass.set("io.github.lucasstarsz.fxdex.App")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

val os: OperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()
if (os.isMacOsX) {
    version = "1.0.3"
}

// Packaging & Distribution
val distributionName = "FXDex ${version}"

jlink {
    addExtraDependencies("javafx")
    options.addAll("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    
    

    launcher {
        name = distributionName
        noConsole = false
    }

    jpackage {
        val currentOs = org.gradle.internal.os.OperatingSystem.current()
        skipInstaller = true

        if (!skipInstaller) {
            installerOptions.addAll(
                listOf(
                    "--description", project.description,
                    "--vendor", project.group as String,
                    "--app-version", project.version as String
                )
            )

            when {
                currentOs.isWindows -> {
                    installerType = "msi"
                    installerOptions.addAll(listOf("--win-per-user-install", "--win-dir-chooser", "--win-shortcut"))
                }

                currentOs.isLinux -> {
                    installerType = "deb"
                    installerOptions.addAll(listOf("--linux-shortcut"))
                }

                currentOs.isMacOsX -> {
                    installerType = "pkg"
                    installerOptions.addAll(listOf("--mac-package-name", project.name))
                }
            }
        }
    }
}