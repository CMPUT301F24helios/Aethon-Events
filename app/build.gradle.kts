plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.aethoneventsapp"
    compileSdk = 34

    defaultConfig {


        applicationId = "com.example.aethoneventsapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "MAPTILER_API_KEY", "\"HjlQ4E4vEEbxno9Yzmb0\"")
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
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    // map
    implementation ("org.maplibre.gl:android-sdk:10.0.2")
    // location services
    implementation("com.google.android.gms:play-services-location:21.0.1")
    // Firebase BoM for Firestore and Database
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-firestore")
    implementation ("com.google.android.gms:play-services-maps:17.0.1")  // or latest version
    implementation ("com.google.firebase:firebase-firestore:24.4.0") // Make sure this is also added for Firebase
    implementation("com.google.firebase:firebase-database:20.0.5") // Only one Firebase Database version
    // Glide dependency for image loading
    implementation("com.github.bumptech.glide:glide:4.15.1")  // Check for the latest version on Maven
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation(libs.glide)
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    // Android libraries
    implementation("com.google.firebase:firebase-messaging:23.0.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    implementation(libs.espresso.idling.resource)
    testImplementation(libs.espresso.intents)
    testImplementation(libs.testng)  // Check for the latest version on Maven

    // QR code and barcode libraries (keep only one zxing-core)
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.2")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("com.google.firebase:firebase-storage:20.2.0")// Use the latest version



    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.intents)
    //testImplementation(libs.robolectric)
    androidTestImplementation("androidx.test.espresso.idling:idling-concurrent:3.5.1") // For IdlingResource support (Optional for multi-thr

    // Mockito dependencies
    //implementation("org.mockito:mockito-core:5.14.2")
    implementation("org.mockito:mockito-android:5.14.2")

    // For mocking final classes and static methods (inline mock maker)
    //implementation("org.mockito:mockito-inline:4.10.0")
    implementation("com.google.android.gms:play-services-tasks:18.2.0")

    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

}