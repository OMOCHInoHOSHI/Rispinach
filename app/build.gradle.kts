plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // Pythonの導入により追加
    id("com.chaquo.python")

    // MapsSDK
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "io.github.OMOCHInoHOSHI.Jyoukaisendonn_Rispinach"
    compileSdk = 35

    // Pytnonにより追加
    flavorDimensions += "pyVersion"
    productFlavors{
        create("py312"){dimension = "pyVersion"}
    }

    defaultConfig {

        // Pythonにより追加
        ndk{
            abiFilters += listOf("arm64-v8a", "x86_64")
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
        val mapsApiKey = rootProject.properties["GOOGLE_MAPS_API_KEY"] as? String ?: ""
        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = mapsApiKey
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
chaquopy{
    defaultConfig{

        version = "3.12"
    }
    productFlavors{
        getByName("py312"){version = "3.12"}
    }
    sourceSets{ }
}

dependencies {

    //map
    implementation ("com.google.maps.android:maps-compose:6.1.0")
    implementation ("com.google.android.gms:play-services-maps:19.0.0")

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
    implementation (libs.material3)
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
    implementation("androidx.compose.material:material-icons-extended:1.7.4")//これ追加
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

    // tensorflow-liteの依存関係を追加
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.tensorflow.lite)
    implementation (libs.tensorflow.lite.support)

}