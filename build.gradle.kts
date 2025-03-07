import groovy.util.Node

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
    version = "0.1.0"

    repositories {
        mavenCentral()
    }
}

signing {
    sign(publishing.publications)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom.withXml {
                asNode().apply {
                    val dependenciesNode = get("dependencies") as groovy.util.Node
                    val gigachatJavaExampleDependency = dependenciesNode.children().find {
                        it.toString().contains("gigachat-java-example")
                    }
                    dependenciesNode.remove(gigachatJavaExampleDependency as Node?)
                }
            }
        }
    }
    repositories {
        maven {
            name = "MavenCentral"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.findProperty("OSSRH_USERNAME") as String? ?: ""
                password = project.findProperty("OSSRH_PASSWORD") as String? ?: ""
            }
        }
    }
}
//publishing {
//    publications {
//        create<MavenPublication>("mavenJava") {
//            from(components["java"])
//            groupId = project.group.toString()
//            artifactId = "gigachat-java"
//            version = project.version.toString()
//
//            pom {
//                name.set("GigaChat Java")
//                url.set("https://github.com/ai-forever/gigachat-java")
//                licenses {
//                    license {
//                        name.set("Apache License, Version 2.0")
//                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//                    }
//                }
//                developers {
//                    developer {
//                        id.set("name")
//                        name.set("name")
//                        email.set("name")
//                    }
//                }
//                scm {
//                    connection.set("scm:https://github.com/ai-forever/gigachat-java.git")
//                    developerConnection.set("scm:git@github.com:ai-forever/gigachat-java.git")
//                    url.set("https://github.com/ai-forever/gigachat-java")
//                }
//            }
//        }
//    }
//    repositories {
//        maven {
//            name = "MavenCentral"
//            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
//            credentials {
//                username = project.findProperty("OSSRH_USERNAME") as String? ?: ""
//                password = project.findProperty("OSSRH_PASSWORD") as String? ?: ""
//            }
//        }
//    }
//}
