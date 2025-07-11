plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.klivvr.assignment"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.klivvr.assignment"
        minSdk = 21
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

    // Serialization
    implementation(libs.gson)
    // DI[Hilt]
    implementation(libs.hilt.android)
    // Hilt for ViewModel and Compose Navigation
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
    // Lifecycle viewmodel compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Coroutines
    implementation(libs.bundles.coroutines)
    testImplementation(libs.kotlinx.coroutines.test)
    // Paging3
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)
    testImplementation(libs.androidx.paging.common)
    // Turbine[Flow testing]
    testImplementation(libs.turbine)
    // MockK[Unit testing]
    testImplementation(libs.mockk)

}