// Species.java
/*
  Teil vom module config. Data holder für Art-Parameter
  macht das Bauen von PlantSpecies leichter.
  STYLE: Prozeduraler Daten Container.

  CONTRACT: Alle Felder werden im Konstruktor gesetzt und bleiben unverändert.
*/
public final class Species {
    final double y0, cMin, cMax, fMin, fMax, hStart, hEnd, q, p;

    // Parameter bündeln.
    // CONTRACT: Preconditions: Parameter sinnvoll (z.B. Intervalle). Postconditions: Felder fix.
    public Species(double y0, double cMin, double cMax, double fMin, double fMax,
                   double hStart, double hEnd, double q, double p) {
        this.y0 = y0;
        this.cMin = cMin;
        this.cMax = cMax;
        this.fMin = fMin;
        this.fMax = fMax;
        this.hStart = hStart;
        this.hEnd = hEnd;
        this.q = q;
        this.p = p;
    }

    // CONTRACT: Postcondition: Liefert neue PlantSpecies-Instanz.
    public PlantSpecies toSpecies() {
        return new PlantSpecies(y0, cMin, cMax, fMin, fMax, hStart, hEnd, q, p);
    }
}
