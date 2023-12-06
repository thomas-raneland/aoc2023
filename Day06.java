import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day06 {
    private static final String INPUT = """
            Time:        60     94     78     82
            Distance:   475   2138   1015   1650""";

    public static void main(String... args) {
        long[] times = parseLine(0).mapToLong(Long::parseLong).toArray();
        long[] distances = parseLine(1).mapToLong(Long::parseLong).toArray();
        long partI = IntStream.range(0, times.length)
                              .mapToLong(i -> waysToWin(times[i], distances[i]))
                              .reduce(1, (a, b) -> a * b);
        System.out.println(partI);

        long time = Long.parseLong(parseLine(0).collect(Collectors.joining()));
        long distance = Long.parseLong(parseLine(1).collect(Collectors.joining()));
        long partII = waysToWin(time, distance);
        System.out.println(partII);
    }

    private static Stream<String> parseLine(int line) {
        return Stream.of(INPUT.lines().toList().get(line).split("\\s+")).skip(1);
    }

    private static long waysToWin(long time, long recordDistance) {
        long count = 0;

        for (long load = 0; load <= time; load++) {
            if ((time - load) * load > recordDistance) {
                count++;
            }
        }

        return count;
    }
}
