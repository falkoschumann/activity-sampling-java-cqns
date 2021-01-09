![Java CI](https://github.com/falkoschumann/activity-sampling-java/workflows/Java%20CI/badge.svg)

# Activity Sampling

Um die aufgewandte Zeit Deiner Aufgaben zu erfassen, fragt Dich die App
_Activity Sampling_ in einstellbaren Abstand nach Deiner aktuellen Tätigkeit.
Deine Antworten werden als CSV-Datei zur leichten Auswertung mit weiteren
Werkzeugen gesichert.

## Installation

### macOS

Die DMG-Datei öffnen und die App in den Ordner _Programme_ ziehen.

### Windows

Die MSI-Datei ausführen. Die App kann anschließend über das Startmenü gestartet
werden.

## Usage

Jedes Mal, wenn die Zeit abgelaufen ist, wird nach Deiner aktuellen Tätigkeit
gefragt. Optional können Tags wie zum Beispiel Kunde, Projekt und/oder Produkt
zu der Tätigkeit angegeben werden.

Voreingestellt ist die Nachfrage in einem Intervall von 20 Minuten und der
Speicherort der Datei `activity-log.csv` im Benutzerverzeichnis. Beides kann in
den Einstellungen geändert werden.

## Contributing

- Der Code Style [Google Java Style Guide][1] wird beim Build geprüft.
- Code formatieren: `./gradlew spotlessApply`
- [Project Lombok][2] wird verwendet, um Boilerplate Code zu reduzieren, es
  werden nur stabile Features verwendet, zum Beispiel: `@NonNull`, `@Data`,
  `@Value` oder `@Builder`.
- Release erstellen: `./gradlew jpackage` 


[1]: https://google.github.io/styleguide/javaguide.html
[2]: https://projectlombok.org
