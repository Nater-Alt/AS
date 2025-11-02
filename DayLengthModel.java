// DayLengthModel.java

// Berechnet die Sonnenstunden für jeden Tag des Jahres abhängig vom Standort
// CONTRACT: Implementoren liefern Werte ≥ 0 und ≤ 24.
public interface DayLengthModel {
    double lightHours(int dayOfYear, double latitudeDegree);
}
