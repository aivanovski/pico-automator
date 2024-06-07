import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinSerialization)
    id("jacoco")
}

val appGroupId = "com.github.aivanovski.picoautomator"

group = appGroupId
version = libs.versions.appVersion

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
    implementation(project(":pico-automator-web-api"))
    implementation(libs.logback)
    implementation(libs.koin)
    implementation(libs.kotlinx.json)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.negotiation)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.authjwt)
    // implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
}