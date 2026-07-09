---
name: research-upstream-api
description: >-
  Research an upstream library's source code and documentation to understand its API surface, algorithms, and behavior
  before porting features to Krayon. Use when the user asks to port, implement, or add missing features from an external
  library (D3, Vega, Apache Commons Math, etc.), or when you need to understand how an upstream feature works before
  writing code.
---

# Research Upstream API

Before porting any feature into Krayon, research the upstream library to understand its API, algorithm, test fixtures, and license.

## What to produce

Return a structured summary containing:

- Upstream library/module name, URL, and license
- List of symbols already present in the corresponding Krayon module
- List of symbols missing from Krayon
- For each missing symbol: brief description, algorithm notes, and links to the upstream source and test fixtures

## D3-specific notes

Krayon's primary upstream is [D3](https://github.com/d3/d3).

- Repos follow `https://github.com/d3/d3-<module>` (e.g. d3-shape, d3-scale).
- Test files contain `[x, y]` point arrays and expected SVG path strings — ideal gold data for Krayon tests.
- D3's curve protocol (`areaStart`, `areaEnd`, `lineStart`, `lineEnd`, `point`) maps to Krayon's `Curve` interface
  (`startArea`, `endArea`, `startLine`, `endLine`, `point`).
- Note configurable parameters (e.g. `bundle.beta()`, `cardinal.tension()`, `catmullRom.alpha()`).

## Tips

- Upstream test suites are the most reliable source of expected behavior. Always look for them.
- Note the upstream license — it will be needed for the NOTICE file.
- If the upstream uses `Double` or 64-bit floats, note precision differences when porting to Kotlin `Float`.
