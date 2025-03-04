plugins {
    java
}

dependencies {
    implementation(project(":gigachat-java"))
    implementation("ch.qos.logback:logback-classic:1.5.8")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
