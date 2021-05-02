/*
 * Activity Sampling - Contract
 * Copyright (c) 2021 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.contract.messages.commands;

import java.nio.file.Path;
import lombok.NonNull;

public record ChangeActivityLogFileCommand(@NonNull Path activityLogFile) {}
