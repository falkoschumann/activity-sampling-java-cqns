/*
 * Activity Sampling - Frontend
 * Copyright (c) 2020 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.activitysampling.frontend;

import de.muspellheim.activitysampling.contract.data.ActivityTemplate;
import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;

class TrayIconController {
  @Getter @Setter private Consumer<ActivityTemplate> onActivitySelected;

  private TrayIcon trayIcon;

  TrayIconController() {
    if (!SystemTray.isSupported()) {
      System.out.println("System tray is not supported on this platform");
      return;
    }

    var url = getClass().getResource("tray-icon.png");
    var image = Toolkit.getDefaultToolkit().getImage(url);
    trayIcon = new TrayIcon(image);
  }

  private final BooleanProperty visible =
      new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
          if (!SystemTray.isSupported()) {
            return;
          }

          EventQueue.invokeLater(
              () -> {
                var tray = SystemTray.getSystemTray();
                if (visible.get()) {
                  var missingIconInTray = !List.of(tray.getTrayIcons()).contains(trayIcon);
                  if (missingIconInTray) {
                    try {
                      tray.add(trayIcon);
                    } catch (AWTException e) {
                      System.err.println("Can not add icon to system tray: " + e);
                    }
                  }
                } else {
                  tray.remove(trayIcon);
                }
              });
        }
      };

  public final BooleanProperty visibleProperty() {
    return visible;
  }

  void setRecent(List<ActivityTemplate> value) {
    if (!SystemTray.isSupported()) {
      return;
    }

    var converter = new ActivityTemplateStringConverter();
    EventQueue.invokeLater(
        () -> {
          var menu = new PopupMenu();
          value.forEach(
              it -> {
                MenuItem menuItem = new MenuItem(converter.toString(it));
                menuItem.addActionListener(e -> onActivitySelected.accept(it));
                menu.add(menuItem);
              });
          trayIcon.setPopupMenu(menu);
        });
  }

  void showQuestion() {
    if (!SystemTray.isSupported()) {
      return;
    }

    EventQueue.invokeLater(
        () -> trayIcon.displayMessage("What are you working on?", null, MessageType.NONE));
  }
}
