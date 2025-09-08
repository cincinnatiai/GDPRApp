import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
}

android {
    namespace = "com.cincinnatiai.gdpr"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 35

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
    implementation(project(":CincinnatiAccountCommons"))
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.logging)
    implementation(libs.retrofit.serialization)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

val major = 0
val minor = 0
val patch = 30

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.cincinnatiai"
            artifactId = "gdpr"
            version = "$major.$minor.$patch"

            afterEvaluate {
                from(components.getByName("release"))
            }
        }
    }

    repositories {

        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/cincinnatiai/GDPRApp")
            credentials {
                username = gradleLocalProperties(rootDir, providers).getProperty("gdp.user")
                password = gradleLocalProperties(rootDir, providers).getProperty("gpr.key")
            }
        }
    }
}