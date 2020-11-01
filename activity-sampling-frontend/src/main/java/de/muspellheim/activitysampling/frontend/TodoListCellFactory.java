/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class TodoListCellFactory<T extends TodoModel>
    implements Callback<ListView<T>, ListCell<T>> {
  @Override
  public ListCell<T> call(ListView<T> param) {
    return new TodoListCell<>();
  }
}
