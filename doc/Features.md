# Activity Sampling

## Protokolliere Aktivität

- Frage Nutzer zyklisch nach aktueller Aktivität (alle 20 min)
- Archiviere Zeitstempel, Intervall, Aktivität und optional Stichworte
- Sichere Protokoll als CSV-Datei

## Parametriere Nachfrage

- Stelle Intervall der Nachfrage auf 15, 20 (Default), 30 oder 60 Minuten
- Ändere Speicherort der Protokolldatei (Default ~/activity-log.csv)

## Importiere Protokoll

- Erhalte zeitliche Reihenfolge aller Aktivitäten
- Füge fehlende Zeitpunkte ohne Nachfrage ein
- Ignoriere doppelte Zeitpunkte, wenn Aktivität und Stichworte gleich
- Frage bei doppelten Zeitpunkten nach, wenn Aktivität oder Stichworte
  unterschiedlich

## Werte Protokoll aus

- Stelle aktuellen Tag als Zeitleiste dar
- Summiere Intervalle für Aktivitäten
- Summiere Intervalle für Stichworte
- Bestimme Durchlaufzeiten für Aktivitäten

## Korrigiere Protokoll

- Unterscheide im Protokoll zwischen neuer, geänderter und gelöschter Aktivität
- Suche nach Text in Aktivität oder Stichwort
- Archiviere Änderung, aber ändere nicht Protokoll
- Archiviere Löschung, aber ändere nicht Protokoll
