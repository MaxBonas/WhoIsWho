plugins {
    id("com.android.application")
    id("com.google.gms.google-services")  // Añade este plugin para Google Services
}

android {
    namespace = "com.max.FaceTrace"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.max.FaceTrace"
        minSdk = 21  // Cambiado para soportar Android 4.4 y posteriores
        this.targetSdk = 34  // Actualizado a 34
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
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("androidx.lifecycle:lifecycle-process:2.6.2")
    implementation("com.google.code.gson:gson:2.8.9")

    // Import the Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:32.4.0"))

    // Add the dependencies for Firebase products you want to use
    // No version numbers needed when using the BoM
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0") // Ajusta la versión según sea necesario
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
