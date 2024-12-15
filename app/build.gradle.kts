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
    compileSdk = 34

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
        manifestPlaceholders["MAPS_API_KEY"] = project.property("MAPS_API_KEY").toString()
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
    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-video:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")
    implementation("androidx.camera:camera-extensions:${cameraxVersion}")
    //カメラライブラリE---------------------------------------------------------

    //アイコン関係S----------------------------------------------------------------------
    implementation ("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.compose.material:material-icons-core:1.7.4")
    implementation("androidx.compose.material:material-icons-extended-android:1.7.4")
    //アイコン関係E----------------------------------------------------------------------

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}