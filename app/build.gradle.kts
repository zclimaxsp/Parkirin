import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.netra.parkirin.officer"
    compileSdk = 36

    lint {
        disable += "FlowOperatorInvokedInComposition"
        abortOnError = false
    }

    defaultConfig {
        applicationId = "com.netra.parkirin.officer"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }
        buildConfigField("String", "BASE_URL", "\"${properties.getProperty("BASE_URL", "http://localhost:8080/")}\"")
        buildConfigField("String", "AUTH_BASE_URL", "\"${properties.getProperty("AUTH_BASE_URL", "http://localhost:9000/")}\"")
        buildConfigField("String", "OAUTH_CLIENT_ID", "\"${properties.getProperty("OAUTH_CLIENT_ID", "parkirin-officer")}\"")
        buildConfigField("String", "OAUTH_REDIRECT_URI", "\"${properties.getProperty("OAUTH_REDIRECT_URI", "com.netra.parkirin.officer://oauth/callback")}\"")
        manifestPlaceholders["appAuthRedirectScheme"] = "com.netra.parkirin.officer"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.3")

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.3")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    // Hilt DI
    implementation("com.google.dagger:hilt-android:2.52")
    ksp("com.google.dagger:hilt-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // CameraX
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")

    // ML Kit OCR
    implementation("com.google.mlkit:text-recognition:16.0.1")

    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")

    // System UI Controller
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // Datastore (token storage)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // AppAuth (OAuth2 PKCE)
    implementation("net.openid:appauth:0.11.1")

    // Coil
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Date/Time
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
