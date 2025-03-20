import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.vanniktech.maven.publish")
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

extra["signingInMemoryKey"] = System.getenv("GPG_SIGNING_KEY")
extra["signingInMemoryKeyId"] = System.getenv("GPG_SIGNING_KEY_ID")
extra["signingInMemoryKeyPassword"] = System.getenv("GPG_SIGNING_PASSWORD")

configure<MavenPublishBaseExtension> {
    signAllPublications()
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    coordinates(project.group.toString(), project.name, project.version.toString())
    pom {
        name.set("GigaChat Java")
        url.set("https://github.com/ai-forever/gigachat-java")
        description.set("A Java HTTP client for interacting with the GigaChat API.")
        licenses {
            license {
                name.set("The MIT License (MIT)")
                url.set("http://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("isergeymd, hellnn, dmbocharova")
                name.set("Sergey Safonov, Igor Obruchnev, Darya Bocharova")
                email.set("SSafonov@sberbank.ru, IYObruchnev@sberbank.ru, dmbocharova@sberbank.ru")
            }
        }
        scm {
            connection.set("scm:https://github.com/ai-forever/gigachat-java.git")
            developerConnection.set("scm:git@github.com:ai-forever/gigachat-java.git")
            url.set("https://github.com/ai-forever/gigachat-java")
        }
    }
}
