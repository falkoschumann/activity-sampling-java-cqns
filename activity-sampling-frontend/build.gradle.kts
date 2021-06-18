plugins {
  id("activity-sampling.java-library-conventions")
  id("activity-sampling.java-openjfx-conventions")
}

dependencies {
  api(project(":activity-sampling-contract"))
  testImplementation("org.mockito:mockito-core:3.11.1")
}
