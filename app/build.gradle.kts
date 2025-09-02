plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services) // Hinzugefügt
}

android {
    namespace = "de.lshorizon.pawplan"
    compileSdk = 36

    defaultConfig {
        applicationId = "de.lshorizon.pawplan"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play)
    implementation(libs.google.identity.googleid)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.airbnb.lottie.compose)

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1") // Hinzugefügt

    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.1.2")) // Hinzugefügt

    // Firebase Services
    implementation("com.google.firebase:firebase-auth-ktx") // Hinzugefügt
    implementation("com.google.firebase:firebase-firestore-ktx") // Hinzugefügt
    implementation("com.google.firebase:firebase-storage-ktx") // Hinzugefügt

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.2.0") // Hinzugefügt

    // Navigation & ViewModel Compose
    implementation("androidx.navigation:navigation-compose:2.7.7") // Hinzugefügt
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0") // Hinzugefügt


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
