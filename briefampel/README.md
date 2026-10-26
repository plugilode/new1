# Briefampel (Android)

Kleine Android-App: Brief scannen, teilen oder einfügen – die Ampel sagt, ob man ihn lesen will, was drinsteht und was zu tun ist. Dazu ein Antwortbrief aus Stichworten.

Plan und Entscheidungen: [PLAN.md](PLAN.md) · Design-Entwurf (M0): https://claude.ai/artifact/W3FxiyNnmWU9t6h9tNtvkr

## Stand: M1

- Kotlin + Jetpack Compose, ein App-Modul, minSdk 26, targetSdk 35
- Design „Jemand Vertrautes nimmt dir den Brief ab“: warmes Briefpapier und Tinte, Fraunces und Nunito Sans, die App spricht in der Ich-Form; Hell und Dunkel (Abendlicht)
- Selbst gezeichnete Ampel aus Keramik mit warmem Licht: pulsiert beim Lesen, Lampe glüht weich auf, Bildschirm färbt sich leicht ein, Vibration je Farbe
- Eingang: Scannen (ML Kit Document Scanner), Datei (PDF/Bilder), Text einfügen, Teilen-Ziel aus anderen Apps
- Ergebnis mit Frist-Leiste, Fakten, versiegeltem Umschlag; Antwort-Werkbank mit Vorschlägen, Ton, Absender, Live-Schreiben mit Stopp, Kopieren, Teilen, Neu formulieren
- Einwilligung vor der ersten Prüfung; Absender nur auf dem Gerät (DataStore, kein Backup)
- **Noch ohne KI:** `SampleProvider` liefert Beispielergebnisse (Rot → Gelb → Grün im Wechsel). Gemini über Firebase AI Logic kommt in M2 hinter das Interface `LlmProvider`.

## Bauen

Voraussetzung: Android Studio (oder JDK 17+ und Android SDK 35).

```bash
./gradlew :app:assembleDebug        # APK: app/build/outputs/apk/debug/app-debug.apk
./gradlew :app:testDebugUnitTest    # Unit-Tests (Parser, Fristen, Stichworte, Beispiel-Anbieter)
./gradlew :app:recordPaparazziDebug # Screenshots aller Bildschirme nach app/src/test/snapshots/images
./gradlew :app:verifyPaparazziDebug # prüft, dass sich das Aussehen nicht ungewollt ändert
```

## Vor dem Play-Store-Upload offen

- Paket-ID `de.briefampel.app` ist ein Platzhalter (lässt sich nach dem ersten Upload nicht mehr ändern)
- `PRIVACY_URL` in `ui/screen/Sheets.kt` auf die echte Datenschutzerklärung setzen
- Schriften: Fraunces, Nunito Sans (SIL Open Font License, siehe `docs/font-licenses`)
