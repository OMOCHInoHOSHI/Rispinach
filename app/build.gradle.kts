import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // Pythonの導入により追加
    id("com.chaquo.python")

    // MapsSDK
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

    // Firebaseプラグインの追加
    id("com.google.gms.google-services")
}

android {
    namespace = "io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach"
    compileSdk = 35

    // Pythonにより追加
    flavorDimensions += "pyVersion"
    productFlavors {
        create("py38") { dimension = "pyVersion" }
    }

    defaultConfig {

        // Pythonにより追加
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64", "armeabi-v7a", "x86")
        }

        applicationId = "io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // map追記
        val mapsApiKey = rootProject.properties["MAPS_API_KEY"] as? String ?: ""
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey

        // local.properties ファイルからプロパティを読み込む
        val localProperties = Properties()
        localProperties.load(rootProject.file("local.properties").inputStream())

        // build.gradle ファイルに API キーを追加
        buildConfigField("String", "MAPS_API_KEY", "\"${localProperties.getProperty("MAPS_API_KEY")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        mlModelBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Pythonにより追加
chaquopy {
    defaultConfig {
        version = "3.8"
    }
    productFlavors {
        getByName("py38") { version = "3.8" }
    }
    sourceSets { }
}

dependencies {

    //map
    implementation("com.google.maps.android:maps-compose:6.1.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    //implementation(libs.firebase.auth.common)//重複している可能性があるため一旦削除してます

    //カメラライブラリS---------------------------------------------------------
    val cameraxVersion = "1.3.4"    //変数
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.video)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)
    //カメラライブラリE---------------------------------------------------------

    //アイコン関係S----------------------------------------------------------------------
    implementation(libs.material3)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended.android)
    //アイコン関係E----------------------------------------------------------------------

    // 既存の依存関係
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation("androidx.compose.material:material-icons-extended:1.7.4") //これ追加
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.camera.core)
    //implementation(libs.litert)       // tensorflow-liteの使用のためコメントアウト
    //implementation(libs.litert.support.api)       // tensorflow-liteの使用のためコメントアウト
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // tensorflow-liteの依存関係を追加S----------------------------------------------------------------------
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)
    // tensorflow-liteの依存関係を追加E----------------------------------------------------------------------

    // Firebaseの依存関係を追加S----------------------------------------------------------------------
    implementation(platform("com.google.firebase:firebase-bom:31.0.2"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-core:16.0.8")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Firebaseの依存関係を追加E---------------------------------------------------------------------


    //Geocodingの依存関係
    implementation("com.squareup.okhttp3:okhttp:4.10.0") // 最新バージョンを確認してください
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4") // 最新バージョンを確認してください
    implementation ("com.google.maps:google-maps-services:0.18.0") // 追加
}