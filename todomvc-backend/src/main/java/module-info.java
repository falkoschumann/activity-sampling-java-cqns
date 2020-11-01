module de.muspellheim.todomvc.backend {
  requires transitive de.muspellheim.todomvc.contract;
  requires com.google.gson;
  requires static lombok;

  exports de.muspellheim.todomvc.backend;
  exports de.muspellheim.todomvc.backend.adapters;
}
