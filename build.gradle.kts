import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED

plugins {
    kotlin("jvm")
    id("com.github.ben-manes.versions")
    jacoco
    id("io.gitlab.arturbosch.detekt")
    id("com.dorongold.task-tree")
}

group = "tdd.study.group.supermarket"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenLocal()
    mavenCentral()
    google()
}
//https://docs.gradle.org/6.0.1/userguide/java_testing.html#sec:configuring_java_integration_tests/
sourceSets.create("integrationTest") {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

configurations["integrationTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

dependencies {
    val archunitVersion: String by project
    val hamkrestVersion: String by project
    val http4kVersion: String by project
    val junitVersion: String by project
    val kotestVersion: String by project
    val mockkVersion: String by project
    val moshiVersion: String by project

    implementation(kotlin("reflect"))
    implementation(platform("org.http4k:http4k-bom:$http4kVersion"))

    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    implementation("com.squareup.moshi:moshi:$moshiVersion")

    implementation("org.http4k:http4k-client-apache")
    implementation("org.http4k:http4k-client-okhttp")
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-format-moshi")
    implementation("org.http4k:http4k-server-undertow")
    implementation("org.http4k:http4k-template-handlebars")
    implementation("org.http4k:http4k-testing-approval")

    //detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))

    testImplementation("com.natpryce:hamkrest:$hamkrestVersion")
    testImplementation("com.tngtech.archunit:archunit:$archunitVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-sql:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.http4k:http4k-testing-approval")
    testImplementation("org.http4k:http4k-testing-chaos")
    testImplementation("org.http4k:http4k-testing-hamkrest")
    testImplementation("org.http4k:http4k-testing-kotest")
    testImplementation("org.http4k:http4k-testing-servirtium")
    testImplementation("org.http4k:http4k-testing-webdriver")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-api")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-console")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
        kotlinOptions {
            jvmTarget = "11"
            javaParameters = true
            freeCompilerArgs = listOf("-Xjvm-default=all")
            //useIR = true
        }
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }

    test {
        enableAssertions = true
        useJUnitPlatform()
        testLogging {
            events(
                PASSED,
                SKIPPED,
                FAILED
            )
        }
        finalizedBy(jacocoTestReport) // report is always generated after tests run
    }

    task<Test>("unitTest") {
        description = "Runs unit tests."
        useJUnitPlatform {
            excludeTags("integration")
            excludeTags("acceptance")
        }
        shouldRunAfter(test)
    }

    task<Test>("integrationTest") {
        description = "Runs integration tests."
        useJUnitPlatform {
            includeTags("integration")
        }
        shouldRunAfter(test)
    }

    task<Test>("acceptanceTest") {
        description = "Runs acceptance tests."
        useJUnitPlatform {
            includeTags("acceptance")
        }
        shouldRunAfter(test)
    }

    check {
        dependsOn("integrationTest")
    }

    jacocoTestReport {
        dependsOn(test) // tests are required to run before generating the report
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            enabled = true

            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                minimum = "1.0".toBigDecimal()
            }
        }
    }
}

tasks.dependencyUpdates {
    resolutionStrategy {
        componentSelection {
            all {
                if (!isStable(candidate.version) && isStable(currentVersion)) {
                    reject("Release candidate")
                }
            }
        }
    }

    // optional parameters
    checkForGradleUpdate = true
    outputFormatter = "json"
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"
}

fun isStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA" /*, "M"*/).any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    return stableKeyword || regex.matches(version)
}
