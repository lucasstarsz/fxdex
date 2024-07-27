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

open module fxdex.main {
    requires java.net.http;

    // required for allowing HTTPS connections on app usage outside the developer's computer.
    requires jdk.crypto.ec;

    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    requires org.json;
    requires org.apache.commons.text;
    requires atlantafx.base;

    requires com.google.guice;
    // required for Guice to function.
    requires jakarta.inject;
}
