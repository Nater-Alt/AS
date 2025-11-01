import java.util.Random;

// Reproduction.java

public interface Reproduction {
    // aktualisiert täglich den Bestand und den Wachstum in der Saison
    void updateDaily (PlantSpecies plant, DayWeather weather, Pollinator bees, double food);
    // Samen in die SeedBank ablegen
    void endOfSeason (PlantSpecies plant, SeedBank seeds, Random rng);
    // Keimung aus der SeedBank - vigor wächst
    void startOfSeason (PlantSpecies plant, SeedBank seeds, Random rng);
}
