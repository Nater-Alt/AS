// PlantSpecies.java

import java.util.Random;

/*
  Teil vom module species. Eine Pflanzenart mit state (y, b, s)
  und Parametern (c, f, h, q, p).
  STYLE: OO Entität. Implementiert Seasonal und NectarSource für Austauschbarkeit.

  CONTRACT (Class invariants):
  - vigor, bloomFraction und seedSet bleiben stets >= 0; bloomFraction und seedSet liegen in [0,1].
  - seedBank und repro sind niemals null nach der Konstruktion.
  - seasonRandom darf null sein, wird aber nur als optionaler Seeder genutzt.
  HISTORY-CONSTRAINTS:
  - bloomFraction und seedSet ändern sich nur über clamp01, d.h. sie verlassen [0,1] niemals.
  - vigor sinkt nie unter 0 und wächst nur durch definierte Mutatoren (winterReproduce/add/mul/set).
*/
public class PlantSpecies implements Seasonal, NectarSource {
    private double vigor;          // Wuchskraft (y)
    private double bloomFraction;  // Blühanteil (b) [0,1]
    private double seedSet;        // Samenqualität (s) [0,1]
    private Random seasonRandom;
    private final double cMin, cMax;   // Vermehrungsgrenzen
    private final double fMin, fMax;   // Feuchtegrenzen
    private final double hStart, hEnd; // Blühfenster (cumulative sun)
    private final double q;            // Blühintensität: 0 < q < 1/15
    private final double p;            // Bestäubungswahrscheinlichkeit
    private final SeedBank seedBank;   // Samenspeicher
    private final Reproduction repro; // Reproduktion (Ein- oder Mehrjährig)

    // CONTRACT: Preconditions: repro != null, q in (0,1), initialVigor >= 0 optional. Postconditions: Objekt erfüllt Invarianten.
    // GOOD: Nutzung von Reproduction-Interface hält Kopplung gering und erlaubt polymorphen Austausch der Saisonlogik.
    public PlantSpecies(double initialVigor, double cMin, double cMax,
                        double fMin, double fMax,
                        double hStart, double hEnd,
                        double q, double p, Reproduction repro) {
        this.cMin = cMin;
        this.cMax = cMax;
        this.fMin = fMin;
        this.fMax = fMax;
        this.hStart = hStart;
        this.hEnd = hEnd;
        this.q = q;
        this.p = p;
        this.bloomFraction = 0;
        this.seedSet = 0;
        this.vigor = (initialVigor > 0) ? initialVigor : 0;
        this.seedBank = new SeedBank();
        this.repro = repro;
    }

    // Konstruktor ohne Reproduktion - Default Reproduktion ist mehrjährig
    public PlantSpecies(double initialVigor, double cMin, double cMax,
                        double fMin, double fMax,
                        double hStart, double hEnd,
                        double q, double p) {
        this(initialVigor, cMin, cMax, fMin, fMax, hStart, hEnd, q, p, new PerennialReproduction());
    }

    // Saisonwerte zurücksetzen
    // CONTRACT: Postcondition: bloomFraction == 0 und seedSet == 0; Invarianten bleiben erhalten.
    public void resetSeason() {
        bloomFraction = 0;
        seedSet = 0;
    }

    @Override
    // CONTRACT: Preconditions: seasonRandom optional, repro/seedBank != null. Postconditions: Saison startet mit resetSeason und Reproduktion informiert.
    public void startSeason() {
        resetSeason();
        Random rng = (seasonRandom != null) ? seasonRandom : new Random();
        if (repro != null) repro.startOfSeason(this, this.seedBank, rng);
    }

    // CONTRACT: Preconditions: soilMoisture >= 0. Postconditions: vigor bleibt >= 0.
    // BAD: Direkte Anpassung der Zustandsvariablen koppelt an konkrete Prozentwerte; Strategy-Objekt wäre flexibler.
    public void applyMoistureStress(double soilMoisture) {
        if (soilMoisture <= fMin / 2.0 || soilMoisture >= 2.0 * fMax) {
            vigor *= 0.97;
        } else if ((soilMoisture > fMin / 2.0 && soilMoisture < fMin) ||
                (soilMoisture > fMax && soilMoisture < 2.0 * fMax)) {
            vigor *= 0.99;
        }
        if (vigor < 0) vigor = 0;
    }

