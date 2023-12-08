plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.griffith.mybuddy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.griffith.mybuddy"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            // Exclude the NOTICE.md file in the META-INF directory from the packaged resources
            excludes.add("META-INF/NOTICE.md")
            // Exclude the LICENSE.md file in the META-INF directory from the packaged resources
            excludes.add("META-INF/LICENSE.md")
        }
    }
}

dependencies {
    /**
     * This line adds the JavaMail API. It’s a set of abstract APIs that model a mail system.
     * The API provides a platform-independent and protocol-independent framework to
     * build mail and messaging applications.
     */
    implementation("com.sun.mail:android-mail:1.6.6")
    /**
     * This line adds the JavaBeans Activation Framework (JAF). JAF is
     * used by the JavaMail API to handle data content on messages and to define contracts
     * between systems that consume that data.
     */
    implementation("com.sun.mail:android-activation:1.6.6")
    /**
     * This line adds the SQLite KTX library to project. It’s a set of Kotlin
     * extensions that optimizes the use of SQLite in Android development.
     */
    implementation("androidx.sqlite:sqlite-ktx:2.4.0")
    /**
     * Navigation Compose library to your project. It’s a part of Jetpack Compose, a modern
     * toolkit for building native Android UI. Navigation Compose simplifies the implementation
     * of navigation in your Compose application.
     */
    implementation("androidx.navigation:navigation-compose:2.4.0-beta01")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-unit-android:1.5.4")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("org.jetbrains:annotations:15.0")
    implementation("org.jetbrains:annotations:15.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}