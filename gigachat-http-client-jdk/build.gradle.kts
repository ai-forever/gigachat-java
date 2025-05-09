plugins {
    `java-library`
    id("com.diffplug.spotless")
    id("gigachat.publish")
}

dependencies {
    implementation(project(":gigachat-http-client"))
    implementation("org.slf4j:slf4j-api:2.0.16")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation("org.mockito:mockito-junit-jupiter:5.15.2")
    testImplementation("commons-io:commons-io:2.18.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    dependsOn(tasks.spotlessApply)
}

spotless {
    java {
        removeUnusedImports()
    }
}