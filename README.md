# Linguine Gradle Plugin

**Linguine** is a Kotlin-based Gradle plugin that simplifies localization in multiplatform, Android, or JVM projects by **automatically converting JSON localization files into Kotlin code**. It keeps localized strings type-safe and integrated into your build process, reducing boilerplate.

---

## ✨ Features

- **JSON Localization Support:**  
  Converts nested JSON localization files into Kotlin `object` structures with type-safe string accessors.

- **Automatic Package Naming:**  
  Builds the Kotlin package name from the **generated file’s location relative to `sourceRootPath`**, keeping your codebase organized.

- **Incremental Build Support:**  
  Processes only changed files, speeding up builds.

- **Multiplatform Compatible:**  
  Works with Kotlin Multiplatform, Android, and JVM projects.

- **Highly Configurable:**  
  Customize input/output paths, delimiters, and task names.

---

## 🔧 Installation

Add this to your module-level `build.gradle.kts`:

```kotlin
plugins {
    id("com.qinshift.linguine") version "x.y.z"
}
```

---

## ⚙️ Configuration

### Example:

```kotlin
linguine {
    inputFilePath = "localization-data/en/strings.json"
    outputFilePath = "src/commonMain/kotlin/com/example/app/localisation/en"
    sourceRootPath = "src/commonMain/kotlin"
    majorDelimiter = "__"
    minorDelimiter = "_"
}
```

### 🔑 Key Configuration Options

| Property          | Description                                                                                 |
|-------------------|---------------------------------------------------------------------------------------------|
| `inputFilePath`   | Path to the input JSON file with localizations. Independent from the output structure.     |
| `inputFileType`   | (Optional) Type of the input file (default: `FileType.JSON`).                                          |
| `outputFilePath`  | Where to place the generated Kotlin file(s). Defines the target folder in your source tree. |
| `sourceRootPath`  | (Optional) **Base folder for generating package names. The package is computed as the path from `sourceRootPath` to `outputFilePath`.** |
| `majorDelimiter`  | (Optional) Splits keys into nested Kotlin `object`s. (default: `__`)                                    |
| `minorDelimiter`  | (Optional) Formats individual string names. (default: `_`)                                               |
| `buildTaskName`   | (Optional) Custom name for the Gradle task. (default: `generateStrings`)                                                 |

---

## 📦 Package Name Generation

The package name is computed from the **relative path between `sourceRootPath` and `outputFilePath`**.

Example:

```kotlin
linguine {
    inputFilePath = "localization-data/en/strings.json"
    outputFilePath = "src/commonMain/kotlin/com/example/app/localisation/en"
    sourceRootPath = "src/commonMain/kotlin"
}
```

➡️ Package name:
```kotlin
package com.example.app.localisation.en
```

If the relative path is empty or invalid, it falls back to:
```kotlin
package presentation
```

---

## 🧪 Usage Example

### Input JSON (`localization-data/en/strings.json`)
```json
{
  "home__welcome_message": "Welcome Home!"
}
```

### Generated Kotlin (`src/commonMain/kotlin/com/example/app/localisation/en/Strings.kt`)
```kotlin
package com.example.app.localisation.en

object Home {
    val welcomeMessage: String = localise("home__welcome_message")
}
```

### Usage in Code
```kotlin
val msg = Home.welcomeMessage
```

---

## 🚀 Build Integration

The plugin runs during the Gradle build:

```bash
./gradlew build
```

Or, run the task directly (if configured):
```bash
./gradlew generateLocalization
```

---

## 📝 License

See [license.md](license.md)
