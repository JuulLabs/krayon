---
name: implement-upstream-port
description: >-
  Port a feature from an upstream library into Kotlin Multiplatform code in the Krayon repo. Use when the user asks to
  implement, add, or port a feature from an external library (D3, Vega, Apache Commons Math, etc.) into a Krayon module.
---

# Implement Upstream Port

Translate an upstream library's implementation into idiomatic Kotlin for Krayon.

## Prerequisites

If you don't already have a clear understanding of the upstream feature's API and algorithm, use the
**research-upstream-api** skill first.

## Krayon-specific patterns

- **Float, not Double**: Krayon uses `Float` throughout. Convert upstream numeric types accordingly.
- **PathBuilder**: drawing code emits commands via `PathBuilder<*>`. Key methods: `moveTo`, `lineTo`, `cubicTo`,
  `quadraticTo`, `close`.
- **Curve interface**: new curves implement `com.juul.krayon.shape.curve.Curve` with `startArea`, `endArea`,
  `startLine`, `endLine`, `point`.
- **Parameterized features**: upstream builder/factory configuration (e.g. D3's `cardinal.tension(t)`) becomes
  constructor parameters with matching default values.
- **State management**: mirror upstream instance variables as private `var` properties, reset in the appropriate
  lifecycle method (e.g. `startLine`).

## JS/D3 translation cheat sheet

| JS / D3                              | Krayon (Kotlin)                          |
|---------------------------------------|------------------------------------------|
| `export default function Foo`         | `public class Foo : <Interface>`         |
| Closure-captured mutable state        | Private mutable properties on the class  |
| `NaN` sentinel for "not set"          | `Float.NaN` — use a `truthy()` helper    |
| `context.bezierCurveTo(a,b,c,d,e,f)` | `context.cubicTo(a,b,c,d,e,f)`          |

The existing `Linear.kt` has a private `Float.truthy()` extension for JS truthiness semantics. Reuse this when porting
code that checks `NaN`/`0` as boolean.

## After implementing

1. Run `./gradlew lintKotlin` and fix any formatting issues.
2. Run `./gradlew apiDump` to regenerate API signature files.
3. Update the repo-root `NOTICE` file to credit the upstream library (see existing entries for the format).
4. Write tests (see the **write-krayon-tests** skill).
