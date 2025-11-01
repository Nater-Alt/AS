// Species.java
/*
  Teil vom module config. data holder für Art-Parameter
  macht das Bauen von PlantSpecies leichter.
  STYLE: Prozeduraler Daten Container
*/
public final class Species {
    final double y0, cMin, cMax, fMin, fMax, hStart, hEnd, q, p;

    // Parameter bündeln.
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

    public PlantSpecies toSpecies() {
        return new PlantSpecies(y0, cMin, cMax, fMin, fMax, hStart, hEnd, q, p);
    }
}
