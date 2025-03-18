plugins {

    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)

    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    alias(libs.plugins.dotenv)

    id("maven-publish")

}

group = "dev.h4kt"
version = "0.1.0"

repositories {
    mavenCentral()
    google()
}

kotlin {

    withSourcesJar()

    androidTarget()

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

android {
    namespace = "dev.h4kt.compose.grid"
    compileSdk = 35
}

publishing {
    repositories {
        maven {

            name = "Personal"
            url = uri("https://repo.h4kt.dev/releases")

            credentials {
                username = project.findProperty("repo.username") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("repo.key") as String? ?: System.getenv("TOKEN")
            }

        }
    }
}
