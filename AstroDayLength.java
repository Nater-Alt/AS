// AstroDayLength.java

public class AstroDayLength implements DayLengthModel {

    // Berechnung anhand der Sonnendeklinationsformel
    @Override
    public double lightHours(int dayOfYear, double latitudeDegree) {
        if (dayOfYear < 1 || dayOfYear > 366) dayOfYear = 1;
        if (Double.isNaN(latitudeDegree)) latitudeDegree = 0.0;
        double lat = Math.toRadians(Math.max(-89.0, Math.min(89.0, latitudeDegree)));

        // Sonnendeklination
        double degToRad = Math.PI / 180.0;
        double declDeg = -23.44 * Math.cos(degToRad * (360.0 / 365.0) * (dayOfYear + 10));
        double decline = declDeg * degToRad;

        // Stundenwinkel
        double x = -Math.tan(lat) * Math.tan(decline);

        if (x >= 1.0) return 0.0;   // Polarnacht
        if (x <= -1.0) return 24.0; // Polartag

        double H0 = Math.acos(x);
        // 15° pro Stunde
        double daylight = (2.0 * Math.toDegrees(H0)) / 15.0;

        if (daylight < 0) daylight = 0;
        if (daylight > 24) daylight = 24;
        return daylight;
    }
}
