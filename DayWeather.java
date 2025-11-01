// DayWeather.java
/*
  Teil vom module environment. Snapshot für einen Tag:
  heutige Sonne, kumulierte Sonne, Bodenfeuchte. Immutable.
  STYLE: immutable value object; nur Getter, keine Setter.
*/
public class DayWeather {
    final double sunHoursToday; // Sonnenscheindauer d
    final double cumSunHours;   // Aufsummierte Sonnenstunden h
    final double soilMoisture;  // Bodenfeuchte f [0,1]

    // Erzeugt einen neuen Wetter-Snapshot.
    public DayWeather(double sunHoursToday, double cumSunHours, double soilMoisture) {
        this.sunHoursToday = sunHoursToday;
        this.cumSunHours = cumSunHours;
        this.soilMoisture = soilMoisture;
    }

    // Getter
    public double sunHoursToday() {
        return sunHoursToday;
    }

    public double cumSunHours() {
        return cumSunHours;
    }

    public double soilMoisture() {
        return soilMoisture;
    }
}
