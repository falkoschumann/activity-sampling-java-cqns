module de.muspellheim.activitysampling.frontend {
  requires static lombok;
  requires transitive de.muspellheim.activitysampling.contract;
  requires java.desktop;
  requires javafx.controls;
  requires javafx.fxml;
  requires jdk.localedata;

  exports de.muspellheim.activitysampling.frontend;

  opens de.muspellheim.activitysampling.frontend to
      javafx.fxml;
}
