// Seasonal.java
/*
  Teil vom module lifecycle. Dinge mit Jahres-Zyklus.
  STYLE: OO interface, wird vom yearly control flow benutzt.
*/
public interface Seasonal {
    // CONTRACT: Postcondition: Objekt kehrt in saisonalen Ausgangszustand zurück.
    void startSeason(); // Saisonstart (reset von Saisonzuständen)
    // CONTRACT: Preconditions: rng != null. Postconditions: Jahresinvarianten gewahrt.
    void applyWinter(java.util.Random rng); // Wintereffekte mit RNG
}
