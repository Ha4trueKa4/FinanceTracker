plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.example.fianancetracker.core.domain"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Flow фигурирует в публичных интерфейсах репозиториев — отдаём как api.
    api(libs.kotlinx.coroutines.android)
}
