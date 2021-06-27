import java.time.LocalDate

plugins {
  java
  jacoco
  id("com.diffplug.spotless")
  id("io.freefair.lombok")
}

version = rootProject.version
val copyrightYear by extra(LocalDate.now().year)

repositories {
  mavenCentral()
}

dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.compileJava {
  options.encoding = "UTF-8"
  options.compilerArgs.add("-parameters")
}

java {
  sourceCompatibility = JavaVersion.VERSION_16
  targetCompatibility = JavaVersion.VERSION_16
  modularity.inferModulePath.set(true)
}

tasks {
  processResources {
    filesMatching("**/*.properties") {
      expand("version" to version, "copyrightYear" to copyrightYear)
    }
  }
}

tasks.compileTestJava {
  options.encoding = "UTF-8"
}

tasks.test {
  useJUnitPlatform()
  testLogging {
    events("passed", "skipped", "failed")
  }
  testLogging.showExceptions = true
  finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
}

spotless {
  java {
    googleJavaFormat()
    licenseHeaderFile("config/LicenseHeader.txt")
  }
}
