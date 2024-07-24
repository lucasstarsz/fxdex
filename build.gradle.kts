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

plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.lucasstarsz"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.json:json:20240303")
    implementation("com.google.inject:guice:7.0.0")
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")
    implementation("io.github.mkpaz:atlantafx-base:2.0.1")

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
