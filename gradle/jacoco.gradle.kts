val jacocoTestReport by tasks.registering(JacocoReport::class) {
    reports {
        csv.required.set(false)
        html.required.set(true)
        xml.required.set(true)
    }

    classDirectories.setFrom(file("${buildDir}/classes/kotlin/jvm/main/"))
    sourceDirectories.setFrom(files("src/commonMain", "src/jvmMain"))
    executionData.setFrom(files("${buildDir}/jacoco/jvmTest.exec"))

    dependsOn("jvmTest")
}
