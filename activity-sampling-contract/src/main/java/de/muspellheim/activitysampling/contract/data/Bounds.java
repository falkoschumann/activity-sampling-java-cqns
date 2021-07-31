/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.data;

public record Bounds(double x, double y, double width, double height) {
  public static final Bounds NULL = new Bounds(0, 0, 0, 0);
}
