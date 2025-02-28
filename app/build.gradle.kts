import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.compose")
}

val properties = Properties()
properties.load(FileInputStream(rootProject.file("local.properties")))
val naverClientId: String = properties.getProperty("naver_client_id")
val clientId: String = properties.getProperty("client_id")
val clientSecret: String = properties.getProperty("client_secret")

android {
    namespace = "com.wngud.locationalarm"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.wngud.locationalarm"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.0.1"

        buildConfigField("String", "NAVER_CLIENT_ID", naverClientId)
        buildConfigField("String", "CLIENT_ID", clientId)
        buildConfigField("String", "CLIENT_SECRET", clientSecret)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("androidx.activity:activity-ktx:1.9.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // hilt
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-android-compiler:2.51")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Room
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-paging:2.6.1")

    // location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // naver map
    implementation("com.naver.maps:map-sdk:3.18.0")
    implementation("io.github.fornewid:naver-map-compose:1.5.7")
    implementation("io.github.fornewid:naver-map-location:21.0.2")

    implementation("androidx.compose.material:material:1.6.7")

    // permission
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // media3
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")

    // data store
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // lottie
    implementation("com.airbnb.android:lottie-compose:6.6.2")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // coil
    implementation("io.coil-kt:coil-compose:2.6.0")

    // admob
    implementation("com.google.android.gms:play-services-ads:23.6.0")
}