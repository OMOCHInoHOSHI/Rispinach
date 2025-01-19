// プロジェクトレベルの build.gradle.kts

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    // Python導入により追加
    id("com.chaquo.python") version  "15.0.1" apply false

    // Firebaseプラグインの追加
    id("com.google.gms.google-services") version "4.4.2" apply false
}

buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
        classpath("com.google.gms:google-services:4.3.10")
    }
}




