module de.muspellheim.activitysampling.backend {
  requires transitive de.muspellheim.activitysampling.contract;
  requires com.google.gson;
  requires static lombok;

  exports de.muspellheim.activitysampling.backend;
  exports de.muspellheim.activitysampling.backend.adapters;
}
