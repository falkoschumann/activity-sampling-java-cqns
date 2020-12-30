/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import java.time.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

class PeriodProgress extends VBox {
  private final ProgressBar progressBar;
  private final Label remainingTimeLabel;

  PeriodProgress() {
    remainingTimeLabel = new Label("00:20:00");
    remainingTimeLabel.setMaxWidth(Double.MAX_VALUE);
    remainingTimeLabel.setAlignment(Pos.CENTER);
    VBox.setMargin(remainingTimeLabel, new Insets(Views.GAP, 0, 0, 0));

    progressBar = new ProgressBar();
    progressBar.setMaxWidth(Double.MAX_VALUE);
    progressBar.setProgress(0);

    setSpacing(Views.GAP);
    getChildren().setAll(remainingTimeLabel, progressBar);
  }

  void start(Duration period) {
    updateRemainingTime(period);
    progressBar.setProgress(0.0);
  }

  void progress(Duration period, Duration elapsedTime, Duration remainingTime) {
    updateRemainingTime(remainingTime);
    var progress = (double) elapsedTime.getSeconds() / period.getSeconds();
    progressBar.setProgress(progress);
  }

  void end() {
    updateRemainingTime(Duration.ZERO);
    progressBar.setProgress(1.0);
  }

  private void updateRemainingTime(Duration remainingTime) {
    var text = new DurationStringConverter().toString(remainingTime);
    remainingTimeLabel.setText(text);
  }
}
