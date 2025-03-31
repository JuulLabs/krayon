## Android

```shell
./gradlew installDebug
```

## Web

```shell
./gradlew jsBrowserDevelopmentRun
```

## Desktop (JVM)

```shell
./gradlew run
```

## Desktop (macOS)

```shell
./gradlew runDebugExecutableMacosArm64
```

# Development

## Resources

### Charts

Mermaid charts should be placed in the `mermaid/` directory with `.mermaid` extensions.
To convert the `*.mermaid` files (from the `mermaid` directory) to PNGs (in the
`src/commonMain/composeResources/drawable/` directory), run `convert.sh`.

To generate resources and accessors run:

```shell
./gradlew :documentation:generateComposeResClass :documentation:generateResourceAccessorsForCommonMain
```
