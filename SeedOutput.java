public final class SeedOutput {
    public final int count;
    public final double quality;

    public SeedOutput(int count, double quality) {
        this.count = Math.max(0, count);
        this.quality = Math.max(0, Math.min(1, quality));
    }
}
