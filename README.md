# VokabelStar Android 🦉

> **Barrierefreier Vokabeltrainer wie Duolingo – speziell optimiert für TalkBack & Screenreader**

[![Build Android APK](https://github.com/Lauju1909/VokabelStar-Android/actions/workflows/build-apk.yml/badge.svg)](https://github.com/Lauju1909/VokabelStar-Android/actions/workflows/build-apk.yml)

VokabelStar bringt das motivierende Duolingo-Lernprinzip auf Android – 100% barrierefrei, ohne Schreibzwang und vollständig offline nutzbar.

---

## 🌟 Highlights & Funktionen

* **🔥 Duolingo-Spielsystem:** Tages-Streaks, XP, Herzen (oder unbegrenzte Herzen) und Level-Aufstiege.
* **🗣️ Echte Sprachausgabe:** Native Aussprache englischer Vokabeln und deutscher Begriffe über Android Text-to-Speech.
* **♿ 100% Barrierefrei (TalkBack & Blindengerecht):**
  * Klare ARIA-Labels, eindeutige Schaltflächen und strukturierte Screenreader-Führung.
  * Hoher Kontrastmodus (Gelb auf Schwarz) für Sehbehinderte integriert.
* **🎮 Abwechslungsreiche Übungen komplett ohne Tippen:**
  * Paare Finden (Zuordnen)
  * Hör-Quiz (Hören & Auswählen)
  * Multiple Choice Schnellauswahl
  * Wahr / Falsch Blitz-Check
  * Barrierefreie Karteikarten
  * Wort-Baukasten Puzzle
  * *(Optional)* Schreib-Training & Diktat
* **📥 Universal-Import:** Eigene Vokabellisten direkt aus **Word (.docx)**, **Excel (.xlsx)**, **CSV** oder Text importieren!
* **🔒 100% Lokal & Privat:** Keine Registrierung, keine Tracker, keine Cloud – alle Lernfortschritte bleiben sicher auf dem Smartphone.

---

## 📲 Download & Installation

* **GitHub Releases:** Lade die fertige `.apk` direkt unter [Releases](https://github.com/Lauju1909/VokabelStar-Android/releases) herunter.
* **F-Droid Paketquelle:** Erhältlich über Laujus F-Droid Repository: `https://lauju1909.github.io/fdroid-repo/`

---

## 🛠️ Entwicklung & Build

```bash
# Dependencies installieren
npm install

# Web-Assets zu Android synchronisieren
npx cap sync android

# Debug APK bauen
cd android
./gradlew assembleDebug
```

---

## 📜 Lizenz
MIT License © 2026 Lauri
