object Versions {
    const val ktor = "1.4.2"
    const val coroutines = "1.4.2-native-mt"
    const val kotlinxSerialization = "1.0.0-RC"

    object Test {
        const val junit = "4.13"
    }
}

object Ktor {
    const val clientCore = "io.ktor:ktor-client-core:${Versions.ktor}"
    const val clientSerialization = "io.ktor:ktor-client-serialization:${Versions.ktor}"
    const val clientJson = "io.ktor:ktor-client-json:${Versions.ktor}"
    const val clientLogging = "io.ktor:ktor-client-logging:${Versions.ktor}"
    const val clientAndroid = "io.ktor:ktor-client-android:${Versions.ktor}"
    const val clientIos = "io.ktor:ktor-client-ios:${Versions.ktor}"
}

object Kotlinx {
    const val serializationCore = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.kotlinxSerialization}"

    object Coroutines {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    }
}

object Test {
    const val junit = "junit:junit:${Versions.Test.junit}"
}