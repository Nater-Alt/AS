// Weather.java

import java.util.Random;

/*
  Teil vom module environment. Erzeugt daily weather (clouds, rain, evaporation)
  und die day length 8h -> 16h -> 8h. Hält day, cumulative sun, soil moisture, clouds.
  STYLE: OO-Entität
*/
public class Weather implements Seasonal {
    private static final int SEASON_DAYS = 240;
    private static final double MIN_DAYLEN = 8.0;
    private static final double MAX_DAYLEN = 16.0;

    private final Random rng;

    private int day;
    private double cumSunHours;    // cumulative sunshine
    private double soilMoisture;   // soil moisture in [0,1]
    private double cloud;          // cloud fraction in [0,1]
    private final double rho = 0.93; // Persistenz (higher -> longer dry/wet spells)
    private final double sigma = 0.10; // täglicher wobble (higher -> jumpier clouds)

    private final DayLengthModel dayLength;
    private final double latitude;
    private final int dayOfYearStart;
    private int dayOfYear;

    // RNG setzen
    public Weather(long seed, DayLengthModel dayLength, double latitude, int dayOfYearStart) {
        this.rng = new Random(seed);
        this.dayLength = dayLength;
        this.latitude = latitude;
        this.dayOfYearStart = dayOfYearStart;
    }


    // Saisonstartwerte setzen.
    @Override
    public void startSeason() {
        day = 0;
        cumSunHours = 0.0;
        soilMoisture = rng.nextDouble();   // start mid moisture
        cloud = rng.nextDouble();  // start half-cloudy
        dayOfYear = dayOfYearStart;
    }

    // einen Tag fortschreiben und Snapshot liefern.
    public DayWeather nextDay() {
        day++;
        if (day > SEASON_DAYS) day = SEASON_DAYS;

        cloud = clamp(0.5 + rho * (cloud - 0.5) + sigma * rng.nextGaussian(), 0.0, 1.0);

        if (dayOfYear < 1 || dayOfYear > 365) dayOfYear = 1;
        int today = dayOfYear;
        dayOfYear++;
        if (dayOfYear > 365) dayOfYear = 1;

        // Tageslänge: Dreieck 8h -> 16h -> 8h
        double dayLen = dayLength.lightHours(today, latitude);

        dayLen = Math.max(0, Math.min(24, dayLen));
        // Sonnenschein - Wolken
        double sun = Math.max(0.0, dayLen * (1.0 - cloud));

        // Sonnenschein sammeln und Wetter Snapshot zurückgeben
        cumSunHours += sun;

        // Regen: wahrscheinlich nur bei starker Bewölkung; Menge skaliert mit der Bewölkung
        double rainChance = Math.max(0.0, cloud - 0.55);
        boolean rains = rng.nextDouble() < rainChance;
        double rainAmt = rains ? rainChance * (0.02 + 0.04 * rng.nextDouble()) : 0.0;

        double evap = 0.01 + 0.04 * (sun / MAX_DAYLEN); // [0.01 , 0.05]
        soilMoisture = clamp(soilMoisture + rainAmt - evap, 0.05, 1.0);

        return new DayWeather(sun, cumSunHours, soilMoisture);
    }

    // RNG freigeben (Winter nutzt denselben Zufall).
    public Random random() {
        return rng;
    }

    // STYLE: prozedurale Math-helfer
    private static double dayLengthTriangle(int d, int seasonDays, double min, double max) {
        int mid = seasonDays / 2;
        if (d <= 0) return min;
        if (d <= mid) {
            double t = d / (double) mid;         // 0..1 rising
            return min + (max - min) * t;
        } else {
            double t = (d - mid) / (double) mid; // 0..1 falling
            return max - (max - min) * t;
        }
    }

    private static double clamp(double x, double lo, double hi) {
        return x < lo ? lo : (Math.min(x, hi));
    }

    @Override
    public void applyWinter(Random rng) {
    }

}
