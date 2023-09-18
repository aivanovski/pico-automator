import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm").version("1.8.22")
    id("java-library")
    id("maven-publish")
    jacoco
}

val appGroupId = "com.github.aivanovski"
val appArtifactId = "pico-automator"
val appVersion = "0.0.1"

group = appGroupId
version = appVersion

tasks.withType<KotlinCompile> {
    kotlinOptions {
        apiVersion = "1.5"
        languageVersion = "1.5"
        jvmTarget = "11"
    }
}

java {
    withSourcesJar()
    withJavadocJar()

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

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.2")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.2")
    testImplementation("io.mockk:mockk:1.12.3")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.22")
    implementation("org.buildobjects:jproc:2.8.0")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.3")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.3")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = appGroupId
            artifactId = appArtifactId
            version = appVersion

            from(components["java"])
        }
    }
}