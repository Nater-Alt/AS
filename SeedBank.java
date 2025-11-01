import java.util.Random;

// SeedBank.java

public final class SeedBank {
    private int stored;
    private double qualityAvg;

    // Füge einen Output zu der bestehenden Seed Bank hinzu
    public void add(SeedOutput output) {
        if(output.count <= 0) return;

        double total = stored + output.count;
        qualityAvg = (stored*qualityAvg + output.count*output.quality) / total;
        this.stored = (int) total;
    }

    // Berechnet die Keimung der Seeds in der SeedBank
    public int germinate(Random rng) {
        if (stored == 0) return 0;

        // 30-80% können überwintern und 10-30% des Ergebnisses keimen
        int afterWinter = (int) Math.round(stored * (0.5 + 0.3*rng.nextDouble()));
        int germinating = (int) Math.round(afterWinter * (0.1 + 0.2*rng.nextDouble()));

        stored = afterWinter - germinating;
        return Math.max(0, germinating);
    }

    public double getQualityAvg() {
        return qualityAvg;
    }

    public int getStored() {
        return stored;
    }


}
