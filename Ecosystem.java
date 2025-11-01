// Ecosystem.java

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
  Teil vom module ecosystem. Hält alle Pflanzenarten und steuert den day flow:
  moisture → bloom → food → bees → seeds.
  STYLE: prozeduraler Tagesablauf. ruft Methoden auf OO-Klassen auf.
*/
public class Ecosystem {
    private final List<PlantSpecies> species;

    // Konstruktor. Übernimmt die Pflanzenliste
    public Ecosystem(List<PlantSpecies> species) {
        this.species = new ArrayList<>(species);
    }

    // Setzt alle Pflanzenarten auf Saisonstart
    public void resetSeason() {
        resetSeason(new Random());
    }

    public void resetSeason(Random rng) {
        for (PlantSpecies s : species) {
            s.setSeasonRng(rng);
            s.startSeason();
        }
    }

    // tägliches Nahrungsangebot (n) als Summe von (yi * bi) aller species
    public double totalFoodToday() {
        double sum = 0;
        for (PlantSpecies s : species) sum += s.foodSupplyToday();
        return sum;
    }

    // 1 Tag: Moisture-Stress -> Blüte -> heutiges Nahrungsangfebot -> Bienen -> Samen.
    // STYLE: prozedurale Steuerung, geringe Kopplung durch Methoden der OO-Objekte.
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
    public void winterAll(java.util.Random rng) {
        for (PlantSpecies s : species) {
            s.applyWinter(rng);
        }
    }

    // Getter für die Liste der Pflanzenarten
    public List<PlantSpecies> species() {
        return species;
    }
}
