plugins {
  `groovy-gradle-plugin`
  `java-gradle-plugin`
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
  mavenCentral()
}

dependencies {
  implementation("com.diffplug.spotless:spotless-plugin-gradle:5.12.5")
  implementation("io.freefair.gradle:lombok-plugin:6.0.0-m2")
  implementation("org.openjfx:javafx-plugin:0.0.10")
  implementation("org.ow2.asm:asm:8.0.1")
}

gradlePlugin {
  plugins {
    register("extra-java-module-info") {
      id = "extra-java-module-info"
      implementationClass = "org.gradle.sample.transform.javamodules.ExtraModuleInfoPlugin"
    }
  }
}
