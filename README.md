# Linguine Gradle Plugin

**Linguine** is a Kotlin-based Gradle plugin that simplifies localization in multiplatform, Android, or JVM projects by **automatically converting JSON localization files into Kotlin code**. It keeps localized strings type-safe and integrated into your build process, reducing boilerplate.

---

## ✨ Features

- **JSON Localization Support:**  
  Converts nested JSON localization files into Kotlin `object` structures with type-safe string accessors.

- **Automatic Package Naming:**  
  Builds the Kotlin package name from your file structure relative to `sourceRootPath`.

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
    id("com.qinshift.linguine") version "0.3.0"
}
```

---

## ⚙️ Configuration

### Example:

```kotlin
linguineConfig {
    inputFilePath = "src/localization/english/strings.json"
    outputFilePath = "build/generated/linguine"
    sourceRootPath = "src/localization"
    majorDelimiter = "__"
    minorDelimiter = "_"
}
```

### 🔑 Key Configuration Options

| Property          | Description                                                                                 |
|-------------------|---------------------------------------------------------------------------------------------|
| `inputFilePath`   | Path to the input JSON file with localizations.                                             |
| `inputFileType`   | Type of the input file (default: `FileType.JSON`).                                          |
| `outputFilePath`  | Where to place the generated Kotlin file(s). Typically a build or generated directory.      |
| `sourceRootPath`  | **Defines the root folder used to compute package names. Must be a parent of `inputFilePath`.** |
| `majorDelimiter`  | Splits keys into nested Kotlin `object`s. Default: `__`.                                    |
| `minorDelimiter`  | Formats individual string names. Default: `_`.                                               |
| `buildTaskName`   | (Optional) Custom name for the Gradle task.                                                 |

---

## 📦 Package Name Generation

Linguine derives the package name based on how your input file's location relates to `sourceRootPath`.

Example:
- `sourceRootPath = "src/localization"`
- `inputFilePath = "src/localization/english/strings.json"`

➡️ Resulting Kotlin package:
```kotlin
package english
```

If the relative path is empty or invalid, it falls back to:
```kotlin
package presentation
```

---

## 🧪 Usage Example

### Input JSON (`src/localization/english/strings.json`)
```json
{
  "welcome_message": "Welcome to our app!"
}
```

### Generated Kotlin (`build/generated/linguine/english/Strings.kt`)
```kotlin
package english

object Strings {
    val welcomeMessage: String = localise("welcome_message")
}
```

### Usage in Code
```kotlin
val msg = Strings.welcomeMessage
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
