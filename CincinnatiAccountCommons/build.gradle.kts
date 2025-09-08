import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.cincinnatiai.cincinnatiaccountcommons"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    testImplementation(libs.junit)
}

val major = 0
val minor = 0
val patch = 11

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.cincinnatiai"
            artifactId = "cincinnati-account-commons"
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