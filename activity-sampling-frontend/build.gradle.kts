plugins {
  id("activity-sampling.java-library-conventions")
  id("activity-sampling.java-openjfx-conventions")
}

dependencies {
  api(project(":activity-sampling-contract"))
}