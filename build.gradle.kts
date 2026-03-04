import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.android.kmp.library)

    alias(libs.plugins.composeMultiplatform)

    alias(libs.plugins.dotenv)

    id("maven-publish")
}

group = "dev.h4kt"
version = "0.2.0"

repositories {
    mavenCentral()
    google()
}

kotlin {
    withSourcesJar()

    androidLibrary {
        namespace = "dev.h4kt.compose.grid"
        compileSdk = 35

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    )

    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(compose.ui)
            implementation(compose.foundation)
        }
    }
}

publishing {
    repositories {
        maven {
            name = "Personal"
            url = uri("https://repo.h4kt.dev/releases")

            credentials {
                username = env.REPO_USERNAME.orNull()
                password = env.REPO_TOKEN.orNull()
            }
        }
    }
}
