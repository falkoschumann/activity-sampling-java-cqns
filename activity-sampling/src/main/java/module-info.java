module de.muspellheim.activitysampling {
  requires javafx.controls;
  requires de.muspellheim.activitysampling.backend;
  requires de.muspellheim.activitysampling.frontend;

  exports de.muspellheim.activitysampling to
      javafx.graphics;
}
