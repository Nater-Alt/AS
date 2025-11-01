# Aufgabe 3 – vollständige To-do-Liste / Checkliste

## 0. Orga & Rahmen

* **Thema der Aufgabe:**

  * Zusicherungen & Analyse des bestehenden Programms
  * Funktionaler Programmierteil
  * Paralleler oder nebenläufiger Programmierteil. 

* **Basis-Code:**
  Du arbeitest auf deiner Lösung von Aufgabe 2. Der existierende Code (bis auf neue Kommentare) soll möglichst unverändert bleiben. Neue Funktionsteile (funktional + parallel/nebenläufig) werden ergänzt. 

* **Abgabeinfos:**

  * Ausgabe: 27.10.2025
  * Deadline: 03.11.2025 um 13:00
  * Abgabeordner: `Aufgabe1-3`
  * Programmstart: `java Test` muss alles Nötige ausführen (alter Code + neue Teile). 

* **Dokumentation der Zuständigkeiten:**
  Ganz oben in `Test.java` muss ein Kommentar stehen, der erklärt, wer aus der Gruppe was gemacht hat – nicht nur für Aufgabe 2, sondern auch für diese Aufgabe 3 (wer hat Zusicherungen gemacht, wer Funktional, wer Parallel/Nebenläufig, etc.). Nicht jede Person muss an jedem Teil arbeiten, aber das soll klar dokumentiert sein. 

---

## 1. Zusicherungen & Analyse des bestehenden Programms

### 1.1 Prozedural vs. OO klar markieren

* Markiere im gesamten bestehenden Code (aus Aufgabe 2), welche Stellen prozedural sind und welche objektorientiert sind.
* Das soll so gemacht werden, dass eindeutig ist:

  * "Das hier ist prozeduraler Stil"
  * "Das hier ist objektorientierter Stil". 
* In deinem aktuellen Code passiert das schon teilweise mit Kommentaren wie `STYLE: OO Entität`, `STYLE: prozedural`, etc. – diese Markierungen müssen durchgängig sein und eindeutig bleiben. Organisatorische Kommentare (wie wer was geschrieben hat oder `STYLE:`-Hinweise) dürfen bleiben. Falls so ein Kommentar nicht klar als organisatorisch erkennbar ist, startet ihn mit `NOTE:`. 

### 1.2 Design-by-Contract Kommentare (Zusicherungen)

* **Für *alle* objektorientierten Programmteile** musst du Kommentare hinzufügen, die wie Verträge (Design by Contract) formuliert sind. Diese Kommentare sind Teil der Lösung. 

* Das heißt:

  * **Vorbedingungen (Preconditions)**: Was muss der Client garantieren, bevor er die Methode aufruft?
  * **Nachbedingungen (Postconditions)**: Was garantiert die Methode/Klasse nach Ausführung?
  * **Invarianten**: Was bleibt für dieses Objekt immer gültig (z.B. Bereiche von Feldern wie `0 ≤ population`)?
  * **History-Constraints**: Regeln über die Veränderung im Verlauf (z.B. "dieses Feld steigt nur", "dieses Feld wird nie negativ").
  * Diese Zusicherungen sollen in normaler Sprache (Deutsch oder Englisch) als Kommentare im Code stehen. Es reicht, sie als Text zu formulieren, aber sie müssen klar lesbar als Zusicherungen erkennbar sein – also nicht nur schwammige Doku, sondern wirklich Verträge. 
  * Du darfst vorhandene Kommentare umschreiben, damit sie jetzt klar als Zusicherungen lesbar sind.

* Ziel:

  * Jeder Client darf nur das erwarten, was der Server zusichert.
  * Jeder Server darf nur das annehmen, was der Client verspricht.
  * Mit anderen Worten: Client- und Serverseite müssen zueinander passen. Du musst explizit prüfen, ob das im Code erfüllt wird. 

* Hinweis:

  * Es ist erlaubt und sogar empfohlen, bei jeder Zusicherung dazuzuschreiben, welche Art sie ist (Vorbedingung, Nachbedingung, Invariante, History-Constraint). Das hilft beim Überprüfen. 

### 1.3 Vererbung / Subtypen prüfen

* Überprüfe für **jede** Stelle mit `extends` oder `implements`, ob die Verträge korrekt sind.

  * Untertypen dürfen **Vorbedingungen nur abschwächen** (niemals strenger machen).
  * Untertypen müssen **Nachbedingungen und Invarianten nur verstärken** (also mindestens so stark wie der Obertyp, eher stärker / konkreter).
  * Ähnliches gilt für History-Constraints. 
