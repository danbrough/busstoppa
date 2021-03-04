import org.jetbrains.dokka.gradle.DokkaTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.

@Suppress("GradleDependency","GradlePluginVersion") buildscript {

  repositories {
    google()
    mavenCentral()
  }

  dependencies {
    classpath("com.android.tools.build:gradle:_")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
  }

}

plugins {
  //buildSrcVersionsapply plugin: 'org.jetbrains.dokka'
  id("org.jetbrains.dokka") version "0.10.1"
}

apply("project.gradle.kts")

allprojects {
  repositories {
    google()
    mavenCentral()
    jcenter()
    maven("https://jitpack.io")
  }
}


tasks {
  val dokka by getting(DokkaTask::class) {
    outputFormat = "gfm"
    outputDirectory = "$rootDir/docs"
    subProjects = listOf("app")

    configuration {
      jdkVersion = 8
      includes = listOf("README.md")
    }

  }
}


subprojects {
  afterEvaluate {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
      kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.4"
        // freeCompilerArgs = listOf("-Xjvm-default=enable")
        freeCompilerArgs = freeCompilerArgs + listOf(
          //  "-Xopt-in=kotlinx.serialization.InternalSerializationApi",
          "-Xopt-in=kotlinx.serialization.InternalSerializationApi",

          "-Xopt-in=kotlinx.coroutines.InternalCoroutinesApi",
          "-Xopt-in=kotlin.time.ExperimentalTime",
          "-Xopt-in=kotlin.ExperimentalStdlibApi"
        )
      }
    }

  }
}