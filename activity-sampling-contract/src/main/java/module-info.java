module de.muspellheim.activitysampling.contract {
  requires static lombok;
  requires de.muspellheim.messages;

  exports de.muspellheim.activitysampling.contract.data;
  exports de.muspellheim.activitysampling.contract.messages.commands;
  exports de.muspellheim.activitysampling.contract.messages.queries;
}