* Falls du Widersprüche oder Verletzungen findest, markiere sie im Code mit Kommentaren, die mit `ERROR:` beginnen.

  * In diesen `ERROR:`-Kommentaren soll kurz stehen:

    * Wo das Problem liegt,
    * Warum das ein Problem ist,
    * Was man am Code ändern müsste, um es sauber zu lösen.
  * Wichtig: Du sollst den eigentlichen Code (bis auf Kommentare) **nicht** ändern, selbst wenn du Fehler findest. Die Aufgabe will die Analyse, nicht den Fix. 

### 1.4 GOOD: / BAD: Kommentare im OO-Teil

Finde **mindestens drei** Stellen im objektorientierten Teil, an denen etwas **gut** ist, und kommentiere dort mit `GOOD:`. Und mindestens **drei** Stellen, wo etwas **schlecht** ist, und kommentiere dort mit `BAD:`. Insgesamt also mindestens 6 Kommentare nur für OO. 

* Schreibe `GOOD:` an Stellen, wo mindestens eines von diesen zutrifft:

  * Die Klasse hat **hohen Zusammenhalt** (Cohesion): Die Attribute und Methoden gehören eng zusammen; du kannst dir leicht vorstellen, wie eine schlechtere Version aussehen würde, die unzusammenhängende Verantwortlichkeiten mischt.
  * Die **Kopplung ist schwach**: Objekte sind nur locker voneinander abhängig; du kannst dir leicht vorstellen, wie eine schlechtere Version aussehen würde, wo alles hart verdrahtet ist.
  * **Dynamisches Binden (Polymorphie)** wird sinnvoll eingesetzt und macht den Code wartbarer/übersichtlicher. Beispiel in deinem Code: `Reproduction` ist ein Interface, `AnnualReproduction` und `PerennialReproduction` sind zwei konkrete Varianten – und `PlantSpecies` arbeitet nur über das Interface. Das entkoppelt die Logik. Genau so etwas will man als `GOOD:` kennzeichnen. 

* Schreibe `BAD:` an Stellen im OO-Teil, wo etwas davon passiert:

  * Der Klassenzusammenhalt ist nicht ideal (z.B. eine Klasse macht zu viel / mischt Zuständigkeiten).
  * Die Kopplung ist unnötig stark (z.B. zwei Klassen wissen zu viel voneinander oder greifen quer in den Zustand).
  * Du hast auf dynamisches Binden verzichtet oder konntest es nicht nutzen, und das hat den Code unübersichtlicher gemacht.
  * Oder allgemein: eine Designentscheidung macht die Wartung schwerer.
  * Zu jedem `BAD:` musst du kurz schreiben:

    * Warum es schlecht ist,
    * Wie es dazu gekommen ist (z.B. Zeitdruck, Legacy aus Aufgabe 2, etc.),
    * Wie man es besser machen könnte. 

---

## 2. Analyse des prozeduralen Teils

Du musst das Gleiche auch für den prozeduralen Teil machen:

* Mindestens **drei `GOOD:`-Kommentare** an guten prozeduralen Stellen.
* Mindestens **drei `BAD:`-Kommentare** an schlechten prozeduralen Stellen.
* Das sind zusätzlich zu den OO-Kommentaren – insgesamt also mindestens zwölf Stellen mit GOOD:/BAD:. 

**Was gilt als GOOD: im prozeduralen Teil?**

* Kontrollfluss gut nachvollziehbar.
* Keine oder bewusst minimierte globale Zustände / versteckte Aliase.
* Strukturen sind klar, linear, verständlich.
* Wichtig: Es zählt nur dann als "gut", wenn es nicht einfach automatisch so geworden ist, sondern eine bewusste Entscheidung war. (Also du musst argumentieren, warum das hier bewusst gut gemacht ist.) 

**Was gilt als BAD: im prozeduralen Teil?**

* Kontrollfluss schwer zu folgen.
* Viele implizite Abhängigkeiten.
* Zu viel globale, verteilte, schwer überprüfbare Zustände.
* Dinge sind zusammengeschoben, die man besser trennen sollte.
* Und wieder: du musst kurz sagen, wie man’s verbessern könnte. 

---

## 3. Funktionaler Teil (Neu implementieren)

Du musst dein bestehendes Programm um **eine neue Funktionalität** erweitern (oder eine alternative Implementierung von etwas, das du schon hast), und diese neue Funktionalität muss **in funktionalem Stil** implementiert sein. Das ist ein komplett neuer Block Code, kein Kommentar-Refactor. Dieser Block zählt als Teil der Abgabe. 

### 3.1 Anforderungen an den funktionalen Teil

* Mindestens grob ~100 Zeilen eigener funktionaler Logik sind zu erwarten (das ist als Untergrenze formuliert). Weniger ist riskant. 

