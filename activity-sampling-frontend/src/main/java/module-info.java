module de.muspellheim.activitysampling.frontend {
  requires static lombok;
  requires transitive de.muspellheim.activitysampling.contract;
  requires java.desktop;
  requires transitive javafx.controls;
  requires transitive javafx.fxml;

  exports de.muspellheim.activitysampling.frontend;

  opens de.muspellheim.activitysampling.frontend to
      javafx.fxml;
}
