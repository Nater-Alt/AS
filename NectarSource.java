// NectarSource.java
/*
  Teil vom module species
  STYLE: OO interface für Polymorphie.
*/
public interface NectarSource {
    // Anteil in Blüte [0,1].
    // CONTRACT: Postcondition: Rückgabe in [0,1].
    double bloomFraction();

    // Nektarmenge heute >= 0.
    // CONTRACT: Postcondition: Rückgabe >= 0.
    double nectarToday();
}
