# Krayon

Kotlin Multiplatform drawing/charting library. See `README.md` for overview.

## Cursor Cloud specific instructions

### Prerequisites

- **JDK 21** runs Gradle; **JDK 11** (`openjdk-11-jdk`) is required by the JVM toolchain (Gradle's foojay auto-download is blocked by egress restrictions).
- **Android SDK** (platform 36) is needed because all library modules apply the `android-library` plugin. Set `ANDROID_HOME=/opt/android-sdk` and ensure `local.properties` contains `sdk.dir=/opt/android-sdk`.
- **Mesa GL** (`libgl1-mesa-dri`, `libglx-mesa0`, `libegl-mesa0`) is needed to run the Compose Desktop sample app.

### Key Gradle tasks

| Task | Purpose |
|------|---------|
| `./gradlew lintKotlin` | Kotlin style checks (kotlinter) |
| `./gradlew jvmTest` | JVM unit tests |
| `./gradlew apiCheck` | Binary API compatibility (kotlinx-bvc) |
| `./gradlew :sample:run` | Run the Compose Desktop sample app |

### Running the sample desktop app

The sample app uses Skiko (Compose Desktop renderer). In headless/VM environments without GPU:

```
export DISPLAY=:1
export LIBGL_ALWAYS_SOFTWARE=1
export SKIKO_RENDER_API=SOFTWARE_FAST
./gradlew :sample:run
```

All three environment variables are required for the app window to render. Without `SKIKO_RENDER_API=SOFTWARE_FAST`, Skiko fails with "Cannot create Linux GL context".

### Notes

- Kotlin/Native targets (iOS, macOS) are automatically disabled on Linux — the warnings are expected and harmless. Add `kotlin.native.ignoreDisabledTargets=true` to `gradle.properties` to suppress them.
- The `local.properties` file is gitignored; it must exist with `sdk.dir` pointing to the Android SDK.
- CI runs on `macos-15` with JDK 17. On Linux cloud VMs, JVM and JS/WasmJS targets are fully testable; native Apple targets are not.
