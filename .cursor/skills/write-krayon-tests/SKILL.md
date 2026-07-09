---
name: write-krayon-tests
description: >-
  Write Kotlin Multiplatform tests for Krayon library code. Use when the user asks to add tests, verify a ported
  implementation, or when implementing new Krayon features that need test coverage.
---

# Write Krayon Tests

## Placement and framework

Tests go in `<module>/src/commonTest/kotlin/`, mirroring the main source structure. Use `kotlin.test` only — no JUnit
or other platform-specific frameworks in `commonTest`.

## Naming conventions

- Class: `<Feature>Tests` (e.g. `CardinalTests`).
- Method: `descriptive_camelCase_describingBehavior` (e.g. `cardinal_withDefaultTension_producesExpectedPath`).

## Floating-point comparisons

Use `assertEquals` with the `absoluteTolerance` parameter:

```kotlin
assertEquals(expected, actual = result, absoluteTolerance = 1e-4f)
```

## Coverage checklist

1. **Happy path**: typical input produces expected output.
2. **Edge cases**: empty, single-element, and boundary inputs.
3. **Parameter variations**: test non-default configuration values.
4. **Upstream equivalence**: when porting, include at least one test using exact upstream fixture data.

## Running tests

```bash
./gradlew :<module>:allTests     # all platforms
./gradlew :<module>:jvmTest      # quick JVM-only check
```
