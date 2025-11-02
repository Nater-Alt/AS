// SeedOutput.java
// STYLE: immutable value object.
// CONTRACT: count >= 0, quality ∈ [0,1] nach Konstruktion.
public final class SeedOutput {
    public final int count;
    public final double quality;

    // CONTRACT: Preconditions: count >= 0, 0 ≤ quality ≤ 1 (wird notfalls geclamped). Postconditions: Felder final.
    public SeedOutput(int count, double quality) {
        this.count = Math.max(0, count);
        this.quality = Math.max(0, Math.min(1, quality));
    }
}