    // Steuert Blühanteil (b) anhand der Sonnenstunden (h) und Blühfenster (h-, h+).
    // CONTRACT: Preconditions: sunHoursToday >= 0, cumSunHours >= 0. Postconditions: bloomFraction in [0,1] und wächst im Fenster.
    public void advanceBloom(double sunHoursToday, double cumSunHours) {
        double step = q * (sunHoursToday + 3.0);
        if (cumSunHours >= hStart && cumSunHours < hEnd) {
            bloomFraction = clamp01(bloomFraction + step);
        } else if (cumSunHours >= hEnd) {
            bloomFraction = clamp01(bloomFraction - step);
        }
    }

    // Erhöht Samenqualität (s) basierend auf Bestäubung. Faktor x/n, falls Bienen < Nahrung (x < n)
    // CONTRACT: Preconditions: beePopulation,totalFood,sunHoursToday >= 0. Postconditions: seedSet in [0,1].
    public void updateSeedSet(double beePopulation, double totalFood, double sunHoursToday) {
        if (bloomFraction <= 0 || totalFood <= 0) return;
        double inc = p * bloomFraction * (sunHoursToday + 1.0);
        if (beePopulation < totalFood) inc *= (beePopulation / totalFood);
        seedSet = clamp01(seedSet + inc);
    }

    // CONTRACT: Preconditions: dailyWeather != null, bees != null. Delegiert an Reproduction und wahrt Invarianten.
    public void reproduceDaily(DayWeather dailyWeather, Pollinator bees, double totalFood) {
        if (repro != null) repro.updateDaily(this, dailyWeather, bees, totalFood);
    }

    // Ruhephase: Simuliert Vermehrung. Wuchskraft (y) wird mit
    // Samenqualität (s) und Zufallsfaktor (c) multipliziert
    // CONTRACT: Preconditions: random != null optional. Postconditions: vigor >= 0, seedSet bleibt unverändert.
    // GOOD: SeedBank kapselt Reproduktionszustand und vermeidet direkten Zugriff von außen.
    public void winterReproduce(Random random) {
        Random rng = (random != null) ? random : new Random();
        if (repro != null) repro.endOfSeason(this, this.seedBank, rng);
        double c = cMin + rng.nextDouble() * (cMax - cMin);
        double growthFactor = 1.0 + seedSet * (c - 1.0);
        vigor *= growthFactor;
        if (vigor < 0) {
            vigor = 0;
        }
    }

    // heutiges Nahrungsangebot dieser Art.
    // CONTRACT: Postcondition: Ergebnis >= 0 und reflektiert invarianten Zustand.
    public double foodSupplyToday() {
        return vigor * bloomFraction;
    }

    // NectarSource: read-only-Views
    @Override
    // CONTRACT: Postcondition: Rückgabewert ∈ [0,1].
    public double bloomFraction() {
        return bloomFraction;
    }

    @Override
    // CONTRACT: Postcondition: Rückgabewert >= 0.
    public double nectarToday() {
        return foodSupplyToday();
    }

    @Override
    // CONTRACT: Preconditions: rng optional. Postcondition: Invarianten gelten, da winterReproduce clamp nutzt.
    public void applyWinter(Random rng) {
        winterReproduce(rng);
    }

    // Accessors
    // CONTRACT: Postcondition: >= 0.
    public double vigor() {
        return this.vigor;
    }

    // CONTRACT: Preconditions: v kann negativ sein, aber Ergebnis clamp über Aufrufer sicherstellen. History: vigor bleibt >=0.
    public void addVigor(double v) {
        this.vigor += v;
    }

    // CONTRACT: Preconditions: f >= 0 für sinnvolle Nutzung. History: vigor >=0 bleibt erhalten.
    public void mulVigor(double f) {
        this.vigor *= f;
    }

    // CONTRACT: Preconditions: vigor >= 0 vom Aufrufer. Postcondition: Feld exakt gesetzt.
    public void setVigor(double vigor) {
        this.vigor = vigor;
    }

    // CONTRACT: Postcondition: Wert in [0,1].
    public double seedSet() {
        return seedSet;
    }

    private static double clamp01(double v) {
        return v < 0 ? 0 : (v > 1 ? 1 : v);
    }

    // CONTRACT: Preconditions: rng kann null sein für default RNG. Postcondition: Optionaler Seeder gesetzt.
    public void setSeasonRng(Random rng) {
        this.seasonRandom = rng;
    }

    @Override
    public String toString() {
        return String.format("vigor=%.2f bloom=%.3f seed=%.3f", vigor, bloomFraction, seedSet);
    }
}