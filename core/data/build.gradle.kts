import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
}

val secretsFile = rootProject.file("local.properties")
val secrets = Properties().apply {
    if (secretsFile.exists()) load(secretsFile.inputStream())
}

android {
    namespace = "com.example.fianancetracker.core.data"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        buildConfigField("String", "TOKEN", "\"${secrets.getProperty("TOKEN", "")}\"")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":core:domain"))

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.koin.android)
    implementation(libs.kotlinx.coroutines.android)
}
