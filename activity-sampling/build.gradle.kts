import org.apache.tools.ant.taskdefs.condition.Os

import java.time.LocalDate

plugins {
  id("activity-sampling.java-application-conventions")
  id("activity-sampling.java-openjfx-conventions")
  id("activity-sampling.java-modules-conventions")
  id("com.gluonhq.client-gradle-plugin")
  id("org.beryx.jlink") version "2.24.0"
}

dependencies {
  implementation(project(":activity-sampling-frontend"))
  implementation(project(":activity-sampling-backend"))
}

application {
  mainModule.set("de.muspellheim.activitysampling")
  mainClass.set("de.muspellheim.activitysampling.App")
}

val year = LocalDate.now().year
val copyright = "Copyright (c) 2020-${year} Falko Schumann"

tasks {
  processResources {
    filesMatching("**/*.properties") {
      expand("version" to rootProject.version, "copyright" to copyright)
    }
  }
}

jlink {
  options.addAll(
    "--strip-debug",
    "--compress",
    "2",
    "--no-header-files",
    "--no-man-pages",
    "--include-locales",
    "en,de"
  )
}

if (Os.isFamily(Os.FAMILY_MAC)) {
  jlink {
    jpackage {
      icon = "src/main/macos/AppIcon.icns"
      imageName = "Activity Sampling"
      installerType = "dmg"
      imageOptions = listOf(
        "--copyright", copyright,
        "--mac-sign",
        "--mac-signing-key-user-name", "Falko Schumann (QC6EN37P56)"
      )
    }
  }
}

if (Os.isFamily(Os.FAMILY_WINDOWS)) {
  jlink {
    jpackage {
      icon = "src/main/win/app.ico"
      imageName = "Activity Sampling"
      imageOptions = listOf(
        "--copyright", copyright,
      )
      installerName = "Activity Sampling"
      installerType = "msi"
      installerOptions = listOf(
        "--copyright", copyright,
        "--license-file", "../LICENSE.txt",
        "--win-dir-chooser",
        "--win-menu",
        "--win-menu-group", "Muspellheim",
        "--win-upgrade-uuid", "cce3747c-06dc-466c-8c49-4aebe528da61"
      )
    }
  }
}