* Der Code soll *funktional* sein:

  * Nutze referentielle Transparenz (d.h. gleicher Input ⇒ gleicher Output, keine versteckten Seiteneffekte).
  * Schreibe Funktionen, die Werte zurückgeben statt globalen Zustand zu verändern.
  * Nutze entweder:

    * rekursiven Stil
    * oder applikativen Stil über Java-8-Streams / Lambdas / Higher-Order Functions
    * oder eine sinnvolle Mischung aus beidem. 
  * Setze gezielt Lambdas ein.
  * Nutze höherwertige Funktionen (Funktionen, die Funktionen als Parameter nehmen oder Funktionen zurückgeben). Das kann deine eigenen Utilities einschließen – du darfst selbst solche Higher-Order-Funktionen schreiben. 
  * Nutze funktionale Programmierung für eher algorithmisch komplexere Teile, also nicht nur triviale 3-Zeilen-Filter, sondern Dinge, wo diese Denkweise wirklich hilft, kompakteren und saubereren Code zu schreiben. 

* Der funktionale Teil muss beim Aufruf von `java Test` auch wirklich laufen / ausgeführt werden, also nicht nur als tote Utility-Klasse rumliegen. Das heißt: `main` (bzw. Code aus `Test.main`) muss diesen Teil starten/anzeigen/benutzen. 

### 3.2 Dokumentation des funktionalen Teils

* Alle neuen funktionalen Programmteile müssen mit Kommentaren markiert sein, die mit `STYLE:` beginnen.

  * Diese `STYLE:`-Kommentare sollen erklären:

    * dass es sich hier um funktionalen Code handelt,
    * wie dieser Teil logisch arbeitet,
    * wie dieser Teil mit dem Rest des Programms zusammenspielt,
    * und wie dabei referentielle Transparenz sichergestellt bleibt (z.B. "diese Funktion liest keinen globalen Zustand, sondern bekommt alles als Parameter und gibt ein Ergebnis zurück"). 

* In funktionalem Code kannst du (und sollst du) auch wieder Vor-/Nachbedingungen dokumentieren, aber da eher aus Sicht von "Input X ⇒ Output Y" statt aus Sicht von Objektzuständen. Das passt gut zu Funktionen ohne Seiteneffekte. 

---

## 4. Paralleler oder nebenläufiger Teil (Neu implementieren)

Zusätzlich zum funktionalen Teil musst du **noch einen zweiten neuen Teil** schreiben, der Parallelität oder Nebenläufigkeit verwendet. Das ist ein weiterer Block neuer Funktionalität (auch wieder ~100 Zeilen Mindestumfang). 

### 4.1 Anforderungen an Parallelität / Nebenläufigkeit

* Du darfst frei wählen:

  * parallele Programmierung (z.B. echte Parallelisierung von Berechnungen), **oder**
  * nebenläufige Programmierung (Concurrency, z.B. mehrere Threads, die interleaved arbeiten, evtl. mit Synchronisation).
* Diese neue Funktionalität darf entweder etwas komplett Neues sein, **oder** eine alternative Implementierung von etwas, das du im bestehenden Code schon hast – aber jetzt eben parallel/ nebenläufig. 
* Wähle etwas, das zu den Zielen des Paradigmas passt:

  * z.B. Parallelisierung, um Performance / Durchsatz zu steigern,
  * Concurrency, um zwei Dinge gleichzeitig zu modellieren (z.B. Umwelt tickt und Bienenpopulation tickt gleichzeitig).
* Dein Code sollte versuchen, diese Ziele auch wirklich zu erreichen (nicht nur "Thread.sleep() als Deko"). Das muss nachvollziehbar sein. 
* Auch dieser Teil muss bei `java Test` tatsächlich ausgeführt werden. Er darf nicht einfach nur existieren. 

### 4.2 Dokumentation des parallelen / nebenläufigen Teils

* Alle neuen parallelen oder nebenläufigen Teile müssen mit Kommentaren markiert sein, die mit `STYLE:` beginnen.

  * Diese Kommentare müssen erklären:

    * welches Paradigma hier verwendet wird (parallel vs. nebenläufig),
    * welches konkrete Ziel damit verfolgt wird (z.B. schneller rechnen, unabhängige simultane Prozesse modellieren, etc.),
    * wie es erreicht wird (z.B. Aufteilen in Tasks, Threads, Thread-Safety, Synchronisation, etc.),
    * und wie gut oder schlecht der Rest des bestehenden Programms dieses Ziel unterstützt.

      * Beispiel: "Das vorhandene Design nutzt Mutable State an vielen Stellen → macht Threadsynchronisation komplizierter."
      * oder: "Lose Kopplung der Module erlaubt parallele Ausführung einzelner Schritte." 

---

## 5. Weitere Qualitätsanforderungen & Tipps (explizit gefordert)

