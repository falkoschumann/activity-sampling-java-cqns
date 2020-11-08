module de.muspellheim.activitysampling.contract {
  requires static lombok;

  exports de.muspellheim.activitysampling.contract.data;
  exports de.muspellheim.activitysampling.contract.messages.commands;
  exports de.muspellheim.activitysampling.contract.messages.queries;

  opens de.muspellheim.activitysampling.contract.data;

  exports de.muspellheim.activitysampling.contract.messages.notifications;
  exports de.muspellheim.activitysampling.contract.util;
}
