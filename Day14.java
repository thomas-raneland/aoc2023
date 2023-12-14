import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Day14 {
    public static void main(String... args) {
        for (var input : List.of(TEST_INPUT, INPUT)) {
            partI(input);
            partII(input);
        }
    }

    private static void partI(String state) {
        System.out.println("Part I: " + measure(tilt(state)));
    }

    private static void partII(String state) {
        long totalSpinCycles = 1_000_000_000;
        var stateToCycle = new HashMap<String, Long>();

        for (long spinCycle = 1; spinCycle <= totalSpinCycles; spinCycle++) {
            state = spin(state);
            Long cycleStart = stateToCycle.putIfAbsent(state, spinCycle);

            if (cycleStart != null) {
                long cycleLength = spinCycle - cycleStart;

                while (spinCycle < totalSpinCycles - cycleLength) {
                    spinCycle += cycleLength;
                }

                long spinCyclesLeft = totalSpinCycles - spinCycle;

                state = stateToCycle.entrySet()
                                    .stream()
                                    .filter(entry -> entry.getValue() == cycleStart + spinCyclesLeft)
                                    .map(Entry::getKey)
                                    .findFirst()
                                    .orElseThrow();

                break;
            }
        }

        System.out.println("Part II: " + measure(state));
    }

    static String spin(String state) {
        for (int i = 0; i < 4; i++) {
            state = rotate(tilt(state));
        }

        return state;
    }

    static String rotate(String input) {
        var rotated = new ArrayList<StringBuilder>();
        var lines = input.lines().toList();

        for (int x = 0; x < lines.get(0).length(); x++) {
            StringBuilder sb = new StringBuilder();

            for (String line : lines.reversed()) {
                sb.append(line.charAt(x));
            }

            rotated.add(sb);
        }

        return String.join("\n", rotated);
    }

    static String tilt(String state) {
        List<String> lines = state.lines().collect(Collectors.toList());

        for (int y = 0; y < lines.size(); y++) {
            for (int x = 0; x < lines.get(y).length(); x++) {
                if (lines.get(y).charAt(x) == '.') {
                    for (int y2 = y + 1; y2 < lines.size(); y2++) {
                        if (lines.get(y2).charAt(x) == 'O') {
                            lines.set(y, with(lines.get(y), x, 'O'));
                            lines.set(y2, with(lines.get(y2), x, '.'));
                            break;
                        } else if (lines.get(y2).charAt(x) == '#') {
                            break;
                        }
                    }
                }
            }
        }

        return String.join("\n", lines);
    }

    static String with(String s, int ix, char v) {
        var sb = new StringBuilder(s);
        sb.setCharAt(ix, v);
        return sb.toString();
    }

    static long measure(String state) {
        var lineTotals = state.lines().map(line -> line.chars().filter(c -> c == 'O').count()).toList();
        long total = 0;

        for (int lineNbr = 1; lineNbr <= lineTotals.size(); lineNbr++) {
            total += lineNbr * lineTotals.get(lineTotals.size() - lineNbr);
        }

        return total;
    }

    static final String TEST_INPUT = """
            O....#....
            O.OO#....#
            .....##...
            OO.#O....O
            .O.....O#.
            O.#..O.#.#
            ..O..#O..O
            .......O..
            #....###..
            #OO..#....""";

    static final String INPUT = """
            ..#.#O##....O.O#O#O...O.....O........OO...#OO.O..O...O..O...O.....O....#O#..O#.......O.##..O....#..#
            .O#O.#O.O#OO..#..O....OOO#....#.O#O..OO#..O...OOO.#.O..#..O#OO.#O..O#...##.#O#..OO#O#O.....#O.O.OO.O
            ...O..O.....#..#.O...O##.O...O...OO..#O.OO..###.OO.....##O.#.#.O.#.......O.....O...O...#.....O.#.#O.
            .#..O.....#..O..OO.O#.....#OOO...OO..#....OO#..O.......................O..#.#O....#.OO.#..O.......#.
            ......O.##O......##.#.#..O.##............#.O.O.O..O#O.#.O....#....#....#OOO....O#O.O..#..#....O#..O#
            ...O#..O...O.O#.O#O...#..O.O.....O..#..#O.#......##..OO.#....#.....###..#...#.O.#.OOO.O.....#O...#O.
            .#O##.#O.O.##O.##.#O....#..O..#......OOO..OO..#.#.#..#O...#O.#.#.O#O...#.#O..#.#....O........OO.....
            ##......O##.#.......O.#..#OO....O..OO.O.O..O#.....OO..O.O....#.O................#O...#O.#...O.O....O
            ...OO.O....##.##...##O...O..O..O#...O.#....#..O...O..#...O....#...O#OO..#.O.O.#.#O#O.O.......#.#O.#.
            ..O...#......#.....#.#.....#...........O..O..##..#.....#.O#.......O.##.....O.OO..#..O..#..........#.
            .#.O...O#O#...OOO............OOO...#O#...#.....#OO.#.O..#OO.##.OOO..#O...#O#...O.O..##.#..O.O#O...O.
            ....O...O#OO..O..O.#.....###....O#..#OO...##.#O.#.#.O.O...OO.O....#..O..O..OOO.#......O...##OOO.O...
            .OOO.....#.O..#..O.O..O..#O..#O#O.#..#.O.........O......#.#......O..#O.........OO#O..OOO......O#.O..
            O.O.#O.....O.O..##......O#....O#.O..##.....O.#..###O.O...#O.....##..#O..#O.O.#.O.OO#....O...OO#....O
            .O#O..#...O.#...O......#.....O......OO...O.#...#..#.O.#..O.#...#.......#.#....O#..OO..O#O...O..O..#.
            .#..#...#.OOOO..#.#.O...O##O##....O..##..OO..#OO#..##.OO#.....#............O..#O....OO.#..#......#O.
            .....O......O..........O#...O...#.O#O...O.#...O.......O#..OOOO......O..O..#.#....O....#.O..O.###.#.O
            .#OO.O#..O.O#...OO...##OO.O....O.O.##O.......O.#O...OO#O#...#.OO.#.O.O..OO.O.O.#...OOO..#...O.O#.#OO
            OO.#.##O..O#.O.#..O#O..#.O#.....O#..O......OOOOO#..O#.#......O.O..OO....##.##O.OO#O#.#.#......O.....
            .O.....O.O.#....OO.O..#..O##.#.....#.#......#..O...O.#..#..O#.O..O..##....##.O..#O....#...O#..O.#...
            ....OO#OO..#..O..O..O#.##O.OO.O.OO.##...#..#..O........#.O......O.#OO#...........#...O#O##.......O..
            #.#O........O...##..........#.###.OO#O.O#.....O#.....#.OO.#..OO..#..O...O...O..OO.OO..#OO..O..##.OO.
            ..#...O.....O.OO#OO##....#O#..#O...O#.....#O.O.O.......#...O..O.O#.O.....#O..OO#...O...O.OO.O.O....#
            OO.#O..O..OOO.....O.....OOO.#........O........OO..##OOO...#.OOO..OO....#..#..........O...#..#.O#.#O.
            #.#O##.O#.#O#.#.OO...O.#O.......O...#O......#.#..O##......#.....O.....O##..#...#.....#..O...O......O
            O.......#...O...O.#O#.O...#..O..#.OO...OO.O.#.#........#OO.#..OOO#...#O#O......O.....##.O.#..O..#.#.
            .#O....O...#OO..O........OO.#O...#...#O#O.O#.#...O....O.....##.#.#..#.#....##.#....#........#O...#.#
            .......O........#.##.........#.O#.O.....OO..O..#.OO.##.O....#..O..OO.#O#.#....O......O..O#OO........
            ..#.O...#.....OO.O.......#...O...O##O..OO.....O.O.#.#....#....O....#O....#..#O.OO..O.#O......O...O..
            .......O#.O.#O....OO##.......#.....##..O.OOO...O...OO#OO#.O#....#.#.O..#O.....OO..#..OOO....O#...O#O
            ......#.O##....O..O...O#...O#.O....O.#O#O#.O..#.O......O#......O.#O.#..#.O.......OO.#.#.O#.OO......#
            ..#.OOOO.#...#O#......#...#OOO.....O..#...O.......#.#O.O...#.O#..O..O....##...........O....O#.O#.O..
            ...#.#...####.#O.......O.#O....OO..O.#....OOO.....#.O..OO.#O#.O.O##O#...OO....O.O..#.OO..........##.
            O.O#..........O#.........OO.....#.O....OOO.O...#.....#O....#..O.#....O#OOOO.#...#.#O.O#.OOO..OO#.O..
            O......O.#....O#O..OOO##..........#OO.........#..O..O.....O..#O...##.OO.O.O........O.O...OOO......O.
            #.....O........#OO.###O..O....#OO.##O#OO.....OO.#O.......#.......#..O..O.........#...O....#.#...O.O.
            .#O....O##.O#....O.#.#..O#.#..........#........#..#O.#O...O.OOO.OO..##.#....OO.O..O#..O.#.#.......O.
            ...O.O....O##OO#..#..#.....O..#..OO..##.O.#.............O.......#.O.........OOO#.#...OO##...#.#.....
            OO.O..O......O...#.O#..##O...O.....#.##.....O##........#.OO#.O..O.....O#.......##.O.OO.O##.O........
            .O#..OO.#O...O...#..O.O.O..##........OO.O.....O.O#.#..OO.O...O.OO..O.O#OO..O..O.......O......OOOO...
            ..O....#..O..OO.#.##O......#.#.........OO.....O....#..O.OO...OO.#O.#O....#........O....#O#...O#O.#.#
            ....O...O.OOO.O...OOO....O.#....##.OO....O.O.##............O..#..OO....O....#......#....OO...O#OO..O
            .O.OO.....##.OOOO.#O#.......OOOO.O..#.....#O#O.........##OO.#.....O.O.#..##.O#..#O....#..O#...O..#..
            #....##O#.O#......O...OO.....O..O....##.O##.....O##O..OO..##O.###......#....#.#O....###.......#.....
            ...##..O.O.#...OOO...OO#.O....OOOO....O....#O..###.#....O..OO....#.....O#O#.O.....O.....O..#..#O..##
            ...OO.O...O.O..O..#.#OO..O#..OOO....#.O..#O#.###..O##..#.#..#....O....OOOO....#O.#.....O#O.....##...
            .........#...O..O#.O#..#..OO..O..#...#......#..OO..O..O.O..OO.O.....O.................O...O#.#O.....
            #.##...O.......#.#O#.....##O#.#..O.O.......#..O......O..O#.OOO#..O..OO#...O.O..O.#O.#.....##..OOOO.#
            .O...O...###..OOOO.#.##.O.#..#O#.O.#O.#.O#O#......#.O.#OO.##.....O...O#.O##.#..#...OO..O...O..##...O
            ..##......O.#OO......#.O...#.......O##.........#.O.#....O...O....#O...O#O#.OO#.O...OO..O#....O.OO..O
            .........#...#.O..#.#........##.O.....O.#...##.#.#.O##...##..OO...#.........#..O.....O...O....#.O...
            .O.OO.#..O.OO..OO..O........O#.#O..O..O...#O.##.......#..#.....#O#..O....O.OOOOO#...O..#......O.....
            ...O..O....#O...O......#.OOO.#.#......O##O#..##O..OO#O.....O..#.#...O.O##O.OO.OO...........O..###...
            ....#.O..#..O#O.....#.OO.....##O....O#....#...O..OO.O...##..O.#O...OO.#..OO.#O.O.O..OOOO......O..O#.
            O.#.....#...O#O..O##....#......#.O.O.....O#....#........##.O...O...O..#.....#O....O..O....#.#...#...
            ....O...O...O.OO.O##......#.......O.#....#.....#O.O...O#O.####.##..........#.#OO...........#.O.O#...
            #.......#...O#.O..#O...#..#.#..O..###.OO.#..O.#..#..O#....###O...O..#..#......#O#.OO#.....#O#.......
            .....#..#..O#.#.#O.....O..O...O.O#....OO......#O#..#O....#.#O#....O##.O.....#O...........O...O.O.#..
            .#OO##.......O....OO.OO.....O.O.....OO.........#.#O.O.O.O.OO......#O...#O#.....OO.OO.O.O.#..#..#..#.
            .O...OOO#O..O.##.....#.#O#O.O.#O.....#.OO#.O...#O....OO..#...OO#.O.....O...O....O..O#...##.OO##..O.O
            #.#.#...O........O##.#....#.....#..........O.#...O.....##O....#...OO..##......#...OO.O#OO......O.O..
            ......#...O..##...O........O#.......#.......OO.##..#...#..#.....#...O#.#O........O#.O...............
            ...O..O....OOO...#.......O..OOO##.O.O#....#O..O.#O#...O.#.O..O.O#O..#...O..O.O.OO.O.#..#O..#O#..#..#
            O.O..O#..O...O...O.O....##..OO#..O#.#OO##...OO..#OO....#..#..O.........O..O....#.#...OO#.#..O....O#.
            .#.##.#..O.O#....#....#O..#O.O....O...#.#.OO..O.OO..#.#O..OOOO#..O..##O.#..O##O..O......#.O..O..O..O
            O#OO..O.....O...O...#...#...#.....O...#.O...#.#O...O..O.###......#O.........#O..........O..OOO..O.O.
            ..#.#.O.....O....#....O..#.OO......O#.....O....O#.#.#...#O.O#OO..........#..#.O....O........O..O..O#
            ..#........##O#O.#....#O.O....OOO..#.O.....#OO.O...#....O#...##O#.O...O.#OO#.O.OO#..OO.....OO..#O...
            ...OO.#O#.#O.......OO#.O..O..OO...O..##O.#O.......#...#.O..O##O...O..O#.#....OO.###...#..O#...O..O..
            .O#O#....#.....###..#.#..#...#O...#..O#.....##.OO.....OO...O....#O.O........##...O.....OO#O.....#...
            #.....OOO..#....O.O..........#.......O...#....O.O...O........O...#O##.O.O#OO....#.#..O.O.##........O
            O..OO...#OO.#O........#...O#OO#..O....#......OOO..OOO#...OO.O....##..#...O#...#.#..#O..O...##..##.#.
            OO..#O#..O...#......#.#.#O#.....#..OO......OO#.OO..#.....O...O#....#O..O...#O...#.OO.##.###.....O..O
            .#.O......#...OO##.#.......O...O.......O#..O#...#..O#..O#O#..O.#O..OO.O..O.#..O..O#......O....O.....
            #..#O#O.#.O...O...#...#.#.#.#O....O.O..#O........O#..#.#...O.O.##.....O.O.O....OO..OO.....##.#O..O..
            ...#......O#...#..O#O.O...#..OOO....#O#...#.....#..O#....#O......#.O..O.#O.......O#..O.#......O.O...
            ..O.....O...O.....O#...#..#OO..##O.O.........OO...##.O.#..#.##O.O.O#..O#.#.#OO#......O#..#.#.O#O#.O.
            ##..OO.O..##O..O...O.O.....#.O#.O.OO....#..##O.O.##.O.#....#......O........##.....OOO.O.#.###....O#O
            ..OO..O.OO..#..O....O..O.###.#..#..#..#......OO#O#..#.#.#.OO#.#...O.....#..O#...............#....O..
            .#O.O..#...#.#OO..O.........O.##.#.....OOO.O.O.......OO##.##.OO.#......OO..O.O...#.....O...#O.#.O.O.
            .O.O#O...O.O#O.O..O.O.O.O..O.O.O...O.#.#....#....#..O.#O.#........#.#...O.......#...O..O...O..#O...O
            ..O...O.O...O.#..........#O.O..........#......O...#OO..O..O##..O......O...#.O...O..#......OO..O....O
            ..#........#..O..O..#...#O...O.O...##....O..#O.#.#.#......O#.O....##O...O.OO.#...O.OOO.#..OOO...O...
            ..OO..O#...O..O#O..O.##...#.O.#O###.O#...O.###...O....O...#...#..#.#....#...O..O....O...####O.#OOO.O
            .O....#.#O#.O.#.#.........O#.#..#.O.....O####.O.OO.O.O#O..O......O.#.OO.O.......OOO..#........OO..#O
            #O....#...O..O.#..O.O#.O..OO#OO.O#..#.#....#.O...#..#O#.O.O.O...O#.O#...#O..O..O..O.O...#.#.#O....O#
            #.....##...O.O..OO..O#.....O...........#.....#O#O......O..OO.....##..#...#O..#.O.........O.....O....
            #...#.#O#O..O...OO.O.O.O..#O..##..O..OO.....#OO.#.#....#.....O.#....#....#...#O....O.#.##..#O.O..#..
            O.##....O..O.#.OO.....#..OO#..#.OO.....#O..OOO#.......#.#....O....#.....OO....#.OO.....#.#..O...O.#.
            ..O...O#..O..#...OO.......#O.O.OO..OO.O...O.O.O.#..#OOO.#.....O....O..OO........#.....O.O#.O.O##....
            ....O..#.#O...#.O....#O.OO.#..#.O...##..#......#OO..#....#....#.O..#OO#.#...O..O..O#.O.O#...O##...OO
            .O.O....#...O#.O.......#.O........#..OO.#O...O##O......#.O.OOO......#OO.O.O..O###..#.O..OO.O.O.O....
            O.......#..O#......O.O#..O...O..O..O..O#..##....................O.#...........##....#..##.#...#O.###
            ..O...O.......O....O..OO..O.#......#.......O#O.....#..##..O.O#.....O...O.#OO....O.#.O.O.#O.#..OO..O#
            #OOO....O.....O..O.O.....OO.OOO..#O.O..O..#O.#.....#.O...OO.O.......#..#O...O.....O.#...O#...O...OO.
            O.#..O..OO.OO#O....#.O.......O....O....#..O...O.....OOOOO#.#...O#.#O.O..OO#O.O.......#.....O...O.OO.
            .O#O.O....O.#OO##......O.......O#.#..##...O......#.#........#O..#.##..#.O.....#..OO..#..#.O.#.O...#O
            .OO....#..O..O#.O.#O.........O.....#OOO............OOO..#.....#...#.O..##.OO....##...O#O....OO#.#.O#
            ...O.##.....###......O#...O.......#.#...#.......O.....###..OOO...O.O..O..O#.O....#O....#.#.......O.#
            OO#....##..O.#......#OO..#....#.#.O.........#.OOO....OO.###....O#.#..#O.O.O..#...OO#.OO#.....O#O#...""";
}
