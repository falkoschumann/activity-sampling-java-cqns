module de.muspellheim.activitysampling.contract {
  requires de.muspellheim.messages;
  requires static lombok;

  exports de.muspellheim.activitysampling.contract.data;
  exports de.muspellheim.activitysampling.contract.messages.commands;
  exports de.muspellheim.activitysampling.contract.messages.queries;
}
