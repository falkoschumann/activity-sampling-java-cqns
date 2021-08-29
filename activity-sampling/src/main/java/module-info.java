module de.muspellheim.activitysampling {
  requires de.muspellheim.activitysampling.backend;
  requires de.muspellheim.activitysampling.frontend;
  requires static lombok;

  exports de.muspellheim.activitysampling to
      javafx.graphics;
}
