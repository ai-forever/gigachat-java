plugins {
    java
    `maven-publish`
    signing
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    withJavadocJar()
    withSourcesJar()
}

allprojects {
    group = "chat.giga"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = "gigachat-java"
            version = project.version.toString()
            pom {
                name.set("GigaChat Java")
                url.set("https://github.com/ai-forever/gigachat-java")
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
    }
    repositories {
        maven {
            name = "MavenCentral"
            val releasesUrl = uri("https://central.sonatype.com/api/v1/publisher")
            val snapshotsUrl = uri("https://central.sonatype.com/repository/maven-snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

tasks.withType<Jar> {
    exclude("**/gigachat-java-example/**")
}

signing {
    val singingPassword = System.getenv("GPG_PASSWORD")
    val signingSecretKey = System.getenv("GPG_SECRETKEY")
    useInMemoryPgpKeys(signingSecretKey, singingPassword)
    sign(publishing.publications.getByName("mavenJava"))
}
