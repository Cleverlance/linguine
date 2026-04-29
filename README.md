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
import com.qinshift.linguine.linguinegenerator.PluralFormPolicy

linguine {
    inputFilePath = "localization-data/en/strings.json"
    outputFilePath = "src/commonMain/kotlin/com/example/app/localisation/en"
    sourceRootPath = "src/commonMain/kotlin"
    outputSuffix = "Strings"
    majorDelimiter = "__"
    minorDelimiter = "_"
    pluralFormPolicy = PluralFormPolicy.REQUIRE_ALL_FORMS
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
| `pluralFormPolicy` | `PluralFormPolicy` = `REQUIRE_ALL_FORMS` | No | Controls generator-side plural validation. Use `REQUIRE_ALL_FORMS` for providers that require `zero`, `one`, `two`, `few`, `many`, and `other`; use `REQUIRE_OTHER_ONLY` when only `other` must be present. |

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

### Plural Example

Input JSON:

```json
{
  "catalog__item_count": {
    "zero": "%1$s no items",
    "one": "%1$s item",
    "two": "%1$s items-two",
    "few": "%1$s items-few",
    "many": "%1$s items-many",
    "other": "%1$s items for %2$s"
  }
}
```

Generated Kotlin:

```kotlin
object CatalogStrings {
    fun itemCount(count: Number, param1: String, param2: String): String =
        localisePlural("catalog__item_count", count, param1, param2)
}
```

Usage:

```kotlin
val label = CatalogStrings.itemCount(3, "3", "Warehouse")
```

The runtime only requires `other` as a fallback plural form. Generator validation is controlled by `pluralFormPolicy`; the default `REQUIRE_ALL_FORMS` keeps strict validation for providers that need every form to be present.

The `count` argument is used only to select the plural form. If the selected text must display the count, pass that value explicitly as a regular placeholder argument, as shown with `param1` above.

If a plural value has no format placeholders, the generated function contains only `count`:

```kotlin
fun itemCount(count: Number): String
```

### Placeholder Indexing

Generated function parameters are derived from format placeholders.

Supported placeholder markers are `%s`, `%d`, `%f` and their indexed variants such as `%1$s`, `%2$d`, `%3$f`. These markers are used to detect argument positions. All generated placeholder parameters are typed as `String`; `count` remains the only generated `Number` parameter.

A single placeholder may be left unindexed:

```json
{
  "sample__label": "Hello %s"
}
```

This generates one parameter:

```kotlin
fun label(param1: String): String
```

If a string contains more than one placeholder, every placeholder must be indexed:

```json
{
  "sample__label": "%1$s and %2$s"
}
```

Generated accessor parameters are based on the complete placeholder index set. For plain text, that set comes from the text value itself. For plural text, that set is collected across all plural forms, so an individual form may use only a subset such as `%1$s` and `%3$s` as long as another form contributes `%2$s`.

The complete generated parameter set must be sequential from `1`. Translations may reorder indexed placeholders, for example `%2$s after %1$s`, but the final combined set must not skip an index.

Unindexed placeholders in multi-placeholder strings are rejected during generation:

```json
{
  "sample__label": "%1$s and %s"
}
```

Skipped placeholder indexes are also rejected during generation:

```json
{
  "sample__label": "%1$s and %3$s"
}
```

For plural values, skipped indexes are checked against all forms together. This is valid because `%2$s` exists in another form:

```json
{
  "catalog__item_count": {
    "zero": "%1$s no items",
    "one": "%1$s item",
    "two": "%1$s items",
    "few": "%1$s items for %2$s",
    "many": "%1$s items",
    "other": "%1$s items for %2$s"
  }
}
```

### Plural Count Behavior

Plural selection is based on `count: Number`.

The count must be finite. `NaN`, positive infinity, and negative infinity are rejected.

Negative values are supported and are matched by absolute value. For example, `-1` follows the same plural form as `1`.

### REQUIRE_ALL_FORMS Gettext Equations

Linguine plural forms are based on Unicode CLDR plural forms:

```text
zero, one, two, few, many, other
```

When `PluralFormPolicy.REQUIRE_ALL_FORMS` is used, the source provider must define all six forms. This keeps the provider export, generator validation, and runtime plural resolution aligned. It also means that provider-side gettext equations must explicitly cover forms such as `two`, even for languages where that form falls back to another grammatical form at runtime.

When configuring `PluralFormPolicy.REQUIRE_ALL_FORMS` in a provider that asks for a `Gettext-style equation for "n"`, use the same form order as Linguine's strict policy:

```text
0 = zero
1 = one
2 = two
3 = few
4 = many
5 = other
```

The matching equations are:

```text
English:
(n == 0) ? 0 : (n == 1) ? 1 : (n == 2) ? 2 : 5
```

```text
Czech:
(n == 0) ? 0 : (n%1 != 0) ? 4 : (n == 1) ? 1 : (n == 2) ? 2 : (n >= 3 && n <= 4) ? 3 : 5
```

```text
Slovak:
(n == 0) ? 0 : (n%1 != 0) ? 4 : (n == 1) ? 1 : (n == 2) ? 2 : (n >= 3 && n <= 4) ? 3 : 5
```

```text
French:
(n == 0) ? 0 : (n >= 0 && n < 2) ? 1 : (n == 2) ? 2 : (n%1 == 0 && n != 0 && n%1000000 == 0) ? 4 : 5
```

```text
Spanish:
(n == 0) ? 0 : (n == 1) ? 1 : (n == 2) ? 2 : (n%1 == 0 && n != 0 && n%1000000 == 0) ? 4 : 5
```

These formulas follow Linguine's runtime behavior. The `zero` form is treated as an explicit override for `0`; if it is not present at runtime, Linguine falls back to the next selected form when one exists, and then to `other`.

## Runtime Fallback Language

Plural selection in the runtime depends on the language of the loaded localisation file. Because of that, the `strings.json` fallback file must also have an explicit language context.

Linguine calls that language the `default localization language`.

By default, the default localization language is English:

```kotlin
import com.qinshift.linguine.linguineruntime.presentation.Localiser

Localiser.configureDefaultLocalization("en")
```

If your `strings.json` file is not English, configure it explicitly during app startup:

```kotlin
import com.qinshift.linguine.linguineruntime.presentation.Localiser

Localiser.configureDefaultLocalization("cs")
```

This makes `strings.json` use the correct plural rules for the configured default localization language.

Supported default localization languages are defined in [LanguagePluralSupported.kt](linguine-runtime/src/commonMain/kotlin/com/qinshift/linguine/linguineruntime/presentation/LanguagePluralSupported.kt).

When adding a new language there, you must also add the corresponding plural-selection rule in [LanguagePluralRules.kt](linguine-runtime/src/commonMain/kotlin/com/qinshift/linguine/linguineruntime/presentation/LanguagePluralRules.kt).

`Localiser.configureDefaultLocalization(...)` accepts any non-blank language code. If plural lookup later uses a language that is not listed in `LanguagePluralSupported`, the runtime fails with an explicit error when it first tries to resolve a plural form for that language.

Expected runtime asset names:

- `strings.json` for the fallback localization
- `strings-en.json`
- `strings-cs.json`
- `strings-sk.json`
- `strings-fr.json`
- `strings-es.json`

If you configure `Localiser.configureDefaultLocalization("cs")`, the runtime will interpret `strings.json` using Czech plural rules.

---

## Known Considerations

These points describe intentional contract boundaries and areas that should be handled carefully when changing provider integration, generator validation, or runtime parsing.

- Placeholder validation is focused on supported `%s`, `%d`, `%f` placeholders and their indexed variants. Unsupported printf-like tokens such as `%q`, `%1$q`, or incomplete forms such as `%1$` are not part of the supported contract and should not be emitted by localization providers.
- Runtime JSON syntax errors are currently logged and treated as empty localisation data, while semantic localisation errors such as invalid plural objects fail fast. This preserves compatibility, but stricter asset validation may require changing malformed JSON handling to fail fast as well.
- Runtime formatting trusts templates to follow the generator contract. If assets bypass generated accessors or are edited manually, mixed indexed/unindexed placeholders, skipped indexes, or multiple unindexed placeholders are not guaranteed to behave like generator-validated templates.

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
