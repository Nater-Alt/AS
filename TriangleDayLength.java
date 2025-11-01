// TriangleDayLength.java


// berechnet die Tageslänge anhand eines Dreicksmusters
public class TriangleDayLength implements DayLengthModel {
    private final int seasonDay;
    private final double minH, maxH;

    public TriangleDayLength(int seasonDay, double minH, double maxH) {
        this.seasonDay = seasonDay;
        this.minH = minH;
        this.maxH = maxH;
    }

    // Entscheidungsbaum, je nach Standort und Tag des Jahres nach Dreiecksmuster
    @Override
    public double lightHours(int dayOfYear, double latitudeDegree) {
        int d = ((dayOfYear - 1) % seasonDay) + 1;
        int mid = seasonDay / 2;
        if (d <= mid) return minH + (maxH - minH) * (d / (double)mid);
        double t = (d - mid) / (double) mid;
        return maxH - (maxH - minH) * t;
    }
}
