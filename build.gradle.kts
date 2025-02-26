plugins {
    `java-library`
}

version = "1.0.0"
group = "chat.giga"



repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}