// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
// In your root build.gradle.kts
buildscript {
    repositories {
        google()  // Required for Firebase
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.0")  // Check latest version
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        classpath("com.google.gms:google-services:4.4.0")  // Firebase plugin
    }
}