module de.muspellheim.activitysampling {
  requires javafx.controls;
  requires static lombok;
  requires de.muspellheim.activitysampling.backend;
  requires de.muspellheim.activitysampling.frontend;

  exports de.muspellheim.activitysampling to
      javafx.graphics;
}
