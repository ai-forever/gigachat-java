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
    // Internal runtime remains on Jackson 2; consumers may use Jackson 2 or 3 mappers with models.
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.2")
    // Needed only so Lombok dual @Jacksonized annotations and Jackson 3 deserializers compile.
    compileOnly("tools.jackson.core:jackson-databind:3.1.5")

    testImplementation(platform("org.junit:junit-bom:5.10.5"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.mockito:mockito-junit-jupiter:5.15.2")
    testImplementation("commons-io:commons-io:2.18.0")
    testImplementation("org.mock-server:mockserver-junit-jupiter-no-dependencies:5.15.0")
    testImplementation("tools.jackson.core:jackson-databind:3.1.5")
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

lombok {
    version = "1.18.46"
}
