module de.muspellheim.todomvc.contract {
  requires static lombok;

  exports de.muspellheim.todomvc.contract.data;
  exports de.muspellheim.todomvc.contract.messages.commands;
  exports de.muspellheim.todomvc.contract.messages.queries;

  opens de.muspellheim.todomvc.contract.data;
}
