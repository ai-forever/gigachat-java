plugins {
    `java-library`
    id("io.freefair.lombok") version "8.12.2"
    id("com.diffplug.spotless")
    id("gigachat.publish")
}

dependencies {
    api("org.slf4j:slf4j-api:2.0.17")

    testImplementation(platform("org.junit:junit-bom:5.10.5"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.mockito:mockito-junit-jupiter:5.15.2")
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
