plugins {
    `java-library`
    `maven-publish`
    id("io.freefair.lombok") version "8.12.2"
    id("org.openapi.generator") version "7.11.0"
}

publishing {
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("publishing-repository"))
        }
    }
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    options.encoding = "UTF-8"

    dependsOn(tasks.openApiGenerate)
}

tasks.openApiGenerate {
    dependsOn(tasks.processResources)
}

openApiGenerate {
    generatorName.set("java")
    inputSpec.set("${sourceSets.main.get().output.resourcesDir}/spec/api.yml")
    outputDir.set("${layout.buildDirectory.dir("generated").get()}")
    modelPackage.set("chat.giga.model")
    generateModelDocumentation.set(false)
    generateModelTests.set(false)
    configOptions.set(
            mapOf(
                    "library" to "microprofile",
                    "serializationLibrary" to "jackson",
                    "generateBuilders" to "true"
            )
    )
    globalProperties.set(
            mapOf(
                    "models" to ""
            )
    )
}
