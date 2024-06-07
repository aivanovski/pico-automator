pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "pico-automator"

include(
    ":pico-automator",
    ":pico-automator-cli",
    ":tests:tests-kotlin",
    ":pico-automator-android",
    ":pico-automator-web",
    ":pico-automator-web-api"
)