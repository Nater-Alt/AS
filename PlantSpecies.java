// PlantSpecies.java

import java.util.Random;

/*
  Teil vom module species. Eine Pflanzenart mit state (y, b, s)
  und Parametern (c, f, h, q, p).
  STYLE: OO Entität. implementiert Seasonal und NectarSource für Austauschbarkeit
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

    // Konstruktor. Initialisiert alle art-spezifischen Konstanten (y>=0, b=0, s=0)
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
    public void resetSeason() {
        bloomFraction = 0;
        seedSet = 0;
    }

    @Override
    public void startSeason() {
        resetSeason();
        Random rng = (seasonRandom != null) ? seasonRandom : new Random();
        if (repro != null) repro.startOfSeason(this, this.seedBank, rng);
    }

    // Ist die Feuchte außerhalb des Intervalls [fMin,fMax] dann reduziere Wuchskraft um 1% oder 3%
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
    public void advanceBloom(double sunHoursToday, double cumSunHours) {
        double step = q * (sunHoursToday + 3.0);
        if (cumSunHours >= hStart && cumSunHours < hEnd) {
            bloomFraction = clamp01(bloomFraction + step);
        } else if (cumSunHours >= hEnd) {
            bloomFraction = clamp01(bloomFraction - step);
        }
    }

    // Erhöht Samenqualität (s) basierend auf Bestäubung. Faktor x/n, falls Bienen < Nahrung (x < n)
    public void updateSeedSet(double beePopulation, double totalFood, double sunHoursToday) {
        if (bloomFraction <= 0 || totalFood <= 0) return;
        double inc = p * bloomFraction * (sunHoursToday + 1.0);
        if (beePopulation < totalFood) inc *= (beePopulation / totalFood);
        seedSet = clamp01(seedSet + inc);
    }

    public void reproduceDaily(DayWeather dailyWeather, Pollinator bees, double totalFood) {
        if (repro != null) repro.updateDaily(this, dailyWeather, bees, totalFood);
    }

    // Ruhephase: Simuliert Vermehrung. Wuchskraft (y) wird mit
    // Samenqualität (s) und Zufallsfaktor (c) multipliziert
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
    public double foodSupplyToday() {
        return vigor * bloomFraction;
    }

    // NectarSource: read-only-Views
    @Override
    public double bloomFraction() {
        return bloomFraction;
    }

    @Override
    public double nectarToday() {
        return foodSupplyToday();
    }

    @Override
    public void applyWinter(Random rng) {
        winterReproduce(rng);
    }

    // Accessors
    public double vigor() {
        return this.vigor;
    }

    public void addVigor(double v) {
        this.vigor += v;
    }

    public void mulVigor(double f) {
        this.vigor *= f;
    }

    public void setVigor(double vigor) {
        this.vigor = vigor;
    }

    public double seedSet() {
        return seedSet;
    }

    private static double clamp01(double v) {
        return v < 0 ? 0 : (v > 1 ? 1 : v);
    }

    public void setSeasonRng(Random rng) {
        this.seasonRandom = rng;
    }

    @Override
    public String toString() {
        return String.format("vigor=%.2f bloom=%.3f seed=%.3f", vigor, bloomFraction, seedSet);
    }
}