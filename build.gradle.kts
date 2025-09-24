// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("com.android.library") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.10" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.3" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("0.50.0")
        android.set(true)
        outputColorName.set("RED")
    }
    
    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        config.setFrom("$rootDir/config/detekt/detekt.yml")
        buildUponDefaultConfig = true
    }
}
