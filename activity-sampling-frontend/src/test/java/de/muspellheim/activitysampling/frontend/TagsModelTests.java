/*
 * Activity Sampling - Frontend
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TagsModelTests {
  private TagsModel model;
  private Set<String> selectedTags;

  @BeforeEach
  void setUp() {
    model = new TagsModel();
    model.setOnSelectedTagsChanged(it -> selectedTags = it);
  }

  @Test
  void testInitialState() {
    assertAll(
        () -> assertTrue(model.isAllSelected(), "allSelected"),
        () -> assertEquals(List.of(), model.getTags(), "tags"),
        () -> assertEquals(Set.of(), model.getSelectedTags(), "selectedTags"),
        () -> assertNull(selectedTags, "onSelectedTags"));
  }

  @Test
  void testSetTags() {
    model.setTags(Set.of("Foo", "Bar"));

    assertAll(
        () -> assertTrue(model.isAllSelected(), "allSelected"),
        () -> assertEquals(List.of("Bar", "Foo"), model.getTags(), "tags"),
        () -> assertEquals(Set.of("Bar", "Foo"), model.getSelectedTags(), "selectedTags"),
        () -> assertNull(selectedTags, "onSelectedTags"));
  }

  @Test
  void testChangeOneSelected() {
    model.setTags(Set.of("Foo", "Bar"));

    model.getSelectedFor("Foo").set(false);

    assertAll(
        () -> assertFalse(model.isAllSelected(), "allSelected"),
        () -> assertEquals(List.of("Bar", "Foo"), model.getTags(), "tags"),
        () -> assertEquals(Set.of("Bar"), model.getSelectedTags(), "selectedTags"),
        () -> assertEquals(Set.of("Bar"), selectedTags, "onSelectedTags"));
  }

  @Test
  void testToggleAllSelected() {
    model.setTags(Set.of("Foo", "Bar"));

    model.setAllSelected(false);
    assertAll(
        () -> assertFalse(model.isAllSelected(), "allSelected"),
        () -> assertEquals(List.of("Bar", "Foo"), model.getTags(), "tags"),
        () -> assertEquals(Set.of(), model.getSelectedTags(), "selectedTags"),
        () -> assertEquals(Set.of(), selectedTags, "onSelectedTags"));

    model.setAllSelected(true);
    assertAll(
        () -> assertTrue(model.isAllSelected(), "allSelected"),
        () -> assertEquals(List.of("Bar", "Foo"), model.getTags(), "tags"),
        () -> assertEquals(Set.of("Bar", "Foo"), model.getSelectedTags(), "selectedTags"),
        () -> assertEquals(Set.of("Foo", "Bar"), selectedTags, "onSelectedTags"));
  }

  @Test
  void testSelectAndUpdate() {
    model.setTags(Set.of("Foo", "Bar"));

    model.getSelectedFor("Foo").set(false);
    model.setTags(Set.of("Foo"));

    assertAll(
        () -> assertFalse(model.isAllSelected(), "allSelected"),
        () -> assertEquals(List.of("Foo"), model.getTags(), "tags"),
        () -> assertEquals(Set.of(), model.getSelectedTags(), "selectedTags"),
        () -> assertEquals(Set.of("Bar"), selectedTags, "onSelectedTags"));
  }
}
