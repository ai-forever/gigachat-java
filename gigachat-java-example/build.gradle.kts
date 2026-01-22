plugins {
    java
}

dependencies {
    implementation(project(":gigachat-java"))
    implementation("io.micrometer:micrometer-java11:1.15.0")
    implementation("io.micrometer:micrometer-registry-prometheus:1.15.0")

    implementation("ch.qos.logback:logback-classic:1.5.25")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.4")
    testImplementation(platform("org.junit:junit-bom:5.10.5"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
