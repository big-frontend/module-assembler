<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Changelog

## [Unreleased]

### Changed

- Upgrade Gradle Wrapper to `8.5`
- Dependencies - upgrade `org.jetbrains.intellij` to `1.16.1`
- Dependencies - upgrade `org.jetbrains.kotlin.jvm` to `1.9.21`
- Dependencies - upgrade `org.jetbrains.kotlinx.kover` to `0.7.5`
- Dependencies - upgrade `annotations` to `24.1.0`
- Dependencies (GitHub Actions) - upgrade `JetBrains/qodana-action` to `v2023.2.8`
- Dependencies (GitHub Actions) - upgrade `actions/setup-java` to `4`

## [1.0.0]
### Added
- module manager 1.0版本发布了耶(*^▽^*).

[comment]: <> (### Changed)

[comment]: <> (- Dependencies - upgrade `org.jetbrains.intellij` to `1.3.0`)

[comment]: <> (- Dependencies - upgrade `org.jetbrains.changelog` to `1.3.1`)

[comment]: <> (- Dependencies - upgrade `org.jetbrains.kotlin.jvm` to `1.6.0`)

[comment]: <> (- Dependencies &#40;GitHub Actions&#41; - upgrade `jtalk/url-health-check-action` to `2`)

[comment]: <> (- Dependencies &#40;GitHub Actions&#41; - upgrade `actions/checkout` to `2.3.5`)

[comment]: <> (- GitHub Actions general performance refactoring)

[comment]: <> (- GitHub Actions - prepare plugin archive content to be archived once)

[comment]: <> (- GitHub Actions - patch changelog only if change notes are provided)

[comment]: <> (- Update `pluginUntilBuild` to include `213.*` &#40;2021.3.*&#41;)

[comment]: <> (- Upgrade Gradle Wrapper to `7.3`)

[comment]: <> (### Fixed)

[comment]: <> (- Fixed passing change notes from `CHANGELOG.md` to the Release Draft)

[comment]: <> (- Fixed passing updated change notes from the Release Draft to `patchChangelog` Gradle task)

[comment]: <> (- Fixed `QODANA_SHOW_REPORT` environment variable resolving for Gradle `6.x`)

[comment]: <> (### Removed)

[comment]: <> (- Removed the `pluginVerifierIdeVersions` configuration to use default IDEs list provided by the `listProductsReleases` task for `runPluginVerifier`)

[comment]: <> (- Removed `platformDownloadSources` from Gradle configuration to use default value)

[comment]: <> (- Removed `updateSinceUntilBuild.set&#40;true&#41;` from Gradle configuration to use default value)