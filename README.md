![Java CI](https://github.com/falkoschumann/activity-sampling-java/workflows/Java%20CI/badge.svg)

# Activity Sampling

Um die aufgewandte Zeit Deiner Aufgaben zu erfassen, fragt Dich die App
_Activity Sampling_ in einstellbaren Abstand nach Deiner aktuellen Tätigkeit.
Deine Antworten werden zur leichten Auswertung mit weiteren Werkzeugen als
CSV-Datei gesichert.

## Installation

### macOS

Die DMG-Datei öffnen und die App in den Ordner _Programme_ ziehen. Die App kann
anschließend aus dem _Programme_-Ordern gestartet werden.

### Windows

Die MSI-Datei ausführen. Die App kann anschließend über das Startmenü gestartet
werden.

## Usage

Jedes Mal, wenn die Zeit abgelaufen ist, wirst Du nach Deiner aktuellen
Tätigkeit gefragt. Optional können Tags wie zum Beispiel Kunde, Projekt und/oder
Produkt zu der Tätigkeit angegeben werden. Mehrere Tags werden mit Komma (,)
getrennt notiert.

Voreingestellt ist die Nachfrage in einem Intervall von 20 Minuten und der
Speicherort der Datei `activity-log.csv` im Benutzerverzeichnis. Beides kannst
Du in den Einstellungen ändern.

Deine letzten 10 Aktivitäten kannst Du am rechten Bereich der Schaltfläche _Log_
auswählen. Diese stehen ebenso im Kontextmenü des Symbols in der Menüleiste
unter macOS und der Taskleiste unter Windows zur Auswahl.

## Contributing

- Build mit `.gradlew clean build`
- Release erstellen mit `./gradlew jpackage`
- Code Style [Google Java Style Guide][1] wird beim Build geprüft und
  mit `./gradlew spotlessApply` formatiert
- [Project Lombok][2] wird verwendet, um Boilerplate Code zu reduzieren, es
  werden nur stabile Features verwendet, zum Beispiel: `@Getter` oder `@Setter`.

### Distribute for macOS

Im Folgenden müssen `$MAC_SIGNING_USERNAME`, `$MAC_SIGNING_PASSWORD`
und `{RequestUUID}` passend ersetzt werden.

1. Notarisierung der App-Distribution beantragen:

   `xcrun altool --notarize-app --primary-bundle-id de.muspellheim.activitysampling --username $MAC_SIGNING_USERNAME --password $MAC_SIGNING_PASSWORD --file activity-sampling/build/jpackage/activity-sampling-1.1.0.dmg`

2. Status der Notarisierung prüfen:

   `xcrun altool --notarization-info {RequestUUID} --username $MAC_SIGNING_USERNAME --password $MAC_SIGNING_PASSWORD`

3. Wenn Notarisierung beglaubigt, App-Distribution um Information zur
   Notarisierung ergänzen:

   `xcrun stapler staple activity-sampling/build/jpackage/activity-sampling-1.1.0.dmg`


[1]: https://google.github.io/styleguide/javaguide.html
[2]: https://projectlombok.org
