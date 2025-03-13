import co.touchlab.skie.configuration.EnumInterop
import co.touchlab.skie.configuration.FlowInterop
import co.touchlab.skie.configuration.SealedInterop
import co.touchlab.skie.configuration.SuspendInterop
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.GregorianCalendar
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.skie)
    alias(libs.plugins.sqlDelight)
}

skie {
    features {
        group {
            FlowInterop.Enabled(false)
            EnumInterop.Enabled(false)
            SealedInterop.Enabled(false)
            coroutinesInterop.set(false)
            SuspendInterop.Enabled(false)
        }
        group(targetFqNamePrefix = "ViewModel") {
            FlowInterop.Enabled(true)
            coroutinesInterop.set(true)
        }
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        this.jvmTarget.set(JvmTarget.JVM_17)
    }
}
compose.resources {
    this.publicResClass = true
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        this.apiVersion.set(KotlinVersion.KOTLIN_2_0)
        this.languageVersion.set(KotlinVersion.KOTLIN_2_0)
    }
    targets.configureEach {
        compilations.configureEach {}
    }
    androidTarget {
        compilations.all {}

//        @OptIn(ExperimentalKotlinGradlePluginApi::class)
//        compilerOptions {
//            jvmTarget.set(JvmTarget.JVM_11)
//        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
//            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.activity.compose)

            // koin
            implementation(libs.koin.android)
            // firebase
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.crashlytics)

            // splash screen api
            implementation(libs.android.splash.api)
            implementation(libs.sql.delight.android.driver)

//            implementation(libs.bundles.precompose)
            implementation(libs.koin.compose)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.koin.core)
            implementation(libs.sql.delight.native.driver)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
//
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.navigation.compose)

//
//
            implementation(libs.skie.config.annotation)
            implementation(compose.runtime)
            implementation(compose.components.resources)

            implementation(libs.sql.delight.coroutines)
            implementation(libs.sql.delight.primitive.adapter)
            implementation(libs.bundles.data.store)

            api(libs.crashkios)
        }
        commonTest.dependencies {}
    }
}

sqldelight{
    databases {
        create("app_db") {
            packageName.set("com.gometro.database")
            verifyMigrations.set(true)
            schemaOutputDirectory.set(file("src/main/sqldelight/databases"))
        }
        linkSqlite = false
    }
}

android {
    namespace = "com.gometro.kmpapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.gometro.kmpapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"


        val gitSha = providers.exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
        }.standardOutput.asText.get().trim()

        val gitBranch = providers.exec {
            commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
        }.standardOutput.asText.get().trim()

        val buildTime = SimpleDateFormat("dd-MM HH:mm").format(GregorianCalendar.getInstance().time)

//        val apikeyProperties = Properties()
//        apikeyProperties.load(FileInputStream(project.rootProject.file("apikey.properties")))

        buildConfigField("String", "GIT_SHA", "\"${gitSha}\"")
        buildConfigField("String", "rabbit_user", "\"appuser:\"")
        buildConfigField("String", "rabbit_pass", "\"MDI2ZTBmYTUxNzVmMjM0ZjM2ZjEyNDYx\"")
        buildConfigField("String", "BUILD_TIME", "\"${buildTime}\"")
//        buildConfigField("String", "PLOTLINE_APP_KEY_DEV", apikeyProperties["PLOTLINE_APP_KEY_DEV"].toString())
//        buildConfigField("String", "PLOTLINE_APP_KEY_PROD", apikeyProperties["PLOTLINE_APP_KEY_PROD"].toString())
        buildConfigField(
            "com.gometro.buildconfig.Environment",
            "ENVIRONMENT",
            "com.gometro.buildconfig.Environment.DEVELOPMENT"
        )

        applicationVariants.all {
            this.outputs
                .filterIsInstance<BaseVariantOutputImpl>()
                .forEach { output ->
                    val outputFileName =
                        "chalo_${this.buildType.name}_${this.versionName}_${gitBranch}.apk"
                    output.outputFileName = outputFileName
                }
        }
    }

//    signingConfigs {
//        create("release") {
//            if (signingStoreFile != null) {
//                storeFile = rootProject.file(signingStoreFile)
//                storePassword = signingStorePassword
//                keyAlias = signingKeyAlias
//                keyPassword = signingKeyPassword
//            }
//        }
//        getByName("debug") {
//            keyAlias = "alias_name"
//            keyPassword = "zophop13"
//            storeFile = file("debug.keystore")
//            storePassword = "zophop13"
//        }
//    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        compileOptions
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

//    kotlinOption {
//        jvmTarget = '21'
//    }

    buildFeatures {
        buildConfig = true
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    externalNativeBuild {
        cmake {
            path = file("src/androidMain/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    sourceSets.getByName("main") {
        jniLibs.srcDirs("libs")
    }
    ndkVersion = "21.1.6352462"


//    dependencies {
//    debugImplementation(libs.androidx.compose.ui.tooling)
        // This is not a recommended way
        // https://kotlinlang.org/docs/multiplatform-android-dependencies.html
        dependencies {
            debugImplementation(libs.chucker.debug)
            releaseImplementation(libs.chucker.release)
//            implementation(libs.bundles.precompose)

        }

//    }
}
