module de.muspellheim.activitysampling.backend {
  requires java.logging;
  requires java.prefs;
  requires static lombok;
  requires org.apache.commons.csv;
  requires transitive de.muspellheim.activitysampling.contract;

  exports de.muspellheim.activitysampling.backend;
  exports de.muspellheim.activitysampling.backend.adapters;
  exports de.muspellheim.activitysampling.backend.messagehandlers;
}
