plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.droiddesign"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.droiddesign"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true
    }
    buildFeatures {
        viewBinding = true
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

    implementation("androidx.credentials:credentials:1.3.0-alpha02")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.android.material:material:1.11.0")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-firestore:24.11.0")
    implementation("com.google.firebase:firebase-auth:22.3.1")

    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.6.10"))
    implementation("com.google.android.libraries.places:places:3.4.0")



    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("androidx.activity:activity:1.8.2")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("androidx.test:core:1.5.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.3")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.squareup.picasso:picasso:2.71828")

    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")


    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation ("androidx.multidex:multidex:2.0.1")
    implementation ("com.google.android.gms:play-services-base:18.3.0")

    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.8.1") // or a later version
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.8.1") // or a later version
    testImplementation ("org.mockito:mockito-junit-jupiter:3.6.0") // or a later version

    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.github.dhaval2404:imagepicker:2.1")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation ("com.github.Drjacky:ImagePicker:2.3.22")
    implementation ("org.greenrobot:eventbus:3.1.1")

    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("com.squareup.okhttp3:okhttp:")
    implementation ("com.google.firebase:firebase-messaging-directboot:23.4.1")
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))

    // Add these lines to your app-level build.gradle dependencies section
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    // Add the dependencies for the Firebase Cloud Messaging and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    testImplementation ("org.robolectric:robolectric:4.5.1")

    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("org.mockito:mockito-core:5.11.0")

    androidTestImplementation ("org.powermock:powermock-module-junit4:2.0.9")
    androidTestImplementation ("org.powermock:powermock-api-mockito2:2.0.9")
    testImplementation ("org.powermock:powermock-classloading-xstream:2.0.9")
// The xstream classloader is often not needed directly; you can usually use the default one
    androidTestImplementation ("org.powermock:powermock-classloading-base:2.0.9")
    implementation ("org.mockito:mockito-android:5.11.0")
    androidTestImplementation ("androidx.test.espresso:espresso-contrib:3.5.1"){
        exclude("com.google.protobuf", "protobuf-lite")
    }
    androidTestImplementation ("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation ("androidx.test:rules:1.5.0")
    implementation ("com.google.protobuf:protobuf-javalite:3.22.3")
    androidTestImplementation ("androidx.test.uiautomator:uiautomator:2.3.0")
}