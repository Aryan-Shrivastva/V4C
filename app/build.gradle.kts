plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.v4c"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.v4c"
        minSdk = 28
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.car.ui.lib)
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    val lottieVersion = "3.4.0"

    implementation("com.airbnb.android:lottie:$lottieVersion")

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    implementation ("com.github.blackfizz:eazegraph:1.2.5l@aar")
    implementation ("com.nineoldandroids:library:2.4.0")

    implementation ("com.google.code.gson:gson:2.10.1")

    implementation ("com.google.android.gms:play-services-maps:18.0.2")

    implementation ("com.cloudinary:cloudinary-android:2.3.1")
    implementation ("com.cloudinary:cloudinary-core:1.33.0")


}