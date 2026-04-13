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

- Class: `<Feature>Test` (e.g. `CardinalTest`).
- Method: `descriptive_camelCase_describingBehavior` (e.g. `cardinal_withDefaultTension_producesExpectedPath`).

## Floating-point comparisons

```kotlin
fun assertClose(expected: Float, actual: Float, epsilon: Float = 1e-4f) {
    assert(kotlin.math.abs(expected - actual) < epsilon) {
        "Expected $expected but was $actual (epsilon=$epsilon)"
    }
}
```

## Testing curves

Render points through the curve and compare the resulting `Path`:

```kotlin
val data = listOf(0f to 0f, 1f to 1f, 2f to 0f)
val path: Path = line<Pair<Float, Float>>()
    .x { (d) -> d.first }
    .y { (d) -> d.second }
    .curve(SomeCurve())
    .render(data)
```

When porting, extract expected values from upstream test suites (e.g. D3's SVG path strings) and adapt for `Float`
precision.

## Coverage checklist

1. **Happy path**: typical input produces expected output.
2. **Edge cases**: single point, two points, collinear/duplicate/empty input.
3. **Parameter variations**: test non-default configuration values.
4. **Area mode**: if the feature supports `startArea`/`endArea`, test that too.
5. **Upstream equivalence**: at least one test uses exact upstream fixture data.

## Running tests

```bash
./gradlew :<module>:allTests     # all platforms
./gradlew :<module>:jvmTest      # quick JVM-only check
```
