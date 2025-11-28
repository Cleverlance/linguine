# Linguine Gradle Plugin

**Linguine** is a Kotlin-based Gradle plugin that simplifies localization in multiplatform, Android, or JVM projects by **automatically converting JSON localization files into Kotlin code**. It keeps localized strings type-safe and integrated into your build process, reducing boilerplate.

---

## ✨ Features

- **JSON Localization Support**  
  Converts JSON localization files into Kotlin `object` structures with type-safe string accessors using configurable delimiters.

- **Automatic Package Naming**  
  Builds the Kotlin package name from the **generated file’s location relative to `sourceRootPath`**, keeping your codebase organized.

- **Configurable Naming**  
  Customize key delimiters and the suffix used for generated file and object names via `outputSuffix` (e.g. `Strings`, `L10n`).

- **Incremental Build Support**  
  Processes only changed files, speeding up builds.

- **Multiplatform Compatible**  
  Works with Kotlin Multiplatform, Android, and JVM projects.

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

### Example

```kotlin
linguine {
    inputFilePath = "localization-data/en/strings.json"
    outputFilePath = "src/commonMain/kotlin/com/example/app/localisation/en"
    sourceRootPath = "src/commonMain/kotlin"
    outputSuffix = "Strings"
    majorDelimiter = "__"
    minorDelimiter = "_"
}
```

### 🔑 Key Configuration Options

| Property          | Type / Default         | Required | Description |
|-------------------|------------------------|----------|-------------|
| `inputFilePath`   | `String` (no default)  | Yes      | Path to the input JSON file with localizations. Independent from the output structure. |
| `inputFileType`   | `FileType` = `JSON`    | No       | Type of the input file. Currently only JSON is supported. |
| `outputFilePath`  | `String` (no default)  | Yes      | Directory where generated Kotlin file(s) are written. Defines the target folder in your source tree. |
| `sourceRootPath`  | `String` (no default)  | No       | Base folder for generating package names. The package is computed as the path from `sourceRootPath` to `outputFilePath`. If omitted or resulting path is blank, `presentation` is used. |
| `outputSuffix`    | `String` = `"Strings"` | No       | Suffix appended to the generated file and root-level object. For example, group `Home` with `outputSuffix = "Strings"` generates `HomeStrings.kt` and `object HomeStrings`. |
| `majorDelimiter`  | `String` = `"__"`      | No       | Splits keys into nested Kotlin `object`s. For example, `home__welcome_message` creates a `Home*` group and a `welcomeMessage` member. |
| `minorDelimiter`  | `String` = `"_"`       | No       | Splits individual key segments into words for camelCase members (e.g. `welcome_message` → `welcomeMessage`). |
| `buildTaskName`   | `String?` = `null`     | No       | Custom name for a build task that should depend on string generation. If not set, all `compile*` tasks will depend on `generateStrings`. |

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

### Generated Kotlin (`src/commonMain/kotlin/com/example/app/localisation/en/HomeStrings.kt`)

Assuming:

```kotlin
outputSuffix = "Strings"
```

Generated file:

```kotlin
package com.example.app.localisation.en

import com.qinshift.linguine.linguineruntime.presentation.Localiser.localise

object HomeStrings {
    val welcomeMessage: String = localise("home__welcome_message")
}
```

### Usage in Code

```kotlin
val msg = HomeStrings.welcomeMessage
```

---

## 🚀 Build Integration

The plugin registers a `generateStrings` task and wires it into the build.

Run the full build:

```bash
./gradlew build
```

Or, run string generation directly:

```bash
./gradlew generateStrings
```

If you set `buildTaskName`, that task will depend on `generateStrings`; otherwise all `compile*` tasks will.

---

## 📝 License

See [license.md](license.md).
