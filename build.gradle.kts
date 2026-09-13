import dev.nucleusframework.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)

    id("org.jetbrains.compose") version "1.12.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.0"
    id("dev.nucleusframework") version "2.5.15"
}

group = "dev.apollointhehouse"
version = "1.0.0-SNAPSHOT"

kotlin {
    jvmToolchain(25)

    compilerOptions {
        freeCompilerArgs.addAll("-Xcollection-literals", "-Xname-based-destructuring=complete")
    }
}

nucleus.application {
    mainClass = "dev.apollointhehouse.MainKt"

    nativeDistributions {
        includeAllModules = true

        targetFormats(
            TargetFormat.Dmg,
            TargetFormat.Exe,
            TargetFormat.Deb
        )
        packageName = "CometProxy"
        packageVersion = "1.0.0"
        homepage = "https://github.com/Apollointhehouse/CometProxy"
    }
}

dependencies {
    // Ktor
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.contentNegotiation)
    implementation(ktorLibs.client.cio)

    // Logging
    implementation("org.apache.logging.log4j:log4j-api:2.26.1")
    implementation(libs.logback.classic)
    implementation("org.apache.logging.log4j:log4j-api-kotlin:1.5.0")
    implementation("org.apache.logging.log4j:log4j-to-slf4j:2.26.1")


    // UI
    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material")
    }

    implementation("dev.nucleusframework:nucleus.core-runtime:2.5.15")
    implementation("dev.nucleusframework:nucleus.taskbar-progress:2.5.15")

    implementation("dev.nucleusframework:nucleus.nucleus-application:2.5.15")
    implementation("dev.nucleusframework:nucleus.decorated-window-core:2.5.15")
    implementation("dev.nucleusframework:nucleus.decorated-window-tao:2.5.15")
    implementation("dev.nucleusframework:nucleus.decorated-window-jewel:2.5.15")
    implementation("dev.nucleusframework:nucleus.decorated-window-awt:2.5.15")

    // Jewel Theme
    implementation("org.jetbrains.jewel:jewel-int-ui-standalone:0.35.0-261.23567.198")
    implementation("org.jetbrains.jewel:jewel-ui:0.35.0-261.23567.198")
    implementation("org.jetbrains.jewel:jewel-decorated-window:0.35.0-261.23567.198")

    // Arg parsing
    implementation("com.github.ajalt.clikt:clikt:5.0.1")

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
}
