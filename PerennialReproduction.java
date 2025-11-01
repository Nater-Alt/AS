import java.util.Random;

// PerennialReproduction.java

// Mehrjährige Pflanzen - weniger Sameproduktion, speichert Samen
public class PerennialReproduction implements Reproduction {

    private int flowers;
    private int pollinated;
    private int fruits;
    private int seeds;

    @Override
    public void updateDaily(PlantSpecies plant, DayWeather weather, Pollinator bees, double food) {
        double b = plant.bloomFraction();
        if (b <= 0) return;

        // Blütenöffnung proportional Blühanteil und vigor
        int openedToday = (int) Math.round(plant.vigor()*b*0.05);
        this.flowers += openedToday;

        // Bestäubung in Abhängigkeit zu Bienenlimit
        double pollinationEfficieny = (food <= 0) ? 0 : Math.min(1, bees.population() / food);
        int pollinatedToday = (int) Math.round(openedToday * (0.3+0.5*pollinationEfficieny));
        this.pollinated += pollinatedToday;

        // Fruchtbarkeit ist abhängig von Sonne
        int fill = (int) Math.round(pollinatedToday * (0.6*weather.sunHoursToday()/16));
        this.fruits += fill;

        // ein Teil wird täglich reif
        int ripen = (int) Math.round(this.fruits*0.08);
        this.seeds += ripen;
        this.fruits -= ripen;

        // Durch Reproduktion geht ein wenig vigor verloren
        plant.mulVigor(0.9995);
    }

    @Override
    public void endOfSeason(PlantSpecies plant, SeedBank seeds, Random rng) {
        // Qualität aus Wetter und Bees berechnen
        double q = Math.min(1,0.6+0.4*plant.seedSet());
        seeds.add(new SeedOutput(this.seeds, q));
        this.flowers = this.pollinated = this.fruits = this.seeds = 0;

    }

    @Override
    public void startOfSeason(PlantSpecies plant, SeedBank seeds, Random rng) {
        this.flowers = this.pollinated = this.fruits = this.seeds = 0;
        // geringe Keimung
        int germ = (int) Math.round(seeds.germinate(rng) * 0.3);
        plant.addVigor(germ*0.2*Math.max(0.2, seeds.getQualityAvg()));
    }

}
