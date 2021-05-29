plugins {
  id("activity-sampling.java-library-conventions")
  id("activity-sampling.java-modules-conventions")
}

dependencies {
  api(project(":activity-sampling-contract"))
  implementation("org.apache.commons:commons-csv:1.8")
}
