plugins {
    java
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

allprojects {
    group = "chat.giga"
    version = "0.1.7"

    repositories {
        mavenCentral()
    }
}
