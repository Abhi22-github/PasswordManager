import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kapt)
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.roaa.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        val localProps = Properties().apply {
            load(rootProject.file("local.properties").inputStream())
        }
        val token = localProps.getProperty("LOGO_API_TOKEN", "")
        buildConfigField("String", "LOGO_API_TOKEN", "\"$token\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // Project modules
    implementation(project(":domain"))

    // Core AndroidX
    implementation(libs.androidx.core.ktx)

    // Hilt
    implementation(libs.androidx.hilt.android)
    kapt(libs.androidx.hilt.compiler)

    // Networking (Retrofit + OkHttp + Serialization)
    api(libs.bundles.networking)

    // Room (encrypted with SQLCipher)
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)
    implementation(libs.sqlcipher.android)
    implementation(libs.androidx.sqlite.ktx)

    // Image loading
    implementation(libs.bundles.coil)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.test)
}

kapt {
    correctErrorTypes = true
}