* **Verträge nicht künstlich verbiegen.**
  Falls du merkst, dass Client und Server logisch nicht zusammenpassen, schreib nicht einfach superfancy angepasste Zusicherungen, nur damit es so aussieht, als wäre alles korrekt.

  * Stattdessen: kommentiere das ehrlich mit `ERROR:` plus kurzer Analyse.
  * Hintergrund: Wenn du die Verträge "hinbiegest", wird der Code schwer wartbar und unbrauchbar, weil plötzlich sinnlose Details Teil des offiziellen Vertrags werden. Genau das sollst du NICHT tun. 

* **Subtyping besonders kritisch prüfen.**
  Fehler kommen sehr oft bei `implements`/`extends`. Du musst checken, ob Untertypen wirklich Ersetzbarkeit garantieren (Liskov: schwächere Vorbedingungen, stärkere Nachbedingungen usw.). Falls nicht: `ERROR:`. Diese Prüfungen sind Teil der Aufgabe, nicht "Nice to have". 

* **Markiere gute und schlechte Stellen bewusst.**
  Beim Einfügen von `GOOD:`- und `BAD:`-Kommentaren sollst du aktiv über Wartbarkeit, Wiederverwendbarkeit, Kopplung, Kohäsion und Kontrollfluss nachdenken – die Aufgabe will, dass du deine eigenen Stärken/Schwächen erkennst. Das ist ein offizielles Lernziel. Mindestens 12 solcher Markierungen insgesamt:

  * ≥3 GOOD im OO-Teil
  * ≥3 BAD im OO-Teil
  * ≥3 GOOD im prozeduralen Teil
  * ≥3 BAD im prozeduralen Teil. 

* **Abhängigkeiten minimieren bei neuen Teilen.**
  Die neuen funktionalen und parallelen/nebenläufigen Implementationen sollen möglichst geringe Kopplung zum bestehenden Code haben. Also: lieber extra Datenstrukturen / Adapter schreiben, statt überall Seiteneffekte in die alten Klassen zu pushen. Das entspricht direkt der Aufgabenbeschreibung. 

* **Kommentare in verschiedenen Paradigmen haben unterschiedliche Rollen.**

  * Objektorientiert: Kommentare = Verträge (Vorbedingung, Nachbedingung, Invariante, History-Constraint).
  * Prozedural: Kommentare erklären Kontrollfluss, globale Zustände, Datenflüsse.
  * Funktional: Kommentare erklären die Idee / Transformationen, oft vollkommen kontextfrei ("für Input X berechnet diese Funktion Y").
  * Parallel / nebenläufig: Kommentare erklären die Ziele des Paradigmas (Parallelisierung, Nebenläufigkeit), und wie Synchronisation / Unabhängigkeit / Performance erreicht wird. 

---

## 6. Zusammenfassender Minimal-Deliverable-Check

Diese Punkte *müssen* am Ende erfüllt sein, damit die Aufgabe formal erledigt ist:

1. **Markierung prozedural vs. OO** überall im bestehenden Code. `STYLE:`-Kommentare sind erlaubt/gewünscht. Falls organisatorisch, beginne mit `NOTE:`. 
2. **OO-Teil hat Design-by-Contract-Kommentare** (Vor-/Nachbedingung, Invariante, History-Constraint) an Klassen und Methoden. 
3. **Vererbung/Subtyping geprüft.**

   * Wo gebrochen: `ERROR:` + kurze Analyse, Code selbst unverändert lassen. 
4. **Mindestens 12 Qualitätsmarker insgesamt:**

   * ≥3× `GOOD:` im OO-Teil
   * ≥3× `BAD:` im OO-Teil
   * ≥3× `GOOD:` im prozeduralen Teil
   * ≥3× `BAD:` im prozeduralen Teil.
     Jeder Kommentar erklärt kurz das Warum. 
5. **Funktionaler Programmierteil (~100+ Zeilen)**

   * klar als funktional markiert (`STYLE:` …),
   * referentiell transparent,
   * nutzt rekursive / applikative / Lambda / Higher-Order-Funktionen,
   * ist algorithmisch nicht trivial,
   * läuft automatisch in `java Test`. 
6. **Paralleler oder nebenläufiger Programmierteil (~100+ Zeilen)**

   * klar als parallel / nebenläufig markiert (`STYLE:` …),
   * erklärt Ziele (Performance, Gleichzeitigkeit, etc.) und wie du sie erreichst,
   * läuft automatisch in `java Test`. 
7. **`Test.java` am Anfang:** Kommentar mit Aufgabenverteilung (wer hat was gemacht, inkl. dieser Aufgabe 3). 
8. **Abgabe:** Alles in Verzeichnis `Aufgabe1-3`, Deadline 03.11.2025 13:00, startbar mit `java Test`. 

Wenn all das gegeben ist, erfüllst du die formalen Anforderungen der Aufgabe 3.
