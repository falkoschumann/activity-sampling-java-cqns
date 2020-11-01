module de.muspellheim.todomvc.frontend {
  requires transitive de.muspellheim.todomvc.contract;
  requires transitive javafx.controls;
  requires javafx.fxml;
  requires static lombok;

  exports de.muspellheim.todomvc.frontend;

  opens de.muspellheim.todomvc.frontend;
}
