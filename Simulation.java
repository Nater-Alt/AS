// Simulation.java

import java.util.List;
import java.util.Random;

/*
  Teil vom module simulation. Kapselt einen multi year run
  (ecosystem + bees + weather).
  STYLE: OO Wrapper. run() ist ein prozeduraler Loop.
*/
public final class Simulation {
    private final Ecosystem ecosystem;
    private final BeePopulation bees;
    private final Weather weather;
    private final Random random;
    private final int years;
    private static final int DAYS = 240; // Vegetationsperiode


    public Simulation(List<PlantSpecies> group, Weather weather, long randomSeed, double initialBeePopulation, int years) {
        this.ecosystem = new Ecosystem(group);
        this.weather = weather;
        this.random = new Random(randomSeed);
        this.bees = new BeePopulation(initialBeePopulation);
        this.years = years;
    }

    // vollen Lauf durchführen: alle Tage, dann Winter.
    public void run() {
        for (int year = 1; year <= years; year++) {
            ecosystem.resetSeason(random);
            weather.startSeason();
            for (int day = 1; day <= DAYS; day++) {
                DayWeather w = weather.nextDay();
                ecosystem.dailyUpdate(w, bees);
            }
            bees.applyWinterMortality(random);
            ecosystem.winterAll(random);
        }
    }

    // Read-only-Getter.
    public BeePopulation bees() {
        return bees;
    }

    public Ecosystem ecosystem() {
        return ecosystem;
    }

    public static int seasonDays() {
        return DAYS;
    }
}
