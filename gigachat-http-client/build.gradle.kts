plugins {
    `java-library`
    id("io.freefair.lombok") version "8.12.2"
    id("com.diffplug.spotless") version "7.0.2"
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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