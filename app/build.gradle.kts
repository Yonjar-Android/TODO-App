plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-kapt")
    alias(libs.plugins.google.dagger.hilt.android)
}

android {
    namespace = "com.example.todoapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.todoapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.example.todoapp.CustomTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE",
                "META-INF/NOTICE.md",
                "META-INF/NOTICE",
                "META-INF/ASL2.0",
                "META-INF/LICENSE-notice.md"
            )
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

    dependencies {

        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.material3)
        testImplementation(libs.junit)
        testImplementation(libs.junit.jupiter)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.ui.test.junit4)
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)

        // Mockk library para mockear en los test de ui

        androidTestImplementation(libs.mockk.android)

        // Librerías de testing de dagger hilt
        androidTestImplementation(libs.hilt.android.testing)
        kaptAndroidTest(libs.hilt.android.compiler)

        // Mockito y turbine
        testImplementation(libs.mockito.core)
        testImplementation(libs.mockito.kotlin)

        testImplementation(libs.kotlinx.coroutines.test)
        testImplementation(libs.turbine)

        // Firebase

        implementation(platform(libs.firebase.bom))
        implementation(libs.firebase.auth)
        implementation(libs.firebase.firestore)

        // Dagger Hilt
        implementation(libs.hilt.android)
        kapt(libs.hilt.compiler)

        // Splash Screen

        implementation(libs.androidx.core.splashscreen)

        // Navigation

        implementation(libs.androidx.navigation.compose)

        // Constraint Layout

        implementation(libs.androidx.constraintlayout.compose)
    }

    kapt {
        correctErrorTypes
    }
}
