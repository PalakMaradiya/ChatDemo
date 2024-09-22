plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.chatdemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.chatdemo"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.firebase.messaging.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //circle imageView

    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //glide
    implementation ("com.github.bumptech.glide:glide:4.12.0")


    //sdp
    implementation ("com.intuit.sdp:sdp-android:1.1.1")

    // ssp
    implementation ("com.intuit.ssp:ssp-android:1.1.1")

    // firebase
    implementation("com.google.firebase:firebase-analytics")
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")

    implementation ("com.google.firebase:firebase-auth:19.4.0")
    implementation ("com.google.android.gms:play-services-auth:18.1.0")

    implementation("com.google.firebase:firebase-database")

    implementation ("com.google.firebase:firebase-messaging:23.0.7")

    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:21.1.0")

    //FireStore
    implementation ("com.google.firebase:firebase-firestore:22.0.1")



}