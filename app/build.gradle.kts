plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    // Cấu hình ký ứng dụng để có thể cài đặt được file Release
    signingConfigs {
        create("release") {
            // Thay vì trỏ trực tiếp đến file, hãy dùng biến môi trường
            storeFile = file(System.getenv("KEYSTORE_FILE") ?: "my_key.jks")
            storePassword = "12345678"
            keyAlias = "key0"
            keyPassword = "12345678"
        }
    }

    // TỰ ĐỘNG: Lấy số bản build từ Jenkins (mặc định là 1 nếu chạy ở máy cá nhân)
    val jenkinsBuildNumber = System.getenv("BUILD_NUMBER")?.toIntOrNull() ?: 1

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 34

        // TỰ ĐỘNG: Gán giá trị tăng dần theo Jenkins để triệt tiêu cache điện thoại
        versionCode = jenkinsBuildNumber
        versionName = "1.0.$jenkinsBuildNumber"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") { // Phải dùng getByName trong Kotlin DSL
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
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

    // Thư viện Room Database
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    implementation("com.google.android.material:material:1.12.0")
}