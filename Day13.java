import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day13 {
    public static void main(String... args) {
        for (String input : List.of(TEST_INPUT, INPUT)) {
            var patterns = parse(input);

            var partI = patterns
                    .stream()
                    .mapToInt(pattern -> pattern.findReflection() * 100 + pattern.transpose().findReflection())
                    .sum();

            System.out.println("Part I: " + partI);

            var partII = patterns
                    .stream()
                    .mapToInt(pattern -> pattern.findSmudgedReflection() * 100 + pattern.transpose().findSmudgedReflection())
                    .sum();

            System.out.println("Part II: " + partII);
        }
    }

    private static List<Pattern> parse(String input) {
        return Stream.of(input.split("\n\n"))
                     .map(grid -> grid.lines()
                                      .map(line -> line.chars()
                                                       .mapToObj(c -> c == '.' ? Cell.ASH : Cell.ROCK)
                                                       .toArray(Cell[]::new))
                                      .toArray(Cell[][]::new))
                     .map(cells -> new Pattern(cells, cells[0].length, cells.length))
                     .toList();
    }

    record Pattern(Cell[][] cells, int width, int height) {
        @SuppressWarnings("SuspiciousNameCombination")
        Pattern transpose() {
            Cell[][] transposed = new Cell[width][height];
            IntStream.range(0, width).forEach(x -> IntStream.range(0, height).forEach(y -> transposed[x][y] = cells[y][x]));
            return new Pattern(transposed, height, width);
        }

        int findReflection() {
            for (int line = 1; line < height; line++) {
                if (isReflection(line)) {
                    return line;
                }
            }

            return 0;
        }

        int findSmudgedReflection() {
            for (int line = 1; line < height; line++) {
                if (!isReflection(line)) {
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            cells[y][x] = cells[y][x] == Cell.ASH ? Cell.ROCK : Cell.ASH;
                            boolean reflection = isReflection(line);
                            cells[y][x] = cells[y][x] == Cell.ASH ? Cell.ROCK : Cell.ASH;

                            if (reflection) {
                                return line;
                            }
                        }
                    }
                }
            }

            return 0;
        }

        private boolean isReflection(int line) {
            return IntStream.range(0, Math.min(line, cells.length - line))
                            .allMatch(distance -> Arrays.equals(cells[line - distance - 1], cells[line + distance]));
        }
    }

    enum Cell {ASH, ROCK}

    private static final String TEST_INPUT = """
            #.##..##.
            ..#.##.#.
            ##......#
            ##......#
            ..#.##.#.
            ..##..##.
            #.#.##.#.
                        
            #...##..#
            #....#..#
            ..##..###
            #####.##.
            #####.##.
            ..##..###
            #....#..#""";

    private static final String INPUT = """
            ###.#.###
            .#..####.
            .##.#.#..
            .##.#.#..
            .#..####.
            ###.#.###
            .#.#.#.##
            ######.##
            #.#....#.
            #.#....#.
            ######.##
            .#.#.#.##
            ###.#.###
            .#..####.
            .##.#....
                        
            #..###...#.
            .##.####.#.
            .##..#.#...
            #####..#.#.
            #####..#.#.
            .##..#.#...
            .##.#.##.#.
            #..###...#.
            .....#.#.#.
            #..#..#...#
            .##.#.#####
                        
            .#..##.#.##
            #.###....#.
            ..#...##..#
            .###.#..#.#
            #.#.####.##
            #.#.####.##
            .###.#..#.#
            ..#....#..#
            #.###....#.
            .#..##.#.##
            .#..##.#.##
            #.###....#.
            ..#....#..#
                        
            ##.#.........
            ..#...######.
            ..####......#
            ##....#.##.#.
            ...#...####..
            ..#..#.####.#
            .......#..#..
            ##.#.########
            ..####......#
            ...##...###..
            ..#.###.##.##
                        
            .#..####...
            ####.##.###
            .##.#...###
            ##......###
            #######.#..
            #######.#..
            #.......###
            .##.#...###
            ####.##.###
            .#..####...
            #...###....
            .##....#...
            ###..##.###
            .##...#..##
            ..##.###.##
                        
            .#...##..#.#...##
            ..#..##...####.#.
            #.##..#..#..###..
            .#.##..##.#...###
            ###..#..#.###...#
            .##..#####..#..##
            .##..#####..#..##
            ###..#..#.###...#
            .#.##..##.#...##.
            #.##..#..#..###..
            ..#..##...####.#.
            .#...##..#.#...##
            #..####.#....#.#.
            #..#.....#..##..#
            #..#.....#..##..#
                        
            ....##.
            #####..
            .....#.
            ....###
            #..####
            ####...
            ....###
                        
            ##.###...#...
            ###.###.####.
            ...#.#.......
            ..###.#.####.
            ##...##......
            ...##..######
            ..###..#....#
            ###....##..##
            ...##..#.##.#
            #########..##
            ##..##.######
                        
            ###.##....##.##
            ####........###
            ...#..####..#..
            ##....#..#....#
            ..####.##.####.
            ###.#.#..#.#.##
            ....#..##..#...
            ##..#..##..#..#
            ##.##....#.##.#
            ...##########..
            ..##...##...##.
            ##..#..##..#..#
            ..#..##..##..#.
            ###...#..#...##
            ###.#.####.#.##
                        
            #..#.##..
            #####..#.
            #..#..##.
            #..######
            .##.##..#
            .##..####
            .##..##.#
            .##.##..#
            #..######
                        
            ##.########.#
            ##.##..#.###.
            ##.##..#.###.
            ##..#######.#
            ..##...######
            ..#..###....#
            ..#.#.#.##.#.
                        
            ####....#
            .#.#.##.#
            .#.#....#
            ..#.#..#.
            .##..##..
            ...##..##
            ..##....#
            ..##....#
            ...##..##
            .#...##..
            ..#.#..#.
            .#.#....#
            .#.#.##.#
                        
            .......##.##.##
            ......#.##..##.
            .####..#..##..#
            #....#..#....#.
            ........#....#.
            #######.#.##...
            ..##..#.#.##.#.
            #....#....##...
            .####....#..#..
            #....#...####..
            #....#...#..#..
            ########..##..#
            #....#####..###
            .####...##..##.
            ##..##.#......#
                        
            .#.#...
            .#.#..#
            ...##.#
            ...##.#
            .#.#..#
            .#.#...
            ####...
            .#..###
            ...####
            ##..#.#
            .######
            ###...#
            #####.#
                        
            #.#####.##.
            ...#.#.....
            .#.###.....
            .#.###.....
            ...#.#.....
            #.#####.##.
            .###.##....
            #####.#.##.
            ....##.####
            #####..###.
            .##..##....
            ....#..####
            #.#...#....
                        
            ####.##..#.##
            ....#.#.##.##
            ##.#..#####.#
            ######.####..
            ####..##.#..#
            .#....###.#..
            ...###.##.#..
            ...#..#.##...
            ...#..#.##...
                        
            #..#......#
            .#.##.##.##
            .#.##.##.##
            ####.#..#.#
            ####.#..#.#
            .#.##.##.##
            .#.##.##.##
            #..#......#
            ####.#..#.#
            .##........
            .###.####.#
            .##.#.##.#.
            .###..##..#
            #..........
            ...########
            .#.###...##
            .....#..#..
                        
            .##..#.##
            .##....##
            .##...###
            .....####
            .##.##.#.
            #..#.##.#
            ####.###.
            #..#.#..#
            .##..##.#
            #..#.#...
            .....##..
            .##.###.#
            .....##.#
            .....#..#
            ....#.##.
                        
            ####..#..
            .##..#...
            ##..#...#
            ....###..
            .#..###.#
            ......#.#
            ......#.#
            .#..###.#
            ....###..
            ##..#...#
            .##..#...
            ####..##.
            ####..##.
                        
            #.####.##..
            .#....#..##
            #......##..
            ###...##...
            .##..##.#..
            ..####..###
            #..##..####
            .#....#....
            .#.##.#..##
                        
            ....##..##.
            .#...#..#..
            .##..#..#..
            ....##..##.
            #.###.##.##
            .#.#......#
            #..........
            ##.........
            ####..##..#
            ###..####..
            ####.####.#
            .#.##....##
            #.#.##..##.
            ..##..##..#
            #####.##.##
            #..#.####.#
            ######..###
                        
            ###.##......##.##
            #.#.###....###.#.
            ..#.###....###.#.
            ###.##......##.##
            .#####.####.#####
            ..#.##..##..##.#.
            .###..#....#..###
            ..##...#..#...##.
            ...##.######.##..
            ..#.....##.....#.
            .....#.####.#....
                        
            .#.####.#.###.#
            .#......#....#.
            .#......#....#.
            .#.####.#.###.#
            ###.##.####.###
            #........###.##
            ....##.....###.
            #.#.##...##.###
            #...##...#..#.#
            #.#....#.##....
            #........#..#.#
            ..#....#...#...
            ...#..#...##.#.
            .#......#.###.#
            #.######.#.....
            ..#.##.#...##..
            .#.#..#.#....#.
                        
            ##.##.#...#
            ......#..#.
            ......#..#.
            ##.##.#...#
            ####..##.##
            ######..##.
            ###..##..#.
            ##.##.#####
            ##.#.#.###.
            #.#...##..#
            ..####.####
            ...#..#####
            ..##..##.##
            ###....####
            ##..#.###.#
                        
            .##...#....##
            ....#.#..#.##
            .####..#...##
            ##.##...#....
            #..##.#######
            #.......###..
            ###.##..#.#..
            ##...##......
            ##...##......
            ###.##..#.#..
            #.......###..
            #..##.#######
            ##.##...#.#..
                        
            ##.##.#
            ##....#
            ##....#
            #####.#
            ..##.#.
            ####.##
            ###.##.
                        
            .#..#..........
            .#..#..##.##.##
            ######..##..##.
            ##..###.##..##.
            #.##.##...##...
            ..##....#.##.#.
            .####..#.#..#.#
            #....####.##.##
            .####.##.####.#
            ..##....#....#.
            #######.######.
            ########.####.#
            #....##.#....#.
            ##..##..######.
            #....##..####..
            ####.####.##.##
            .####....#..#..
                        
            #.########.####
            .#.#.##.#.#....
            #...#..#...#.##
            .##.####.##.###
            .##.####.##.###
            #...#..#...#.##
            .#.#.##.#.#....
            #.########.####
            #.#......#.#.##
            .###....###....
            .#..####..#..#.
            #...#..#..##.##
            ####.##.####.#.
                        
            ####.#.#.
            ...#....#
            .....#..#
            .....#.##
            ##...####
            ###.##.#.
            ###.#..#.
            ##..#####
            ..####.##
            ...###.##
            ######...
            ###..####
            ##..#.#.#
            ...#.##.#
            ...#.##..
                        
            .##..#.##.#..##.#
            #..##......##..#.
            ################.
            .##.#......#.##..
            ###.##.##.##.####
            ....########....#
            .##.###..###.##..
            #####.#..#.#####.
            ####........####.
            ....########.....
            .....#.##.#.....#
            ....#..##..#....#
            .##.###..###.##.#
            ################.
            .......##.......#
            ......#..#.......
            .##..#....#..##.#
                        
            #.##...#.....#..#
            #.##...#.....#..#
            ..##..##.#.###...
            .....#.####.###..
            #####.###.....#.#
            #.##..###.##.#.##
            ##....#..###...##
            .###...#.#..###..
            ....##..#.###.#.#
            .##.#.##.....###.
            .##.#.##.....###.
            ....##..#.###.#.#
            .###...#.#..###..
            ##....#..###...##
            #.##..###.##.#.##
            #####.###.....#.#
            .....#.####..##..
                        
            #........##
            .#.####.#..
            .##..#.##..
            .#.#..#.#.#
            .##....##..
            #.##..##.#.
            ...#..#...#
            .########.#
            #.##..##.##
            ..######...
            ..#....#...
            ####..#####
            #.##..##.#.
            #.#....#.##
            #.#....#.##
            #.##..##.#.
            ####..#####
                        
            ......##.
            #..##....
            .##...#.#
            .......#.
            ....#.##.
            .##......
            .##......
            ....#.##.
            .....#.#.
            .##...#.#
            #..##....
                        
            .####.#.#..
            ##.....##..
            ##......#..
            .####.#.#..
            .##.....#.#
            ##...#.##.#
            .#.###.#...
            ..#####.##.
            ..#####.##.
                        
            #####.##.###.#.#.
            ##..#...#.....#..
            ..#..###.#...####
            ....##.#...####.#
            ###..###.....##..
            ...##.#####..####
            ####.#.##.###.##.
            ####.#.##.######.
            ...##.#####..####
            ###..###.....##..
            ....##.#...####.#
            ..#..###.#...####
            ##..#...#.....#..
            #####.##.###.#.#.
            ...#.###.#..##...
            ####..#...#..#.##
            ##...#...#...##.#
                        
            .##.....#
            ...######
            #..#.##.#
            .#.......
            #.#.####.
            #....##..
            ..#.####.
            ##.##..##
            ##.##..##
            .####..##
            .##......
            .##.#..#.
            .##.####.
            .##.####.
            .##.#..#.
                        
            .#.#...#.
            .#.#...#.
            ...#####.
            #....###.
            #####.#.#
            #####.#.#
            #.#..###.
                        
            ####..######..#
            #.##.#.#..#.#.#
            ....#.######.#.
            #..#...#..#...#
            .##.#..####..#.
            ####.########.#
            .##.##.#..#.##.
            ####..#....#..#
            .##...#....#...
                        
            #.#.##.
            #.##.##
            ##.#.##
            #.#####
            ##.#.##
            .....#.
            .....#.
            ##.#.##
            #.#.###
            ##.#.##
            #.##.##
            #.#.##.
            ##.#.##
            #.#..#.
            ##.#...
            .##..#.
            .##..#.
                        
            ..###.####.
            ##.###.##.#
            ...####..##
            ..#.##....#
            ##.#..#..#.
            ..##.######
            ..####.....
                        
            #####.#..
            ##.#.#.##
            ##.##...#
            ##.#...##
            ##.#...##
            ##.###..#
            ##.#.#.##
            #####.#..
            ########.
            ##.###.##
            ####....#
            ..#...###
            ##.#..#..
            ##...##..
            ##.##...#
            ####.####
            ..#######
                        
            ..#.#....#.#..###
            #.#.######.#.####
            ##.#.#..#.#.##...
            #....#..#....#...
            .#.##.##.####....
            ....#.##.#.......
            .....####.....#..
            .##.#.##.#.##....
            ..#.##..##.#..#..
            ##..######..##.##
            .##........##.###
                        
            #.#...######.#.
            #####.#...#.#.#
            #####.#...#.#.#
            #.#...######.#.
            ##.....#...#...
            .#...#....#..#.
            ##.####...##...
            ...#.##.....###
            #.#.###.#....#.
            ..#.#.#....#...
            ..#.#.#....#...
            #.#.###.#....#.
            ...#.#......###
                        
            ###.####...#..#
            ###.####...#..#
            #.#.#.#.#.#.#.#
            ##..###.....#..
            #####..###...##
            ..#.#.#.#.#####
            ##......##.#...
            .#.#.......###.
            #####...##.#.#.
            ..##..#.####..#
            ..##..#.####..#
            #####...##.#.#.
            .#.#.......###.
            #.......##.#...
            ..#.#.#.#.#####
            #####..###...##
            ##..###.....#..
                        
            ###..####
            #.#..#.#.
            ...##...#
            ..#..#...
            #......#.
            #.#..#.#.
            ##.....##
            .#.##.#.#
            ##.##.##.
            ##.##.##.
            .#.##.#.#
                        
            .####.#...#.#
            #.#.#...#.###
            #....###.#.#.
            #....###.#.#.
            #.#.#..##.###
            #.#.###.#.#..
            #.#.###.#.#..
            #.#.#..##.###
            #....###.#.#.
            #....###.#.#.
            #.#.#...#.###
                        
            #...#..#...##...#
            #.#..##..#.......
            #..#....#..##.###
            ####.##.####.#.##
            .###.##.###..##.#
            ##..####..####...
            .##########.##..#
            ##.##..##.######.
            .##.####.##.#..#.
            ##############.##
            .##.####.##.##...
            .##.####.##.##...
            ##############.##
            .##.####.##.#..#.
            ##.##..##.######.
                        
            .#.##..##
            .#.##..##
            ...#..###
            .#..###..
            ###..#..#
            ##.###.#.
            #..###.#.
                        
            .##.##..#
            .###.#..#
            #..#.#..#
            #..##....
            .##.#####
            ####..##.
            #..######
                        
            ##...#.
            #.#####
            .#..##.
            #......
            #....#.
            #....#.
            #......
            .#..##.
            #.####.
            ##...#.
            ##.####
            ##.####
            ##...#.
                        
            #..#..#..##..#...
            #..##....##.#...#
            ....######...##.#
            .....#.##.#..####
            ######....####.#.
            ....######.##.#..
            ####..#..###..#.#
            ####..##.###.#.##
            .##.###.#.#####.#
            .##.##.##.#####..
            .##.##.##.#####..
            .##.###.#.#####.#
            ####..##.###.#.##
            ####..#..###.##.#
            ....######.##.#..
                        
            ..###.#
            ##.####
            #..##..
            ####..#
            ####..#
            #..##..
            ##.####
            ..##..#
            #.....#
            ####..#
            ##.##.#
            ##.#..#
            #.####.
            ##.###.
            ##.###.
                        
            ######....#
            #.##.######
            .#..#.#..#.
            #....######
            .#..##.##.#
            ......#..#.
            .####.####.
            ......#..#.
            #.##.#.##.#
            #....######
            ......#..#.
            #....#....#
            ......#..#.
            .#..#......
            .#..#.#..#.
                        
            ##.....#.#..###..
            .#.....#.#..###..
            ###..#.....###...
            #.#.#..##..####..
            ##.####.#.##..#..
            .####.##.#.#.#.##
            ..###.#.#.#.###..
            ..##..#.###...#..
            #..##.#....#.....
            .#..##....#..#.##
            ######...#....#..
                        
            ..##.##.##...
            ..#......#..#
            ###......###.
            ###..##..####
            ###.####.####
            ##.#.##.#.###
            ##...##...##.
            .#..####..#..
            ###.####.####
            ..###..###...
            ..#..##..#..#
            ##..#..#..##.
            ###.#..#.###.
            ....####.....
            ##.##..##.##.
            ..##.##.##...
            ##........##.
                        
            ..###.##.##.#.#
            .###.#####....#
            #.########.#.#.
            ..#.#.#..##.#.#
            .#.##..#.....#.
            #####.####..###
            #.#.#.#.###..##
            #.#.#.#.###..##
            #####.####..###
            .#.##..#.....#.
            ..#.#.#..##.#.#
            #.#####.##.#.#.
            .###.#####....#
            ..###.##.##.#.#
            ..###.##.##.#.#
                        
            ..##.....####
            ..##.....####
            #.##.##...###
            ......#...#..
            ####.#..#.#..
            #.##.#.#...#.
            .####..######
            #.##.##.##.#.
            #.##.#...#...
            #....#.##..##
            ##..#####.#.#
            ..##....#.###
            ######...##.#
            #....####..##
            #....#.##.##.
            .......#.##.#
            .#..#.##.....
                        
            .....##..
            #####..##
            ###......
            ..###..##
            ..###....
            ##....#..
            ...###...
            ..#.#.###
            .....#...
            ##.#..###
            ##..#####
            ##.....#.
            ##..###..
            ##.#.#.##
            ...#.#.##
            ..#.#....
            ..###.#..
                        
            ##..#..#.#####..#
            ##..#..#.#####..#
            #....#.######.##.
            ##..###.#....####
            ...##....#####...
            .##.....#.####..#
            #.#.#.#..#..#####
                        
            ##..#.##.#..#
            ###...##...##
            ##...####...#
            ###..#.....##
            ####..##..###
            ##..#....#..#
            ..#..#..#..#.
                        
            #####..####
            #..###...##
            #..###...##
            #####..####
            #..##..#..#
            .........##
            #######.#.#
            ########..#
            #..##..####
            .##.###....
            .....#.#..#
            .#..#....##
            #####.###.#
            ####....###
            .....##...#
            ........##.
            .##..##..##
                        
            ##.....#.
            ###.#....
            .#.###.##
            ..###.#.#
            ##....###
            ##.#.#...
            ..#..###.
            ...####..
            ...####..
            ..#..###.
            ##.#.#...
                        
            .#.#.##.##.
            .#.#.##.##.
            ##..####.##
            ####.##.###
            ...#...####
            ...#...####
            ####.##.#.#
                        
            ###.##.
            .#.#.##
            #..##.#
            #..##.#
            .#.#.##
            ###.##.
            ###.#.#
            ##.#...
            ###.##.
            ###.##.
            ##.#...
            ###.#.#
            ##..##.
            .#.#.##
            #..##.#
                        
            ##...#..####...
            ..##...#.#.#...
            ##.#####.#..#.#
            ##.#..##.#..#.#
            ##.#..##.#..#.#
            ##.#####.#..#.#
            ..##...#.#.#...
            ##.#.#..####...
            ###..#.###.#...
            .......#......#
            ..##.####.#.#.#
            ....#.##..#.###
            ##..#.##..##.##
                        
            ......#
            #.##.##
            #.##.#.
            ......#
            .####..
            #.##.##
            ##..###
            ..##..#
            ......#
            ..##...
            .#..#..
            ######.
            ......#
                        
            #..#..#.##.
            #####..##.#
            ####.###.##
            ####.###.##
            #####..##..
            #..#..#.##.
            #..##...##.
            ....#.#####
            #..#.##....
            ##########.
            #..###.###.
                        
            ########.#.
            .##..##.#..
            .##..##.###
            ........#..
            .##..##...#
            ##.##.#####
            #..##..#..#
            .##..##.#.#
            .##..##.###
            #..##..####
            .........##
            .##..##.#..
            #..##..#...
            ########..#
            .........##
                        
            ..#####..##
            ....##....#
            ....##....#
            ...####..##
            #..#..#..#.
            .#.###.##.#
            ..###.####.
            ##.....##..
            .####......
            ...#.#.##.#
            ##.####..##
            #.##.##..##
            #.#..##..##
            .#..##....#
            #...###..##
            #..##......
            .###..#..#.
                        
            ........####...
            .#.##.#..##..#.
            #......#.##.#..
            #.####.######.#
            ###..####..####
            .........##....
            #.#..#.#.#..#.#
                        
            .##.#.#.#.#....
            #.##.#.##.###..
            .#..#.####...##
            #..#.##..#.....
            ##..#.###..####
            ##..#.###..####
            #..#.##........
                        
            ####..#####......
            ####..#####.#....
            ..#....#..####..#
            .##.##.##.#.###.#
            ##.#..#.#####..##
            .########......#.
            .#......#..#.#..#
            ..######...###...
            ###########.##.#.
            ##.#..#.##.#.#...
            ....##.....####..
            #.#....#.#......#
            ###....#####.#..#
            .............#...
            .########.#..###.
                        
            ###..#..##.
            ##.##..#.##
            #.#.##..##.
            .###.####..
            #.#...#..##
            #.#....#.#.
            #.#....#.#.
            #.#...#..##
            .###.####..
            ..#.##..##.
            ##.##..#.##
            ###..#..##.
            #..##.#.###
            ##...###.##
            ##...###.##
                        
            ###..##.##.#.#.
            ###..##.###...#
            ..#.#.#.#..##.#
            ##.###.....####
            ..#..#.#.###.#.
            #####.##.##....
            ....##..#....##
            ....##..#....##
            #####.##.##....
            ..#..#.#.###.#.
            ##.###.....####
            ..#.#.#.#..##.#
            ###..##.###...#
            ###..##.##.#.#.
            ..#..##.####...
            ####....##.##..
            .#..#.#.###..#.
                        
            ##...##
            ##.....
            ##.....
            ##...##
            ...#.#.
            ##.#.#.
            ..#.#..
            ..#..##
            ##.#..#
            ##.#.##
            ...###.
            #..####
            ###.#..
            ##.#...
            ##.###.
            ..#..##
            ###.###
                        
            ##...#.
            ###.#.#
            .#.##.#
            .#.##.#
            ###.#.#
            ##...#.
            #####.#
            ....###
            ######.
            ..#.##.
            ...#.#.
            ..##.#.
            ...#.#.
                        
            ###.#..#..#..
            ...#.########
            .#...#.####.#
            ..#.####..###
            ..##...#..#..
            ###.#..####..
            ......#....#.
                        
            #####...#.##.#...
            #..#####.####.###
            .###.##.#....#.##
            ..#.##..#.##.#..#
            ..#.##..#.##.#..#
            .###.##.#....#.##
            #...####.####.###
                        
            ....##.
            ##...##
            ....###
            ....###
            ##...##
            ....##.
            #####..
            .#.#...
            ###.#..
                        
            .#.##..########
            .#...####.##.##
            ##.#..###...#..
            .###.###.....##
            #.#..######..##
            ...#.##...#.###
            .#.#.###.#.#.##
            ##.####....####
            .###.#......#..
            ###.##..#.#####
            #..##.#.#.##...
            ....#.##.##....
            .###.#.#.##....
            ...#.##.###.###
            ...#.##.###.###
            .###...#.##....
            ....#.##.##....
                        
            ..#....#.
            ##..##..#
            ####..###
            ..#.##.#.
            ###.##.##
            ##.####.#
            ...###...
            ...####..
            ##..##..#
                        
            .##.###.####.
            ########.####
            ##.#.###..#.#
            ####.#..#####
            ###..#....##.
            ###..#....##.
            ####.#..#####
            ##.#.###..#.#
            ########.####
            .##.###.####.
            ...#........#
            ...###.#...#.
            ...###.#...##
            ...#........#
            .##.###.####.
                        
            ######..#
            .....####
            .##.##..#
            .###.....
            #####....
            ######..#
            .##.#.##.
            ####.....
            #..#.....
            .....####
            ......##.
            .##..####
            ####.....
                        
            ##.##########
            .#..##.#.####
            ####.#..##..#
            ##....###.##.
            ..##..#.#.#..
            .#....#......
            ...####......
            #.#...#.##..#
            #.#...#.##..#
                        
            .#..#...#.#######
            ##...###.#.#....#
            .#.....##.###..##
            ..####.#.#...##..
            ....#...#..######
            ####...#.....##..
            ####...#.....##..
            ....#...#..######
            ..####.#.#...##..
            .#.....##..##..##
            ##...###.#.#....#
            .#..#...#.#######
            #.#.#...###......
                        
            ###...#...#...#
            ###...#...#...#
            #...##.##.#.#..
            ..##..##.#..#.#
            .###....#.###.#
            #.#.##.....##.#
            ..#####...#####
            ..#####...#####
            #.#.##.....##.#
            .###....#.###.#
            ..##..##.#..#.#
            #...##.##.#.#..
            ###...#..##...#
                        
            #..#...##..#...
            ##.#...##...#..
            #.##.#.#.#..###
            #.##.#.#.#..###
            ##.#...##...#..
            #..#...##..#...
            ####.#.##.##.#.
            #...###.#.#.#..
            .##......#..##.
            ..##.####.####.
            ..##.####.####.
            .##......#..##.
            #...###.#.#.#..
            ######.##.##.#.
            #..#...##..#...
            ##.#...##...#..
            #.##.#.#.#..###
                        
            ##........###
            .#.#....#.#..
            ..########...
            ###..##..####
            ##...##...###
            #..#....#..##
            ..#.####.#...
            #.#......#.##
            .#.#....#.#..
            #..#....#..##
            .....##......
            .###.....##..
            #..#.##.#..##
            .#..#..#..#..
            ###..##..####
                        
            #.##..#.##..#..
            #.##..#.##..#..
            ...##.#.##.#.#.
            ####.#####..##.
            .###.#..#.##...
            .###.####......
            #.#..#..#.#.##.
            #..#####..##..#
            ...#....#...#.#
            .#..####.##..#.
            #.#.#....#####.
            ..#..##.#.###..
            ..#..##.#.###..
            #.#.#....######
            .#..####.##..#.
            ...#....#...#.#
            #..#####..##..#
                        
            #..##..##..##..
            .######..##..##
            .#....#.#..#.#.
            ..####........#
            ..####...##...#
            ##.##.#.#..#.#.
            ...##...####...
            #.#..#.#....#.#
            .#.##.#..##..#.
            ##.##.###..###.
            ..#..#........#
                        
            .##..#.....#.
            ....#..##.##.
            ####.....#.##
            ####.###..#.#
            #####.#...#..
            #..#.#.######
            .#...#......#
            .##.##...####
            ####....##.##
            ####.#...####
            ####.#...####
            ####....##.##
            .##.##...####
                        
            ..##..##.
            ..######.
            ####..###
            ...#.....
            ###.##.##
            ..##..##.
            ##......#
                        
            ####..######.
            .#.#..#.#..##
            #..#..#..####
            ###.##.######
            ...#..#...###
            ..######..#..
            #.######.####
            ..#.##.#..###
            #..####..#.##
                        
            ..#.###
            ##...#.
            ##...#.
            ..#.###
            #..###.
            #####..
            #####..
            #.####.
            ..#.###
                        
            ....##...
            ....#####
            ......#..
            ..######.
            #####.#..
            ##..#.#..
            ..#...#..
            ..##...##
            ##..##.##
            ###.#####
            ..##...##
            ..#..##..
            ......###
            ###.##...
            ##.#..###
                        
            ##..#.#
            ###.##.
            #.#...#
            #.#...#
            ###.##.
            ##..#.#
            .#.#.#.
            ###.#..
            ###.#..
            .#.#.#.
            ##.##.#
            ###.##.
            #.#...#
                        
            ####.....
            ..#.#..#.
            ####.#..#
            ....##..#
            #####.##.
            .###.####
            .##.#....
            ##.##.##.
            ###...##.
            .###..##.
            #.#......
            #.#......
            .###..##.
            ###...##.
            ##.##.##.
                        
            ##.##.#..
            ##.##.#..
            .##.##..#
            ..###...#
            #...#..##
            .###.##.#
            ..##..#.#
            ..##..#..
            .###.##.#
                        
            #####.##.####.###
            ...#.####...#....
            ##.###.#..##.####
            .##..##...##..#..
            .######.....#####
            ##.#..######.####
            .##.###.##..##...
            ...#####..#.#...#
            ...##..#.....#.##
            ..#.#...#......##
            ..#.#...#......##
                        
            ######.##
            ##..###..
            .......#.
            #.##.#..#
            ###.####.
            ######..#
            .#..#.#.#
            #....###.
            .......##
            .####.##.
            #....#...
            ..##....#
            ..##....#
            ######...
            .#..#..##
            .#..#..##
            ######...""";
}
