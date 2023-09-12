// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
        classpath("com.google.gms:google-services:4.3.15")
    }
}

plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
    id("io.realm.kotlin") version "1.10.2" apply false

    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.com.android.library) apply false

}