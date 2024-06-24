# Linguine Gradle Plugin

Linguine is a Gradle plugin written in Kotlin designed to simplify the localization process in your applications. It
automates the conversion of localization files to string resource files, supporting incremental builds and integration
with the Gradle build process.

## Key Features

- **Support for JSON File Types:** Automates the conversion of JSON files, commonly used for localization, into Kotlin
  string resource files.
- **Build Process Integration:** Seamlessly integrates with the Gradle build process, enabling the automatic generation
  of localization string files during builds.
- **Incremental Build Support:** Utilizes Gradle's incremental build features to avoid unnecessary work by only
  processing changed input files.
- **Configurable:** Offers customization options for specifying input and output file paths, file names, and delimiter
  settings.

## Requirements

- Gradle 6.1 or higher
- Kotlin 1.5.21 or a compatible version
- Compatible with Java, Android, and Kotlin Multiplatform projects

## Installation and Configuration

To use the Linguine plugin, add the following to your `build.gradle.kts` file:

```
plugins {
    id("com.qinshift.linguine") version "0.3.0"
}

linguineConfig {
    inputFilePath = "src/main/resources/strings.json"
    outputFilePath = "src/main/resources/"
    majorDelimiter = '__'
    minorDelimiter = '_'
}
```

## Usage

After configuring the plugin, Linguine will process the specified JSON file during the build process and generate a
Kotlin file with all localized strings. This Kotlin file (Strings.kt) will be placed in the specified outputFilePath
directory. You can then reference these strings throughout your project as needed.

For a simple example, if your JSON file contains:

```json
    {
  "hello_world": "Hello, World!"
}
```

After the build, Strings.kt will contain:

```
    object Strings {
        val helloWorld: String = localise("hello_world")
    }
```
