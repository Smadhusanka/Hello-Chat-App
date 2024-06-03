plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.hello"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.hello"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:32.7.1")) //firebase

    implementation("com.google.firebase:firebase-auth") //firebase authentication

    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2") //firebase store

    implementation("com.hbb20:ccp:2.5.0") //country code picker

    implementation ("com.github.dhaval2404:imagepicker:2.1") //image picker for pic images from device
    implementation ("com.github.bumptech.glide:glide:4.16.0") //image picker for pic images from device

    implementation ("com.github.ZEGOCLOUD:zego_uikit_prebuilt_call_android:+") //zego audio and video
    implementation ("com.github.ZEGOCLOUD:zego_uikit_signaling_plugin_android:2.4.0") //zego audio and video
}