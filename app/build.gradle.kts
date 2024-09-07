import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

android {
    namespace = "com.fotcamp.finhub"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.fotcamp.finhub"
        minSdk = 30
        targetSdk = 34
        versionCode = 13
        versionName = "1.0.13"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val googleKey = localProperties.getProperty("GOOGLE_CLIENT_ID") ?: ""
        buildConfigField("String", "GOOGLE_CLIENT_ID", googleKey)

        val kakaoKey = localProperties.getProperty("KAKAO_SDK_KEY") ?: ""
        manifestPlaceholders["KAKAO_SDK_KEY"] = kakaoKey
        buildConfigField("String", "KAKAO_SDK_KEY", kakaoKey)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["appName"] = "@string/app_name"
            buildConfigField("String", "BASE_URL", "\"https://main.fin-hub.co.kr/\"")
        }
        debug {
            manifestPlaceholders["appName"] = "@string/app_name_dev"
            applicationIdSuffix = ".debug"
            buildConfigField("String", "BASE_URL", "\"https://dev-main.fin-hub.co.kr/\"")

            val kakaoKey = localProperties.getProperty("KAKAO_SDK_DEV_KEY") ?: ""
            manifestPlaceholders["KAKAO_SDK_KEY"] = kakaoKey
            buildConfigField("String", "KAKAO_SDK_KEY", kakaoKey)
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
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging:21.1.0")
    implementation("com.google.firebase:firebase-config")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.61")

    implementation("com.kakao.sdk:v2-user:2.20.5")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
}