plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)

    id("org.jetbrains.compose") version "1.12.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.0"
}

group = "dev.apollointhehouse"
version = "1.0.0-SNAPSHOT"

kotlin {
    jvmToolchain(21)

    compilerOptions {
        freeCompilerArgs.addAll("-Xcollection-literals", "-Xname-based-destructuring=complete")
    }
}

compose.desktop {
    application {
        mainClass = "dev.apollointhehouse.MainKt"

        nativeDistributions {
            includeAllModules = true

            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )
            packageName = "CometProxy"
            packageVersion = "1.0.0"
        }
    }
}

dependencies {
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.contentNegotiation)
    implementation(ktorLibs.client.cio)
    implementation(libs.logback.classic)

    implementation("org.apache.logging.log4j:log4j-api:2.26.1")
    implementation("org.apache.logging.log4j:log4j-api-kotlin:1.5.0")
    implementation("org.apache.logging.log4j:log4j-to-slf4j:2.26.1")


    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material")
    }

    implementation("org.jetbrains.jewel:jewel-int-ui-standalone:0.35.0-261.23567.198")
    implementation("org.jetbrains.jewel:jewel-ui:0.35.0-261.23567.198")

    implementation(compose.desktop.currentOs)

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
}
