module de.muspellheim.activitysampling.frontend {
  requires transitive de.muspellheim.activitysampling.contract;
  requires transitive javafx.controls;
  requires java.desktop;
  requires static lombok;

  exports de.muspellheim.activitysampling.frontend;

  opens de.muspellheim.activitysampling.frontend;
}
