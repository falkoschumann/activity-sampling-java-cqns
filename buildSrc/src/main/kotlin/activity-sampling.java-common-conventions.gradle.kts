plugins {
  java
  jacoco
  id("com.diffplug.spotless")
  id("io.freefair.lombok")
}

version = rootProject.version

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
