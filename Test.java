import java.util.*;

/**
 PROJECT: Bees Simulation

 Module environment: Weather, DayWeather -> macht daily weather (sun, rain, soil).
 Module species: PlantSpecies, NectarSource -> plants: growth/bloom/seeds
 Module population: BeePopulation, Pollinator -> bees + interface; winter mortality.
 Module lifecycle: Seasonal -> season hooks (start/winter).
 Module ecosystem: Ecosystem -> koppelt plants & bees pro Tag (moisture → bloom → food → bees → seeds).
 Module simulation: Simulation -> läuft mehrere Jahre
 Module I/O: Reporter -> Tabellen
 Module config: Species -> parameter holder baut PlantSpecies.
 Module Test: baut 3 Gruppen (je 10 Arten), fährt 10 runs pro Gruppe (25 Jahre), danach 1 run mit yearly summary + (year 1) daily trace.

 Arbeitsaufteilung Aufgabe 2:
 1. Moustafa Nassli: Wettermodell (cloud/rain/soil)
 2. Valentin Kreuzer: Reproduktion der Pflanzen (Ein-/Mehrjährig) und DayLength Berechnung nach Standort
 */

public class Test {

    static Species[] build(int y0) {
        Species[] g = new Species[10];
        for (int i = 0; i < g.length; i++) {
            int hS = 60 + 120 * i, hE = hS + 660;
            g[i] = new Species(y0 + i, 1.02, 1.12, 0.01, 0.99, hS, hE, 0.05, 0.001);
        }
        return g;
    }

    static List<PlantSpecies> list(Species[] defs) {
        ArrayList<PlantSpecies> L = new ArrayList<>(defs.length);
        for (Species s : defs) L.add(s.toSpecies());
        return L;
    }

    // 10 Läufe einer Gruppe mit Reports fahren.
    static void runGroup(int groupIndex, Species[] defs, int seedBase, DayLengthModel dayLength, double lat, int dayStart) {
        Reporter.printGroupParams(groupIndex, Arrays.asList(defs));
        for (int run = 1; run <= 10; run++) {
            long seed = seedBase + run;
            Reporter.printRunHeader(groupIndex, run, seed);
            Weather weather = new Weather(seed, dayLength, lat, dayStart);
            Simulation sim = new Simulation(listWithReproduction(defs, groupIndex), weather, seed, 120, 25);
            sim.run();
            Reporter.printFinalSummary(sim.bees(), sim.ecosystem().species());
        }
    }

    static Reproduction pickReproduction(int groupIndex, int speciesIndex) {
        return switch (groupIndex) {
            case 1 -> // abwechselnd
                    (speciesIndex % 2 == 0) ? new AnnualReproduction() : new PerennialReproduction();
            case 2 -> // überwiegend einjährig
                    (speciesIndex < 7) ? new AnnualReproduction() : new PerennialReproduction();
            case 3 -> (speciesIndex < 3) ? new AnnualReproduction() : new PerennialReproduction();
            default -> new PerennialReproduction();
        };
    }

    static List<PlantSpecies> listWithReproduction(Species[] defs, int groupIndex) {
        ArrayList<PlantSpecies> L = new ArrayList<>(defs.length);
        for (int i = 0; i < defs.length; i++) {
            Species s = defs[i];
            Reproduction r = pickReproduction(groupIndex, i);
            PlantSpecies ps = new PlantSpecies(s.y0, s.cMin, s.cMax, s.fMin, s.fMax, s.hStart, s.hEnd, s.q, s.p, r);
            L.add(ps);
        }
        return L;
    }

    // ein Lauf mit Jahres Trace und (für Jahr 1) Tages Trace.
    static void yearlyAndDailyTrace(Species[] defs, long seed, DayLengthModel dayLength, double lat, int dayStart) {
        Weather weather = new Weather(seed, dayLength, lat, dayStart);
        BeePopulation bees = new BeePopulation(120);
        Ecosystem eco = new Ecosystem(listWithReproduction(defs, 1));
        final int D = Simulation.seasonDays();

        System.out.println("\n TRACE (one run) ");
        Random rng = new Random(seed);
        for (int year = 1; year <= 25; year++) {
            eco.resetSeason(rng);
            weather.startSeason();

            if (year == 1) System.out.println(" DAILY (year 1) ");
            for (int day = 1; day <= D; day++) {
                DayWeather dw = weather.nextDay();
                eco.dailyUpdate(dw, bees);

                if (year == 1) {
                    double totalFood = eco.totalFoodToday();
                    List<PlantSpecies> spp = eco.species();
                    StringBuilder vigorArr = new StringBuilder("[");
                    StringBuilder bloomArr = new StringBuilder("[");
                    StringBuilder seedArr = new StringBuilder("[");

                    for (int i = 0; i < spp.size(); i++) {
                        if (i > 0) {
                            vigorArr.append(' ');
                            bloomArr.append(' ');
                            seedArr.append(' ');
                        }
                        PlantSpecies ps = spp.get(i);
                        vigorArr.append(String.format("%.2f", ps.vigor()));
                        bloomArr.append(String.format("%.3f", ps.bloomFraction()));
                        seedArr.append(String.format("%.3f", ps.seedSet()));
                    }
                    vigorArr.append(']');
                    bloomArr.append(']');
                    seedArr.append(']');

                    System.out.printf(
                            "%3d d=%.2f h=%.2f f=%.3f n=%.2f x=%.2f y=%s b=%s s=%s%n",
                            day, dw.sunHoursToday(), dw.cumSunHours(), dw.soilMoisture(),
                            totalFood, bees.population(), vigorArr, bloomArr, seedArr
                    );
                }
            }
            bees.applyWinterMortality(rng);
            eco.winterAll(rng);
            System.out.printf(" YEAR %d x=%.2f%n", year, bees.population());
            var spp = eco.species();
            for (int i = 0; i < spp.size(); i++) {
                System.out.printf("vigor[%02d]=%.2f%n", i + 1, spp.get(i).vigor());
            }
        }
    }
    // drei Gruppen laufen lassen + 1 Trace.
    public static void main(String[] args) {
        Species[] g1 = build(45), g2 = build(50), g3 = build(55);

        // Tageslänge für Wien
        DayLengthModel dayLengthT = new TriangleDayLength(240, 8, 16);
        DayLengthModel dayLengthA = new AstroDayLength();

        runGroup(1, g1, 1, dayLengthA, 48.2, 91);
        runGroup(2, g2, 1, dayLengthA, 48.2, 91);
        runGroup(3, g3, 1, dayLengthA, 48.2, 91);
        yearlyAndDailyTrace(g1, 2, dayLengthA, 48.2, 91);
    }
}
