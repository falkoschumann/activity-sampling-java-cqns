module de.muspellheim.activitysampling.backend {
  requires transitive de.muspellheim.activitysampling.contract;
  requires static lombok;

  exports de.muspellheim.activitysampling.backend;
  exports de.muspellheim.activitysampling.backend.adapters;
  exports de.muspellheim.activitysampling.backend.messagehandlers;
}
