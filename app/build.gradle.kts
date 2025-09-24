plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.kmcounty.ridepricing"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kmcounty.ridepricing"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Room schema export
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
        }
        
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // Signing configuration will be added via CI/CD
            signingConfig = signingConfigs.getByName("debug") // Placeholder
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    // Modules
    implementation(project(":core"))
    implementation(project(":ml"))

    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // ML Kit (on-device text recognition)
    implementation("com.google.mlkit:text-recognition:16.0.0")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("com.google.truth:truth:1.1.4")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("androidx.room:room-testing:2.6.1")

    // Android Testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.48")

    // Debug tools
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
