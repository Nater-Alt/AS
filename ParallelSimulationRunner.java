import java.util.List;

/**
 * STYLE: parallel Analyseblock. Beginner-freundliche Variante mit ganz normalen Threads
 * (ohne ExecutorService). Ziel: mehrere Simulationen gleichzeitig laufen lassen und danach
 * gemeinsam auswerten.
 *
 * CONTRACT: Öffentliche Methoden starten höchstens vier Worker-Threads, jeder Worker verwendet
 * eine eigene Simulation und gibt nach Abschluss ein Ergebnis zurück. Keine gemeinsamen mutable
 * Objekte zwischen den Threads → keine Datenrennen.
 */
public final class ParallelSimulationRunner {

    private ParallelSimulationRunner() {
    }

    /**
     * NOTE: Kleine Hilfsklasse statt moderner "record", damit der Aufbau nachvollziehbar bleibt.
     * Jeder Datensatz fasst zusammen, was eine Simulation geliefert hat.
     */
    private static final class ScenarioResult {
        final int scenarioId;
        final double finalBeePopulation;
        final double avgVigor;
        final double seedSetAvg;

        ScenarioResult(int scenarioId, double finalBeePopulation, double avgVigor, double seedSetAvg) {
            this.scenarioId = scenarioId;
            this.finalBeePopulation = finalBeePopulation;
            this.avgVigor = avgVigor;
            this.seedSetAvg = seedSetAvg;
        }
    }

    /**
     * STYLE: parallel – einfache Thread-Steuerung mit start() und join().
     * CONTRACT: Preconditions: defs != null. Postconditions: startet vier Threads, wartet auf alle,
     * anschließend werden die Ergebnisse sauber ausgegeben.
     */
    public static void runParallelScenarios(String title,
                                            Species[] defs,
                                            DayLengthModel dayLength,
                                            double latitude,
                                            int dayStart) {
        System.out.println("\nPARALLEL RUNNER - " + title);

        ScenarioWorker[] workers = new ScenarioWorker[4];

        for (int i = 0; i < workers.length; i++) {
            int scenarioId = i + 1;
            long seed = 1000L + (long) i * 37L;
            workers[i] = new ScenarioWorker(scenarioId, defs, dayLength, latitude, dayStart, seed);
            workers[i].start();
        }

        for (ScenarioWorker worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                System.out.println("Parallel runner interrupted – stopping early.");
                return;
            }
        }

        for (ScenarioWorker worker : workers) {
            ScenarioResult result = worker.result();
            if (result != null) {
                System.out.printf("Scenario %d | Bees=%6.2f | AvgVigor=%6.2f | SeedSet=%4.3f%n",
                        result.scenarioId,
                        result.finalBeePopulation,
                        result.avgVigor,
                        result.seedSetAvg);
            } else {
                System.out.printf("Scenario %d did not finish because of an error.%n", worker.scenarioId);
            }
        }
    }

    /**
     * STYLE: nebenläufiger Worker – jede Instanz startet genau eine Simulation.
     * GOOD: Jeder Thread baut seine eigenen Objekte → keine geteilten Zustände.
     * BAD: Wiederholt ein wenig Code (Seed-Berechnung), aber dadurch gut nachvollziehbar.
     */
    private static final class ScenarioWorker extends Thread {
        private final int scenarioId;
        private final Species[] defs;
        private final DayLengthModel dayLength;
        private final double latitude;
        private final int dayStart;
        private final long seed;
        private ScenarioResult result;

        ScenarioWorker(int scenarioId,
                       Species[] defs,
                       DayLengthModel dayLength,
                       double latitude,
                       int dayStart,
                       long seed) {
            super("Scenario-" + scenarioId);
            this.scenarioId = scenarioId;
            this.defs = defs;
            this.dayLength = dayLength;
            this.latitude = latitude;
            this.dayStart = dayStart;
            this.seed = seed;
        }

        @Override
        public void run() {
            try {
                result = simulateScenario(scenarioId, defs, dayLength, latitude, dayStart, seed);
            } catch (RuntimeException ex) {
                System.out.println("Scenario " + scenarioId + " failed: " + ex.getMessage());
                result = null;
            }
        }

        ScenarioResult result() {
            return result;
        }
    }

    /**
     * CONTRACT: Preconditions: defs != null. Postconditions: jede Simulation erzeugt eigene Objekte,
     * es wird kein Zustand zwischen den Szenarien geteilt.
     */
    private static ScenarioResult simulateScenario(int scenarioId,
                                                   Species[] defs,
                                                   DayLengthModel dayLength,
                                                   double latitude,
                                                   int dayStart,
                                                   long seed) {
        Weather weather = new Weather(seed, dayLength, latitude, dayStart);
        List<PlantSpecies> species = Test.listWithReproduction(defs, scenarioId);
        Simulation simulation = new Simulation(species, weather, seed + 13L, 60, 5);
        simulation.run();
        double bees = simulation.bees().population();
        double avgVigor = simulation.ecosystem().species().stream()
                .mapToDouble(PlantSpecies::vigor)
                .average()
                .orElse(0);
        double seedAvg = simulation.ecosystem().species().stream()
                .mapToDouble(PlantSpecies::seedSet)
                .average()
                .orElse(0);
        return new ScenarioResult(scenarioId, bees, avgVigor, seedAvg);
    }
}
