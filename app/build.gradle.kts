plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.tuempresa.driverapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.tuempresa.driverapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
secrets {
    // Esta configuración le dice al plugin que use 'local.properties' por defecto.
    // Es perfecto para nuestro caso.
    defaultPropertiesFileName = "local.properties"
}
dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Hilt (🔥 actualizado y completo)
    implementation("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Retrofit + OkHttp + Moshi
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Para convertir JSON a objetos Kotlin
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    // CameraX
    implementation("androidx.camera:camera-core:1.2.2")
    implementation("androidx.camera:camera-camera2:1.2.2")
    implementation("androidx.camera:camera-lifecycle:1.2.2")
    implementation("androidx.camera:camera-view:1.2.2")

    // Maps & Location
    val mapsComposeVersion = "4.4.1"

    // Google Maps Compose (mapas en Compose)
    implementation("com.google.maps.android:maps-compose:$mapsComposeVersion")

    // Utilidades adicionales para Compose (helpers, rutas, animaciones)
    implementation("com.google.maps.android:maps-compose-utils:$mapsComposeVersion")
    //implementation("com.google.maps.android:maps-utils-ktx:3.4.0")
    // Widgets listos para Compose (botones de zoom, brújula, overlays)
    implementation("com.google.maps.android:maps-compose-widgets:$mapsComposeVersion")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    // Ubicación del dispositivo (GPS, ubicación en tiempo real)
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Firebase Auth
    implementation(platform("com.google.firebase:firebase-bom:32.1.1"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")

    // JSON handling
    implementation("org.json:json:20240303")
    implementation("com.squareup.moshi:moshi:1.15.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // TensorFlow Lite
    implementation("org.tensorflow:tensorflow-lite:2.14.0")

    // Coroutines + ViewModel
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")

    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.multidex:multidex:2.0.1") // ✅ AGREGAR ESTO
    //biometrico
    implementation("androidx.biometric:biometric-ktx:1.2.0-alpha05")
    implementation("androidx.appcompat:appcompat:1.7.0")
// En la sección dependencies { ... }
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.airbnb.android:lottie-compose:6.4.1")
}

