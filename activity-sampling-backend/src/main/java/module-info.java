module de.muspellheim.activitysampling.backend {
  requires transitive de.muspellheim.activitysampling.contract;
  requires java.logging;
  requires java.prefs;
  requires static lombok;
  requires org.apache.commons.csv;

  exports de.muspellheim.activitysampling.backend;
  exports de.muspellheim.activitysampling.backend.adapters;
  exports de.muspellheim.activitysampling.backend.messagehandlers;
}
