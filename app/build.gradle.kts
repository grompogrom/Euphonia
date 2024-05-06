plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.euphoiniateam.euphonia"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.euphoiniateam.euphonia"
        minSdk = 33
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
    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    defaultConfig {
        buildConfigField("String", "ServerIP", "\"http://192.168.31.33:8083/\"")
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.compose.ui:ui-android:1.6.3")
    implementation("androidx.compose.ui:ui-tooling-preview-android:1.6.3")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.datastore:datastore-core:1.0.0")
    implementation("androidx.media3:media3-exoplayer:1.3.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.3.0")
    implementation("androidx.media3:media3-ui:1.3.0")
    implementation("com.github.LeffelMania:android-midi-lib:7cdd855c2b")
//    implementation("com.github.atsushieno:ktmidi:1.5.0")
    implementation("dev.atsushieno:ktmidi-android:0.7.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")
    implementation("com.google.android.exoplayer:exoplayer-core:2.19.1")
    implementation("androidx.media3:media3-session:1.3.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.3")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("jp.kshoji:javax-sound-midi:0.0.4")

    implementation("com.google.code.gson:gson:2.10")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.retrofit2:retrofit:2.7.2")
    implementation("com.squareup.retrofit2:converter-gson:2.3.0")
    implementation("androidx.media3:media3-exoplayer-midi:1.2.1")
    implementation("dev.atsushieno:ktmidi-android:0.7.0")
    implementation("jp.kshoji:javax-sound-midi:0.0.4")

    implementation("com.google.code.gson:gson:2.8.6")

    // test
    val androidXTestVersion = "1.5.0"
    androidTestImplementation("androidx.test:runner:$androidXTestVersion")
    androidTestImplementation("androidx.test:rules:$androidXTestVersion")

    // Required -- JUnit 4 framework
    testImplementation("junit:junit:4.13.2")
    // Optional -- Robolectric environment
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.4")

//     Optional -- Mockito framework
//    testImplementation ("org.mockito:mockito-core:5.3.0")
    // Optional -- mockito-kotlin
    implementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
//    // Optional -- Mockk framework
//    testImplementation("io.mockk:mockk:1.13.0")
}
