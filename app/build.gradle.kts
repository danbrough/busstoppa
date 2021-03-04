plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  kotlin("plugin.serialization")
  id("kotlin-parcelize")

  // kotlin("android.extensions")
  // id("androidx.navigation.safeargs.kotlin")


}

android {
  compileSdkVersion(ProjectVersions.SDK_VERSION)

  defaultConfig {
    minSdkVersion(ProjectVersions.MIN_SDK_VERSION)
    targetSdkVersion(ProjectVersions.SDK_VERSION)
    versionCode = ProjectVersions.BUILD_VERSION
    versionName = ProjectVersions.getVersionName()
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    multiDexEnabled = true
  }

  compileOptions {
    sourceCompatibility = ProjectVersions.JAVA_VERSION
    targetCompatibility = ProjectVersions.JAVA_VERSION
  }

  buildFeatures {
    viewBinding = true
  }

  signingConfigs {
    register("release") {
      storeFile = file("/home/dan/.android/busapp_keystore2")
      keyAlias = "wellybusapp"
      storePassword = KeystoreConfig.PASSWORD
      keyPassword = KeystoreConfig.PASSWORD
    }
  }

  buildTypes {

    getByName("release") {
      isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
      )
      signingConfig = signingConfigs.getByName("release")
    }
  }


  kotlinOptions {
    jvmTarget = "1.8"
  }


}

tasks.withType<Test> {
  useJUnit()
  testLogging {
    events("standardOut", "started", "passed", "skipped", "failed")
    showStandardStreams = true
    outputs.upToDateWhen {
      false
    }
  }
}

dependencies {

  implementation(project(":api"))
  implementation("com.github.danbrough.androidutils:misc:_")
  implementation("com.google.android.gms:play-services-maps:_")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:_")

  implementation(AndroidX.multidex)
  implementation(AndroidX.viewPager2)
  implementation("org.slf4j:slf4j-api:_")
  implementation("com.github.danbrough.androidutils:slf4j:_")
  implementation(KotlinX.coroutines.android)
  implementation(Kotlin.stdlib.jdk8)

  implementation(Square.retrofit2.retrofit)
  implementation(Square.retrofit2.converter.gson)
  implementation(JakeWharton.retrofit2.converter.kotlinxSerialization)


  implementation(Square.okHttp3.okHttp)
  implementation("com.google.code.gson:gson:_")

  implementation(AndroidX.fragmentKtx)
  implementation(AndroidX.navigation.runtimeKtx)

  implementation(AndroidX.navigation.fragmentKtx)
  implementation(AndroidX.navigation.uiKtx)

  implementation(AndroidX.swipeRefreshLayout)

  implementation(AndroidX.constraintLayout)
  implementation(Google.android.material)
  implementation(AndroidX.lifecycle.liveDataKtx)
  kapt(AndroidX.room.compiler)
  implementation(AndroidX.room.runtime)
  implementation("android.arch.paging:runtime:_")

  implementation("pub.devrel:easypermissions:_")

  kapt("com.github.bumptech.glide:compiler:_")
  implementation("com.github.bumptech.glide:glide:_")
  implementation("com.github.bumptech.glide:okhttp3-integration:_")

  //implementation(Libs.misc)

  androidTestImplementation(AndroidX.test.core)
  androidTestImplementation(AndroidX.test.rules)
  androidTestImplementation(AndroidX.test.runner)


  implementation("com.github.danbrough:touchprompt:_")


  testImplementation(Testing.junit4)
  testImplementation("ch.qos.logback:logback-core:_")
  testImplementation("ch.qos.logback:logback-classic:_")
}
