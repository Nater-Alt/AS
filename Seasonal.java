// Seasonal.java
/*
  Teil vom module lifecycle. Dinge mit Jahres-Zyklus.
  STYLE: OO interface, wird vom yearly control flow benutzt.
*/
public interface Seasonal {
    void startSeason(); // Saisonstart (reset von Saisonzuständen)
    void applyWinter(java.util.Random rng); // Wintereffekte mit RNG
}
