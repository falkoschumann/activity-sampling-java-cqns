module de.muspellheim.activitysampling.backend {
  requires static lombok;
  requires transitive de.muspellheim.activitysampling.contract;
  requires de.muspellheim.messages;
  requires java.sql;
  requires org.apache.commons.csv;

  exports de.muspellheim.activitysampling.backend;
  exports de.muspellheim.activitysampling.backend.adapters;
  exports de.muspellheim.activitysampling.backend.messagehandlers;
}
