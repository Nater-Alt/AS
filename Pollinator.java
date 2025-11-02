// Pollinator.java
/*
  Teil vom module population. Nominaler type für einen Bestäuber.
  STYLE: OO interface, entkoppelt Nutzer vom konkreten Populationsmodell.
*/
public interface Pollinator {
    // aktuelle Populationsgröße
    // CONTRACT: Postcondition: Wert >= 0.
    double population();
}
