---
layout: page
---

<canvas id="line-canvas" style="height: 480px; width: 854px"></canvas>
<script>sample.setupMovingSineWave("line-canvas");</script>

<details>
<summary markdown="span">Data</summary>

```kotlin
{% include kotlin/data/SineWave.kt %}
```
</details>

<details>
<summary markdown="span">Updater</summary>

```kotlin
{% include kotlin/updaters/LineChart.kt %}
```
</details>

<details>
<summary markdown="span">Glue</summary>

```kotlin
{% include kotlin/SetupMovingSineWave.kt %}
```
</details>
