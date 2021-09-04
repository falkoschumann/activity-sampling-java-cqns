module de.muspellheim.activitysampling {
  requires static lombok;
  requires de.muspellheim.activitysampling.backend;
  requires de.muspellheim.activitysampling.frontend;
  requires javafx.controls;

  exports de.muspellheim.activitysampling to
      javafx.graphics;
}
