import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization") version "1.4.32"
}

kotlin {
    android()
    ios {
        binaries {
            framework {
                baseName = "shared"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Kotlinx.Coroutines.core)
                implementation(Ktor.clientCore)
                implementation(Kotlinx.serializationCore)
                implementation(Ktor.clientSerialization)
                implementation(Ktor.clientJson)
                implementation(Ktor.clientLogging)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Ktor.clientAndroid)
                implementation(Kotlinx.Coroutines.android)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(Test.junit)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(Ktor.clientIos)
            }
        }
        val iosTest by getting
    }
}

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")
    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(30)
    }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}

val packForXcode by tasks.creating(Sync::class) {
    group = "build"
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
    val targetName = "ios" + if (sdkName.startsWith("iphoneos")) "Arm64" else "X64"
    val framework =
        kotlin.targets.getByName<KotlinNativeTarget>(targetName).binaries.getFramework(mode)
    inputs.property("mode", mode)
    dependsOn(framework.linkTask)
    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)
}

tasks.getByName("build").dependsOn(packForXcode)
