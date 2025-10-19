plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    
}


dependencies {
    // Firebase BoM (Bill of Materials) for version management
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // Firebase dependencies (no version needed when using BoM)
    implementation("com.google.firebase:firebase-common-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")  // Example
}

android {
    namespace = "com.example.selfcare"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.selfcare"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.material)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.ui.text)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.activity)
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation ("androidx.compose.material3:material3:1.1.0" ) // for Material3
    implementation ("androidx.compose.material:material:1.4.3")
    implementation("com.google.accompanist:accompanist-pager:0.34.0")
    implementation ("com.jakewharton.threetenabp:threetenabp:1.4.4")
    implementation ("androidx.compose.material:material-icons-extended")
    implementation ("androidx.compose.foundation:foundation:1.4.3")
    implementation ("androidx.compose.ui:ui:1.4.3")
    implementation ("androidx.compose.material3:material3:1.0.1")
    implementation ("androidx.compose.material3:material3:1.0.1")
    implementation ("com.google.accompanist:accompanist-pager:0.28.0")
    implementation ("androidx.compose.foundation:foundation:1.3.0")



}

