import java.util.Random;

// AnnualReproduction.java

// Einjährige Pflanzen - bilden Samen, reifen ab, sterben (vigor=0), Nachwuchs aus der Seedbank
public class AnnualReproduction implements Reproduction {

    private int flowers;
    private int pollinated;
    private int fruits;
    private int seeds;

    @Override
    public void updateDaily(PlantSpecies plant, DayWeather weather, Pollinator bees, double food) {
        double b = plant.bloomFraction();
        if (b <= 0) return;

        // Blütenöffnung proportional Blühanteil und vigor
        int openedToday = (int) Math.round(plant.vigor()*b*0.1);
        this.flowers += openedToday;

        // Bestäubung in Abhängigkeit zu Bienenlimit
        double pollinationEfficieny = (food <= 0) ? 0 : Math.min(1, bees.population() / food);
        int pollinatedToday = (int) Math.round(openedToday * (0.2 + 0.6*pollinationEfficieny));
        this.pollinated += pollinatedToday;

        // Fruchtbarkeit ist abhängig von Sonne
        int fill = (int) Math.round(pollinatedToday * (weather.sunHoursToday()/16));
        this.fruits += fill;

        // ein Teil wird täglich reif
        int ripen = (int) Math.round(this.fruits*0.1);
        this.seeds += ripen;
        this.fruits -= ripen;

        // Durch Reproduktion geht ein wenig vigor verloren
        plant.mulVigor(0.999);
    }

    @Override
    public void endOfSeason(PlantSpecies plant, SeedBank seeds, Random rng) {
        // Qualität aus Wetter und Bees berechnen
        double q = Math.min(1,0.5+0.5*plant.seedSet());
        seeds.add(new SeedOutput(this.seeds, q));
        plant.setVigor(0);
        this.flowers = this.pollinated = this.fruits = this.seeds = 0;

    }

    @Override
    public void startOfSeason(PlantSpecies plant, SeedBank seeds, Random rng) {
        this.flowers = this.pollinated = this.fruits = this.seeds = 0;
        int germ = seeds.germinate(rng);

        // neue Keimlinge erhöhen vigor
        plant.addVigor(germ*0.5*Math.max(0.2, seeds.getQualityAvg()));
    }

}
