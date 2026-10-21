plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)   // NUEVO: procesador que genera el código de Room al compilar
}

android {
    namespace = "cl.duoc.tareas"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "cl.duoc.tareas"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // ---- Agregadas para la app Mis Tareas DUOC ----
    implementation(libs.androidx.lifecycle.viewmodel.compose)    // viewModel() dentro de Compose
    implementation(libs.androidx.compose.material.icons.core)    // Íconos (editar, eliminar)
    implementation(libs.androidx.room.runtime)                   // Room: clases y anotaciones para la base de datos
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.compose.material.icons.core)    // Íconos (check, advertencia, rostro, engranaje, flecha atrás)
    implementation(libs.androidx.navigation.compose)        // Room + corrutinas (suspend) y Flow
    ksp(libs.androidx.room.compiler)                             // Room: genera el código al compilar (KSP)
}