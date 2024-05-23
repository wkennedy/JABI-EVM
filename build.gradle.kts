plugins {
    id("java")
    jacoco
}

group = "com.github.wkennedy"
version = "1.0-SNAPSHOT"
val SPRING_PLUGINS_REPO_URL = "https://repo.spring.io/plugins-release/"

repositories {
    mavenCentral()
    maven {
        url = uri("https://artifacts.consensys.net/public/maven/maven/")
    }
    maven {
        url = uri(SPRING_PLUGINS_REPO_URL)
    }
}

dependencies {
    implementation("commons-codec:commons-codec:1.17.0")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    implementation("org.web3j:core:4.12.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

