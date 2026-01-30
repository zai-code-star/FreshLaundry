plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.freshlaundry.admin"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.example.freshlaundry.admin"
        minSdk = 24
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packagingOptions {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("androidx.cardview:cardview:1.0.0")
// Firebase Authentication
    implementation("com.google.firebase:firebase-auth:22.3.0")

// Firebase Realtime Database
    implementation("com.google.firebase:firebase-database:20.3.0")

// Play Services Location (untuk ambil koordinat GPS)
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("org.osmdroid:osmdroid-android:6.1.12")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")

    implementation("com.github.MKergall:osmbonuspack:6.9.0")

    implementation ("com.google.firebase:firebase-messaging:23.0.0")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.google.auth:google-auth-library-oauth2-http:1.19.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}