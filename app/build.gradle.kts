plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kapt)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

// Define version components
val versionMajor = 1
val versionMinor = 1
val versionPatch = 0
val isBeta = false


android {
    namespace = "com.roaa.klef"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.roaa.klef"
        minSdk = 24
        targetSdk = 36
        versionCode = versionMajor * 10000 + versionMinor * 100 + versionPatch
        versionName =
            "${versionMajor}.${versionMinor}.${versionPatch}" + if (isBeta) "-beta" else ""
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            versionNameSuffix = "-debug"
            resValue("string", "app_name", "(Debug)")
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL" // generates symbols for upload
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // Project modules
    implementation(project(":presentation"))
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")

    // Core AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.biometric)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.constraintlayout.compose)

    // Navigation 3
    implementation(libs.bundles.navigation)

    // Hilt
    implementation(libs.androidx.hilt.android)
    implementation(libs.androidx.hilt.compose)
    kapt(libs.androidx.hilt.compiler)

    // Image loading
    implementation(libs.bundles.coil)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.android.test)
    androidTestImplementation(libs.bundles.compose.test)
    debugImplementation(libs.bundles.compose.debug)
    implementation(libs.material.kolor)
}

hilt {
    enableAggregatingTask = false
}

kapt {
    correctErrorTypes = true
}