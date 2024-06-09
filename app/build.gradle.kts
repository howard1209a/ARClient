plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.narc.arclient"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.narc.arclient"
        minSdk = 31
        targetSdk = 34
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}


dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.google.mediapipe:tasks-vision:latest.release")

    // 引入aar文件
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))

// 引入其他依赖项
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("androidx.fragment:fragment-ktx:1.5.3")
    implementation("androidx.activity:activity-ktx:1.3.0-alpha08")
    implementation("androidx.recyclerview:recyclerview:1.1.0")

// kotlin & coroutines
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

}
