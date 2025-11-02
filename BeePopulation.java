import java.util.Random;
// BeePopulation.java
/*
  Teil vom module population. Modelliert die Wildbienen-Population x (x >= 0).
  Hält nur eine Zahl als state und bietet daily growth und winter loss.
  STYLE: OO Entität.

  CONTRACT (Class invariant): population >= 0 zu jedem Zeitpunkt.
  HISTORY: population kann wachsen oder schrumpfen, fällt aber nie unter 0.
*/
public class BeePopulation implements Pollinator, Seasonal {
    private double population; // bee population

    // Konstruktor. Setzt Startpopulation, wenn negativ dann 0
    // CONTRACT: Preconditions: initialPopulation >= 0 (ansonsten clamp). Postconditions: Invariant erfüllt.
    public BeePopulation(double initialPopulation) {
        this.population = Math.max(0, initialPopulation);
    }

    // Getter für die aktuelle Population
    // CONTRACT: Postcondition: Rückgabewert >= 0.
    public double population() {
        return population;
    }

    // Tägliches Update (Vegetationsperiode)
    // Hängt nur vom Nahrungsangebot (n) ab
    // CONTRACT: Preconditions: totalFood >= 0. Postconditions: population >= 0.
    // GOOD: Nutzung von Pollinator-Interface trennt Datenhaltung vom Konsum in Ecosystem.
    public void updateDailyFromFood(double totalFood) {
        if (totalFood >= population) {
            population *= 1.03;
        } else {
            double base = population;
            population *= ((6 * totalFood / base) - 3) / 100.0 + 1.0;
        }
        if (population < 0) population = 0;
    }

    // Ruhephase: Reduziert Population auf 10%-30%
    // CONTRACT: Preconditions: random != null. Postconditions: population >= 0.
    public void applyWinterMortality(Random random) {
        double u = 0.1 + random.nextDouble() * 0.2;  // [0.1, 0.3]
        population *= u;
        if (population < 0) population = 0;
    }


    @Override
    public void startSeason() {
    }


    @Override
    public void applyWinter(Random rng) {
        applyWinterMortality(rng);
    }

}
