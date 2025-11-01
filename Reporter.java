// Reporter.java

import java.util.List;

/*
  Teil vom module I/O. Bündelt console output und Formatierung
  STYLE: prozedurale utility mit static methods
*/
public final class Reporter {
    public static void printRunHeader(int groupIndex, int runIndex, long seed) {
        System.out.printf("%n GROUP %d - RUN %d - seed=%d %n", groupIndex, runIndex, seed);
    }

    // Endzusammenfassung pro Lauf.
    public static void printFinalSummary(BeePopulation bees, List<PlantSpecies> spp) {
        System.out.printf("Bees population = %.2f%n", bees.population());
        for (int i = 0; i < spp.size(); i++) {
            System.out.printf("vigor[%02d] = %.2f%n", i + 1, spp.get(i).vigor());
        }
    }

    // Parameter Tabelle einer Gruppe.
    public static void printGroupParams(int groupIndex, List<Species> defs) {
        System.out.printf("%n GROUP %d PARAMETERS %n", groupIndex);
        System.out.println("idx  y0   cMin  cMax  fMin  fMax  hStart  hEnd   q      p");
        for (int i = 0; i < defs.size(); i++) {
            Species s = defs.get(i);
            System.out.printf("%02d  %.2f  %.2f  %.2f  %.2f  %.2f  %.0f   %.0f   %.4f  %.4f%n",
                    i + 1, s.y0, s.cMin, s.cMax, s.fMin, s.fMax, s.hStart, s.hEnd, s.q, s.p);
        }
    }
}
