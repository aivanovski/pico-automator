import java.io.BufferedWriter
import java.io.FileInputStream
import java.io.FileWriter
import java.util.Properties
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("jacoco")
}

val appGroupId = "com.github.aivanovski.picoautomator"
val appVersion: String = libs.versions.appVersion.get()

group = appGroupId
version = appVersion

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.jacocoTestReport {
    reports {
        val coverageDir = File("$buildDir/reports/coverage")
        csv.required.set(true)
        csv.outputLocation.set(File(coverageDir, "coverage.csv"))
        html.required.set(true)
        html.outputLocation.set(coverageDir)
    }

    dependsOn(allprojects.map { it.tasks.named<Test>("test") })
}

tasks.test {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

tasks {
    register<Copy>("copyClojureSources") {
        from("${project.rootDir.path}/pico-automator-clojure/src/picoautomator")
        into("${project.rootDir.path}/pico-automator-cli/src/main/resources/picoautomator")
    }
}

tasks {
    processResources {
        dependsOn("copyClojureSources")
    }
}

dependencies {
    testImplementation(libs.junit.engine)
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.mockk)

    implementation(project(":pico-automator"))
    implementation(libs.koin)
    implementation(libs.clojure)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.websockets)

}