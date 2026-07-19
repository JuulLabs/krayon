# Documentation

A Compose Multiplatform application documenting Krayon, published to
[GitHub Pages](https://juullabs.github.io/krayon/) by the `gh-pages` workflow on every push to
`main`. Structured after [D3's documentation](https://d3js.org/), it pairs guide pages for each
module with a gallery of live, interactive examples.

This module is not published to Maven Central.

## Running

### Web (Kotlin/Wasm)

```shell
./gradlew :documentation:wasmJsBrowserDevelopmentRun
```

### Desktop (JVM)

Useful for a faster development loop:

```shell
./gradlew :documentation:run
```

## Structure

| Path | Purpose |
|---|---|
| `src/commonMain/kotlin/samples/` | Chart samples: pure `(RootElement, width, height, data)` functions shown in the gallery |
| `src/commonMain/kotlin/features/` | Screens (home, getting started, concepts, D3 guide, gallery) |
| `src/commonMain/kotlin/components/` | Shared UI (markdown rendering, code display, demo framing, Krayon-drawn diagrams) |
| `src/commonMain/composeResources/` | Static resources (fonts) |

## Sample code display

The build copies everything under `src/commonMain/kotlin/samples/` into the app's Compose
resources (`syncDocumentationResources` in `build.gradle.kts`), and gallery pages load and
syntax-highlight those files at runtime. The code shown in the app is therefore always the exact
code the app compiled and ran — it cannot drift.

## Diagrams

Architecture diagrams (e.g. on the home and "Elements & rendering" pages) are drawn live by
Krayon itself — see `src/commonMain/kotlin/components/Diagrams.kt`.

## Deployment

`.github/workflows/gh-pages.yml` builds `:documentation:wasmJsBrowserDistribution` plus the
aggregated Dokka reference (`dokkaGeneratePublicationHtml`), assembles them as a site (app at the
root, API reference under `/api/`), and deploys to the `gh-pages` branch.
