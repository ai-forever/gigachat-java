plugins {
    id("java")
}

group = "chat.giga"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":gigachat-java"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}