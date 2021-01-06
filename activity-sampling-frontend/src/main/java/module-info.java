module de.muspellheim.activitysampling.frontend {
  requires static lombok;
  requires transitive de.muspellheim.activitysampling.contract;
  requires java.desktop;
  requires transitive javafx.controls;

  exports de.muspellheim.activitysampling.frontend;
}
