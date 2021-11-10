module de.muspellheim.activitysampling.frontend {
  requires java.desktop;
  requires javafx.controls;
  requires javafx.fxml;
  requires jdk.localedata;
  requires transitive de.muspellheim.activitysampling.contract;

  exports de.muspellheim.activitysampling.frontend;

  opens de.muspellheim.activitysampling.frontend to
      javafx.fxml;
}
