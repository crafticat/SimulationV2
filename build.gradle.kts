// simulation2/build.gradle.kts  – top-level

plugins {
    alias(libs.plugins.android.application) apply false      // AGP version comes from libs.versions.toml
    id("com.google.gms.google-services") version "4.3.15" apply false
}

/*
 * No repositories here – settings.gradle.kts already defines them and
 * repositoriesMode = FAIL_ON_PROJECT_REPOS
 */
