tasks.register<Copy>("copyKotlinCommon") {
    outputs.upToDateWhen { false }
    mustRunAfter("clean")

    from("${project(":sample").projectDir}/src/commonMain/kotlin/") {
        include("**/*.kt")
    }
    into("$projectDir/_includes/kotlin/")
}

tasks.register<Copy>("copyKotlinJs") {
    outputs.upToDateWhen { false }
    mustRunAfter("clean")

    from("${project(":sample").projectDir}/src/jsMain/kotlin/") {
        include("**/*.kt")
    }
    into("$projectDir/_includes/kotlin/")
}

tasks.register("copyKotlin") {
    group = "Website"
    dependsOn("copyKotlinCommon", "copyKotlinJs")
}

tasks.register<Copy>("copyFramework") {
    group = "Website"
    dependsOn(":sample:jsBrowserDevelopmentWebpack")
    outputs.upToDateWhen { false }
    mustRunAfter("clean")

    from(project(":sample").layout.buildDirectory.dir("dist/js/developmentExecutable").get()) {
        include("*.js")
    }
    into("$projectDir/assets/js/")
}

tasks.register<Copy>("copyDokka") {
    group = "Website"
    dependsOn(rootProject.getTasksByName("dokkaHtmlMultiModule", false))
    outputs.upToDateWhen { false }
    mustRunAfter("clean")

    from(rootProject.layout.buildDirectory.dir("dokka/htmlMultiModule").get())
    into("$projectDir/api/")
}

tasks.register<Exec>("bundleInstall") {
    commandLine("bundle", "install")
}

tasks.register<Exec>("browserBuild") {
    group = "Website"
    dependsOn("copyKotlin", "copyFramework", "copyDokka", "bundleInstall")
    commandLine("bundle", "exec", "jekyll", "build")
    val baseUrl = project.findProperty("baseurl")
    if (baseUrl != null) {
        commandLine("bundle", "exec", "jekyll", "build", "--baseurl", baseUrl)
    } else {
        commandLine("bundle", "exec", "jekyll", "build")
    }
}

tasks.register<Exec>("browserRun") {
    group = "Website"
    dependsOn("copyKotlin", "copyFramework", "copyDokka", "bundleInstall")
    commandLine("bundle", "exec", "jekyll", "serve")
}

tasks.register<Delete>("clean") {
    delete("$projectDir/_includes/kotlin/") // copyKotlin
    delete("$projectDir/assets/js/") // copyFramework
    delete("$projectDir/api/") // copyDokka
}
