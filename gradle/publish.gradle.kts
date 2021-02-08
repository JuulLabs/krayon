import java.net.URI

configure<PublishingExtension> {
    repositories {
        maven {
            name = "github"

            // Because upper case letters aren't supported, you must use lowercase letters for the repository owner even
            // if the GitHub user or organization name contains uppercase letters.
            //
            // https://help.github.com/en/github/managing-packages-with-github-packages/configuring-gradle-for-use-with-github-packages#authenticating-with-a-personal-access-token
            url = java.net.URI("https://maven.pkg.github.com/juullabs/android-github-packages")

            credentials {
                username = project.findProperty("github.packages.username").toString()
                password = project.findProperty("github.packages.password").toString()
            }
        }
    }
}
