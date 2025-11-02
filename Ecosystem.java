// Ecosystem.java

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
  Teil vom module ecosystem. Hält alle Pflanzenarten und steuert den day flow:
  moisture → bloom → food → bees → seeds.
  STYLE: prozeduraler Tagesablauf. Ruft Methoden auf OO-Klassen auf.

  CONTRACT: species-Liste bleibt unveränderlich in ihrer Referenz; Einträge != null.
  HISTORY: Reihenfolge der Updates bleibt fix (Stress -> Bloom -> Food -> Bees -> Seeds).
*/
public class Ecosystem {
    private final List<PlantSpecies> species;

    // Konstruktor. Übernimmt die Pflanzenliste
    // CONTRACT: Preconditions: species != null, enthält keine nulls. Postconditions: interne Liste kopiert.
    public Ecosystem(List<PlantSpecies> species) {
        this.species = new ArrayList<>(species);
    }

    // Setzt alle Pflanzenarten auf Saisonstart
    // CONTRACT: Postcondition: Jede Species startet Saison mit shared RNG.
    public void resetSeason() {
        resetSeason(new Random());
    }

    public void resetSeason(Random rng) {
        // BAD: Alle Arten teilen denselben RNG und damit Korrelationen; individuelle RNGs wären entkoppelter.
        for (PlantSpecies s : species) {
            s.setSeasonRng(rng);
            s.startSeason();
        }
    }

    // tägliches Nahrungsangebot (n) als Summe von (yi * bi) aller species
    // CONTRACT: Postcondition: Summe >= 0. Keine Seiteneffekte.
    // GOOD: Reiner Aggregationsschritt über lokale Variable → klar referentiell.
    public double totalFoodToday() {
        double sum = 0;
        for (PlantSpecies s : species) sum += s.foodSupplyToday();
        return sum;
    }

    // 1 Tag: Moisture-Stress -> Blüte -> heutiges Nahrungsangfebot -> Bienen -> Samen.
    // STYLE: prozedurale Steuerung, geringe Kopplung durch Methoden der OO-Objekte.
    // CONTRACT: Preconditions: weather, bees != null. Postconditions: Alle Species bleiben invariant.
    // GOOD: Klarer Sequenzfluss, der Effekte des Wetters explizit in Phasen strukturiert.
    public void dailyUpdate(DayWeather weather, BeePopulation bees) {
        for (PlantSpecies s : species) s.applyMoistureStress(weather.soilMoisture());
        for (PlantSpecies s : species) s.advanceBloom(weather.sunHoursToday(), weather.cumSunHours());
        double totalFood = totalFoodToday();
        bees.updateDailyFromFood(totalFood);
        for (PlantSpecies s : species) {
            s.updateSeedSet(bees.population(), totalFood, weather.sunHoursToday());
            s.reproduceDaily(weather, bees, totalFood);
        }
    }

    // Winterreproduktion für alle Species.
    // CONTRACT: Preconditions: rng != null. Postconditions: Jede Species hat winterReproduce ausgeführt.
    // BAD: Kopplung an java.util.Random erschwert deterministische Tests; abstrahierter Zufallsprovider wäre besser.
    public void winterAll(java.util.Random rng) {
        for (PlantSpecies s : species) {
            s.applyWinter(rng);
        }
    }

    // Getter für die Liste der Pflanzenarten
    // CONTRACT: Postcondition: Rückgabe ist mutable Liste; Clients müssen defensiv sein.
    // BAD: Gibt interne Liste zurück -> Leck des Zustands; Collections.unmodifiableList wäre robuster.
    public List<PlantSpecies> species() {
        return species;
    }
}
