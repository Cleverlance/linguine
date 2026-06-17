# KMP Localization Example with Linguine Plugin

This is an example of a simple Kotlin Multiplatform (KMP) project demonstrating the integration and usage of the Linguine localization strings plugin. This project targets two platforms: Android and iOS.

## Project Structure

- **Android Localization**:
  JSON localization files for the Android app are located in:
  `/composeApp/src/androidMain/assets`

- **iOS Localization**:
  JSON localization files for the iOS app are located in:
  `/iosApp/iosApp`
  These files are also linked in the Xcode project.

## Linguine Plugin Configuration

The Linguine plugin is configured in `shared/build.gradle.kts`. In this example, the Android English localization file is used as the input for the Linguine generator. Here's the configuration:

```kotlin
linguineConfig {
    inputFilePath = "../composeApp/src/androidMain/assets/strings-en.json"
    outputFilePath = "src/commonMain/kotlin/com/qinshift/project"
    majorDelimiter = "__"
    minorDelimiter = "_"
}
```

## Key Details
- **`inputFilePath`**: Specifies the path to the input localization file (Android English JSON in this case).
- **`outputFilePath`**: Defines the output directory for the generated code.
- **Delimiters**: `majorDelimiter` and `minorDelimiter` control how keys in the localization file are split and organized.

## Plugin Installation

The Linguine plugin is added to the project in `shared/build.gradle.kts`. The plugin version is managed in the `gradle/libs.versions.toml` file. Here's an example of how the plugin is configured:

```kotlin
plugins {
    ...
    alias(libs.plugins.linguine)
}