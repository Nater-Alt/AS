import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * STYLE: functional Analysepipeline.
 * Dieser Block arbeitet referentiell transparent, indem er Species-Definitionen in
 * unveränderliche Snapshots transformiert und ausschließlich Streams/Higher-Order-Funktionen
 * nutzt. Kein globaler Zustand wird geschrieben; alle Ergebnisse sind neue Objekte oder Strings.
 *
 * CONTRACT: Alle öffentlichen Methoden erzeugen nur neue Datenstrukturen und verändern keine
 * Eingaben. Für gleiche Eingaben werden identische Ergebnisse erzeugt.
 */
public final class FunctionalAnalysis {

    private FunctionalAnalysis() {
    }

    /**
     * RECORD: Unveränderlicher Parametersnapshot einer Species.
     * HISTORY: Alle Felder sind final und werden nie verändert.
     */
    private record SpeciesParam(int index,
                                double startVigor,
                                double humiditySpan,
                                double bloomWindow,
                                double pollinationProbability,
                                double bloomIntensity) {
    }

    /**
     * RECORD: Zusammengefasste Statistik für eine Gruppe.
     */
    private record GroupSummary(String label,
                                 long count,
                                 double avgVigor,
                                 double avgHumiditySpan,
                                 double minBloomWindow,
                                 double maxBloomWindow) {
    }

    /**
     * CONTRACT: Preconditions: defs != null. Postconditions: gibt Liste funktional berechneter Snapshots zurück.
     */
    private static List<SpeciesParam> toParams(Species[] defs) {
        return IntStream.range(0, defs.length)
                .mapToObj(i -> {
                    Species s = defs[i];
                    double humiditySpan = Math.abs(s.fMax - s.fMin);
                    double bloomWindow = Math.abs(s.hEnd - s.hStart);
                    return new SpeciesParam(i + 1, s.y0, humiditySpan, bloomWindow, s.p, s.q);
                })
                .collect(Collectors.toList());
    }

    /**
     * CONTRACT: Preconditions: params != null. classifier liefert label.
     * Postconditions: Ergebnissammlung ist immutable (List.of via Streams).
     */
    private static List<GroupSummary> groupByClassifier(List<SpeciesParam> params,
                                                        Function<SpeciesParam, String> classifier) {
        Map<String, List<SpeciesParam>> groups = params.stream()
                .collect(Collectors.groupingBy(classifier, TreeMap::new, Collectors.toList()));
        return groups.entrySet().stream()
                .map(entry -> summarize(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(GroupSummary::label))
                .collect(Collectors.toList());
    }

    /**
     * CONTRACT: Preconditions: members != null. Postconditions: erzeugt neuen Summary ohne Seiteneffekte.
     */
    private static GroupSummary summarize(String label, List<SpeciesParam> members) {
        DoubleSummaryStatistics vigorStats = members.stream()
                .collect(Collectors.summarizingDouble(SpeciesParam::startVigor));
        DoubleSummaryStatistics humidityStats = members.stream()
                .collect(Collectors.summarizingDouble(SpeciesParam::humiditySpan));
        DoubleSummaryStatistics bloomWindowStats = members.stream()
                .collect(Collectors.summarizingDouble(SpeciesParam::bloomWindow));
        return new GroupSummary(
                label,
                vigorStats.getCount(),
                vigorStats.getAverage(),
                humidityStats.getAverage(),
                bloomWindowStats.getMin(),
                bloomWindowStats.getMax()
        );
    }

    /**
     * CONTRACT: Preconditions: stats != null. Postconditions: erzeugt formatierte Darstellung.
     */
    private static String formatGroupSummary(GroupSummary stats) {
        return String.format("%-18s | %2d | avgVigor=%6.2f | avgHumSpan=%5.2f | bloomWindow=[%6.1f,%6.1f]",
                stats.label(), stats.count(), stats.avgVigor(), stats.avgHumiditySpan(),
                stats.minBloomWindow(), stats.maxBloomWindow());
    }

    /**
     * STYLE: funktional – Aufbau eines Analyse-Pipelines mit Higher-Order-Function classifier.
     * GOOD: Nutzung von TreeMap sichert reproduzierbare Reihenfolge ohne zusätzliche Sortierlogik.
     */
    public static void runFunctionalReport(String title, Species[] defs) {
        List<SpeciesParam> params = toParams(defs);

        List<GroupSummary> humidityGroups = groupByClassifier(params, p -> {
            double span = p.humiditySpan();
            if (span < 0.2) return "Humidity narrow";
            if (span < 0.4) return "Humidity balanced";
            return "Humidity wide";
        });

        List<GroupSummary> pollinationGroups = groupByClassifier(params, p -> {
            double poll = p.pollinationProbability();
            if (poll < 0.002) return "Pollination low";
            if (poll < 0.01) return "Pollination medium";
            return "Pollination high";
        });

        List<GroupSummary> bloomGroups = groupByClassifier(params, p -> {
            double intensity = p.bloomIntensity();
            if (intensity < 0.02) return "Bloom slow";
            if (intensity < 0.05) return "Bloom medium";
            return "Bloom fast";
        });

        System.out.println("\nFUNCTIONAL REPORT - " + title);
        humidityGroups.stream().map(FunctionalAnalysis::formatGroupSummary).forEach(System.out::println);
        pollinationGroups.stream().map(FunctionalAnalysis::formatGroupSummary).forEach(System.out::println);
        bloomGroups.stream().map(FunctionalAnalysis::formatGroupSummary).forEach(System.out::println);
    }
}
