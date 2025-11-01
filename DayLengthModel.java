// DayLengthModel.java

// Berechnet die Sonnenstunden für jeden Tag des Jahres abhängig vom Standort
public interface DayLengthModel {
    double lightHours(int dayOfYear, double latitudeDegree);
}
