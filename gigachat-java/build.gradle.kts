plugins {
    `java-library`
    `maven-publish`
    id("io.freefair.lombok") version "8.12.2"
    id("gigachat.publish")
}

publishing {
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("publishing-repository"))
        }
    }
}

dependencies {
    api(project(":gigachat-http-client"))
    api(project(":gigachat-http-client-jdk"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.4")

    testImplementation(platform("org.junit:junit-bom:5.10.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation("org.mockito:mockito-junit-jupiter:5.15.2")
    testImplementation("commons-io:commons-io:2.18.0")
    testImplementation("org.mock-server:mockserver-junit-jupiter-no-dependencies:5.15.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    options.encoding = "UTF-8"
}
