import java.util.Random;

// Reproduction.java
// STYLE: OO Vertrag für Strategien.
// CONTRACT: Implementoren dürfen Vorbedingungen nicht verschärfen.
public interface Reproduction {
    // aktualisiert täglich den Bestand und den Wachstum in der Saison
    // CONTRACT: Preconditions: plant, weather, bees != null; food >= 0. Postconditions: Plant-Invarianten wahren.
    void updateDaily (PlantSpecies plant, DayWeather weather, Pollinator bees, double food);
    // Samen in die SeedBank ablegen
    // CONTRACT: Preconditions: plant, seeds, rng != null.
    void endOfSeason (PlantSpecies plant, SeedBank seeds, Random rng);
    // Keimung aus der SeedBank - vigor wächst
    // CONTRACT: Preconditions: plant, seeds, rng != null.
    void startOfSeason (PlantSpecies plant, SeedBank seeds, Random rng);
}
