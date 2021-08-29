/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class ComponentValueFactory<S, T>
    implements Callback<CellDataFeatures<S, T>, ObservableValue<T>> {
  private final String component;

  private Class<?> columnClass;
  private ComponentReference<T> componentRef;

  public ComponentValueFactory(String component) {
    this.component = component;
  }

  public final String getComponent() {
    return component;
  }

  @Override
  public ObservableValue<T> call(CellDataFeatures<S, T> param) {
    return getCellDataReflectively(param.getValue());
  }

  private ObservableValue<T> getCellDataReflectively(S rowData) {
    if (getComponent() == null || getComponent().isEmpty() || rowData == null) {
      return null;
    }

    if (columnClass == null || !columnClass.equals(rowData.getClass())) {
      columnClass = rowData.getClass();
      componentRef = new ComponentReference<>(rowData.getClass(), getComponent());
    }

    try {
      var value = componentRef.get(rowData);
      return new ReadOnlyObjectWrapper<>(value);
    } catch (Exception e) {
      System.err.println(
          "Can not retrieve component '"
              + getComponent()
              + "' in ComponentValueFactory: "
              + this
              + " with provided record type: "
              + rowData.getClass());
      return null;
    }
  }
}
