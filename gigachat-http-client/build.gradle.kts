plugins {
    `java-library`
    id("io.freefair.lombok") version "8.12.2"
    id("com.diffplug.spotless") version "7.0.2"
}

dependencies {
    implementation("commons-io:commons-io:2.18.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.3")
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
