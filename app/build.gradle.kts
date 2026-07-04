import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
}

// Lấy số build từ Jenkins, nếu không thấy thì mặc định là 1
val buildNumber = System.getenv("BUILD_NUMBER")?.toIntOrNull() ?: 1

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    signingConfigs {
        create("release") {
            // Dùng biến môi trường KEYSTORE_FILE, nếu không có thì trỏ file local
            storeFile = file(System.getenv("KEYSTORE_FILE") ?: "my_key.jks")
            storePassword = "12345678"
            keyAlias = "key0"
            keyPassword = "12345678"
        }
    }

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 34

        versionCode = buildNumber

        // Tự động tạo versionName: 20260704-01
        val currentDate = SimpleDateFormat("yyyyMMdd").format(Date())
        val paddedBuildNumber = String.format("%02d", buildNumber)
        versionName = "$currentDate-$paddedBuildNumber"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Tự động đổi tên file APK đầu ra
    applicationVariants.all {
        val variant = this
        if (variant.buildType.name == "release") {
            variant.outputs.map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
                .forEach { output ->
                    output.outputFileName = "AlarmApp-${defaultConfig.versionName}.apk"
                }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    implementation("com.google.android.material:material:1.12.0")
}