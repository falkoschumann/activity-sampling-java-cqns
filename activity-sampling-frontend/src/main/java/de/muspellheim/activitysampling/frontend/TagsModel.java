/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;

class TagsModel {
  private boolean internalUpdate = false;

  @Getter @Setter Consumer<Set<String>> onSelectedTagsChanged;

  private final BooleanProperty allSelected =
      new SimpleBooleanProperty(true) {
        @Override
        protected void invalidated() {
          var b = getValue();

          if (internalUpdate) {
            return;
          }

          internalUpdate = true;
          selected.values().forEach(it -> it.set(b));
          internalUpdate = false;
          fireSelectedTagsChanged();
        }
      };

  final boolean isAllSelected() {
    return allSelected.get();
  }

  final void setAllSelected(boolean value) {
    allSelected.set(value);
  }

  final BooleanProperty allSelectedProperty() {
    return allSelected;
  }

  private final Map<String, BooleanProperty> selected = new TreeMap<>();

  public BooleanProperty getSelectedFor(String tag) {
    return selected.get(tag);
  }

  Collection<String> getTags() {
    return List.copyOf(selected.keySet());
  }

  void setTags(Collection<String> value) {
    var lastTags = Set.copyOf(selected.keySet());
    lastTags.forEach(
        it -> {
          if (!value.contains(it)) {
            selected.remove(it);
          }
        });

    value.forEach(
        it -> {
          if (!selected.containsKey(it)) {
            selected.put(
                it,
                new SimpleBooleanProperty(true) {
                  @Override
                  protected void invalidated() {
                    getValue();

                    if (internalUpdate) {
                      return;
                    }

                    internalUpdate = true;
                    var b =
                        selected.values().stream()
                            .map(BooleanExpression::getValue)
                            .reduce(Boolean::logicalAnd)
                            .orElse(true);
                    setAllSelected(b);
                    internalUpdate = false;
                    fireSelectedTagsChanged();
                  }
                });
          }
        });
  }

  private void fireSelectedTagsChanged() {
    var selectedTags =
        this.selected.entrySet().stream()
            .filter(it -> it.getValue().get())
            .map(Entry::getKey)
            .collect(Collectors.toSet());
    onSelectedTagsChanged.accept(selectedTags);
  }

  Set<String> getSelectedTags() {
    return selected.entrySet().stream()
        .filter(it -> it.getValue().get())
        .map(Entry::getKey)
        .collect(Collectors.toSet());
  }
}
