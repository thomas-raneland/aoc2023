import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Day23 {
    public static void main(String... args) {
        for (String input : List.of(TEST_INPUT, INPUT)) {
            var map = parse(input);
            System.out.println("Part I: " + partI(map));
            System.out.println("Part II: " + partII(map));
        }
    }

    static long partI(HikingMap map) {
        return longestDownhillPath(map.start(), new HashSet<>(), map);
    }

    static long longestDownhillPath(Pos pos, Set<Pos> visited, HikingMap map) {
        if (pos.equals(map.end())) {
            return 0;
        }

        visited.add(pos);
        long max = Long.MIN_VALUE;

        for (Pos next : map.downhillNeighbors(pos).toList()) {
            if (!visited.contains(next)) {
                long distance = 1 + longestDownhillPath(next, visited, map);

                if (distance > max) {
                    max = distance;
                }
            }
        }

        visited.remove(pos);
        return max;
    }

    static long partII(HikingMap map) {
        Set<Pos> nodes = new LinkedHashSet<>();
        nodes.add(map.start());
        List<Pos> neighbors = map.allNeighbors(map.start()).toList();
        var graph = new AocUtils.Graph<Pos>();
        junctions(graph, map.start(), neighbors, map, new HashSet<>(), nodes);
        return graph.longestPath(map.start(), map.end());
    }

    static void junctions(AocUtils.Graph<Pos> graph, Pos start, List<Pos> neighbors, HikingMap map, Set<Pos> visited,
                          Set<Pos> nodes) {

        int length = 0;
        Pos end = start;

        while (neighbors.size() == 1) {
            length++;
            visited.add(end);
            end = neighbors.get(0);
            neighbors = map.allNeighbors(end).filter(n -> !visited.contains(n)).toList();
        }

        graph.addEdge(start, end, length);
        graph.addEdge(end, start, length);

        if (nodes.add(end)) {
            for (Pos n : neighbors) {
                if (!visited.contains(n)) {
                    junctions(graph, end, List.of(n), map, visited, nodes);
                }
            }
        }
    }

    static HikingMap parse(String input) {
        Map<Pos, Symbol> symbols = new HashMap<>();
        var lines = input.lines().toList();

        for (int y = 0; y < lines.size(); y++) {
            for (int x = 0; x < lines.get(y).length(); x++) {
                var pos = new Pos(x, y);
                var symbol = switch (lines.get(y).charAt(x)) {
                    case '#' -> null;
                    case '.' -> Symbol.P;
                    case '>' -> Symbol.SE;
                    case '<' -> Symbol.SW;
                    case '^' -> Symbol.SN;
                    case 'v' -> Symbol.SS;
                    default -> throw new IllegalArgumentException();
                };

                if (symbol != null) {
                    symbols.put(pos, symbol);
                }
            }
        }

        var start = symbols.keySet().stream().min(Comparator.comparingInt(Pos::y)).orElseThrow();
        var end = symbols.keySet().stream().max(Comparator.comparingInt(Pos::y)).orElseThrow();
        return new HikingMap(symbols, start, end);
    }

    record HikingMap(Map<Pos, Symbol> symbols, Pos start, Pos end) {
        Stream<Pos> downhillNeighbors(Pos pos) {
            Stream<Pos> neighbors = switch (symbols.get(pos)) {
                case P -> Stream.of(Direction.values()).map(pos::step);
                case SN -> Stream.of(pos.step(Direction.N));
                case SE -> Stream.of(pos.step(Direction.E));
                case SS -> Stream.of(pos.step(Direction.S));
                case SW -> Stream.of(pos.step(Direction.W));
            };

            return neighbors.filter(symbols::containsKey);
        }

        Stream<Pos> allNeighbors(Pos pos) {
            return Stream.of(Direction.values()).map(pos::step).filter(symbols::containsKey);
        }
    }

    record Pos(int x, int y) {
        Pos step(Direction d) {
            return switch (d) {
                case N -> new Pos(x, y - 1);
                case E -> new Pos(x + 1, y);
                case S -> new Pos(x, y + 1);
                case W -> new Pos(x - 1, y);
            };
        }
    }

    enum Symbol {P, SN, SW, SE, SS}

    enum Direction {N, E, S, W}

    static final String TEST_INPUT = """
            #.#####################
            #.......#########...###
            #######.#########.#.###
            ###.....#.>.>.###.#.###
            ###v#####.#v#.###.#.###
            ###.>...#.#.#.....#...#
            ###v###.#.#.#########.#
            ###...#.#.#.......#...#
            #####.#.#.#######.#.###
            #.....#.#.#.......#...#
            #.#####.#.#.#########v#
            #.#...#...#...###...>.#
            #.#.#v#######v###.###v#
            #...#.>.#...>.>.#.###.#
            #####v#.#.###v#.#.###.#
            #.....#...#...#.#.#...#
            #.#########.###.#.#.###
            #...###...#...#...#.###
            ###.###.#.###v#####v###
            #...#...#.#.>.>.#.>.###
            #.###.###.#.###.#.#v###
            #.....###...###...#...#
            #####################.#""";

    static final String INPUT = """
            #.###########################################################################################################################################
            #.###.....#.........###...#...#...#####...#...#.......#...###...#.............#.......#.....#...#...#...#...#.............###...#...#...#...#
            #.###.###.#.#######.###.#.#.#.#.#.#####.#.#.#.#.#####.#.#.###.#.#.###########.#.#####.#.###.#.#.#.#.#.#.#.#.#.###########.###.#.#.#.#.#.#.#.#
            #.#...#...#.#.......#...#...#...#...#...#...#...#.....#.#.###.#.#.........#...#.#.....#...#.#.#.#.#.#.#.#.#.#...........#.#...#.#.#.#.#.#.#.#
            #.#.###.###.#.#######.#############.#.###########.#####.#.###.#.#########.#.###.#.#######.#.#.#.#.#.#.#.#.#.###########.#.#.###.#.#.#.#.#.#.#
            #.#.#...#...#...#...#...#...........#...........#.###...#.#...#.>.>.#...#.#...#.#...>.>.#.#.#.#.#.#.#.#.#.#.#...#...###.#.#.#...#.#.#.#...#.#
            #.#.#.###.#####.#.#.###.#.#####################.#.###.###.#.#####v#.#.#.#.###.#.#####v#.#.#.#.#.#.#.#.#.#.#.#.#.#.#.###.#.#.#.###.#.#.#####.#
            #...#...#.#.....#.#.###.#.#...#.......###...###.#.#...###...#.....#.#.#.#...#...###...#...#...#.#.#.#.#...#.#.#.#.#.#...#.#.#...#.#...#...#.#
            #######.#.#.#####.#.###.#.#.#.#.#####.###.#.###.#.#.#########.#####.#.#.###.#######.###########.#.#.#.#####.#.#.#.#.#.###.#.###.#.#####.#.#.#
            #.......#.#.###...#...#.#.#.#.#.....#.#...#...#.#.#.......###.#...#.#.#...#.#.....#.........#...#.#.#.#.....#.#.#.#.#.#...#...#...###...#.#.#
            #.#######.#.###.#####.#.#.#.#.#####.#.#.#####.#.#.#######.###.#.#.#.#.###.#.#.###.#########.#.###.#.#.#.#####.#.#.#.#.#.#####.#######.###.#.#
            #.#.....#.#.....#.....#.#.#.#.#.....#.#.....#.#.#.#.....#...#...#.#.#...#...#...#.#.......#.#.#...#...#.....#.#...#.#.#.......#...#...###...#
            #.#.###.#.#######.#####.#.#.#.#.#####.#####.#.#.#.#.###.###.#####.#.###.#######.#.#.#####.#.#.#.###########.#.#####.#.#########.#.#.#########
            #...###...#.......#...#.#.#.#.#.#.....###...#.#.#...###.....#...#.#.....#...#...#.#.....#.#.#.#...#.........#.....#...#.......#.#...###.....#
            ###########v#######.#.#.#.#.#.#.#.#######.###.#.#############.#.#.#######.#.#.###.#####.#.#.#.###.#.#############.#####.#####.#.#######.###.#
            ###...#...#.>.#.....#.#.#.#.#.#.#.>.>.#...#...#...#...........#...###.....#.#...#.#...#.#.#.#.#...#.....#.>.>.....#...#.....#.#.........#...#
            ###.#.#.#.#v#.#.#####.#.#.#.#.#.###v#.#.###.#####.#.#################.#####.###.#.#.#.#.#.#.#.#.#######.#.#v#######.#.#####.#.###########.###
            #...#...#...#.#...#...#.#.#.#.#.#...#...###...#...#...#...#.........#.#...#.....#...#...#...#...###...#.#.#.........#...#...#.............###
            #.###########.###.#.###.#.#.#.#.#.###########.#.#####.#.#.#.#######.#.#.#.#########################.#.#.#.#############.#.###################
            #...#.......#.#...#.....#.#.#...#.#...#...###...#...#...#...#.......#...#.............#...#.........#.#...#...#.....#...#...#.....#.........#
            ###.#.#####.#.#.#########.#.#####.#.#.#.#.#######.#.#########.#######################.#.#.#.#########.#####.#.#.###.#.#####.#.###.#.#######.#
            #...#.#.....#...###...###...###...#.#...#.........#...#.......#...###.................#.#.#.........#.......#.#...#...#####...###...#...#...#
            #.###.#.###########.#.#########.###.#################.#.#######.#.###.#################.#.#########.#########.###.###################.#.#.###
            #.#...#.#...#...#...#.........#.....###...........#...#.........#...#.....#...#...###...#.#...#...#.........#.#...#.....#...#...#...#.#...###
            #.#.###.#.#.#.#.#.###########.#########.#########.#.###############.#####.#.#.#.#.###.###.#.#.#.#.#########.#.#.###.###.#.#.#.#.#.#.#.#######
            #...###.#.#.#.#...#...........#.........#.....###...###.............#...#...#...#...#.#...#.#.#.#.###...#...#.#...#...#.#.#.#.#.#.#.#.#...###
            #######.#.#.#.#####.###########.#########.###.#########.#############.#.###########.#.#.###.#.#.#.###.#.#.###.###.###.#.#.#.#.#.#.#.#.#.#.###
            #.......#.#.#.....#...#.....###...........#...#...#...#.........#...#.#...###.......#.#.###.#.#.#.#...#...###.....###.#...#...#...#...#.#...#
            #.#######.#.#####.###.#.###.###############.###.#.#.#.#########.#.#.#.###.###.#######.#.###.#.#.#.#v#################.#################.###.#
            #.......#.#.#.....#...#.#...#...#...#...#...#...#.#.#...#.......#.#.#...#.....###...#.#.#...#...#.>.>.#...###...###...#...#.............#...#
            #######.#.#.#.#####v###.#.###.#.#.#.#.#.#v###.###.#.###.#.#######.#.###.#########.#.#.#.#.#########v#.#.#.###.#.###.###.#.#.#############.###
            #.....#...#...#...#.>.#.#.#...#.#.#.#.#.>.>.#...#.#.#...#.....###.#...#.......###.#.#.#.#...#.......#...#.....#...#.....#.#.#.............###
            #.###.#########.#.#v#.#.#.#.###.#.#.#.###v#.###.#.#.#.#######v###.###.#######.###.#.#.#.###.#.###################.#######.#.#.###############
            #...#.#...#...#.#.#.#.#.#.#...#.#.#...#...#...#.#.#.#...#...>.>.#.#...#...###.#...#.#.#.#...#.#.....###...........#.....#...#...........#####
            ###.#.#.#.#.#.#.#.#.#.#.#.###.#.#.#####.#####.#.#.#.###.#.###v#.#.#.###.#.###v#.###.#.#.#.###.#.###.###.###########.###.###############.#####
            #...#.#.#.#.#...#...#...#.#...#.#.###...#####...#.#...#.#.###.#.#.#...#.#.#.>.>.###.#.#.#...#...###...#...###...###...#.#.............#.....#
            #.###.#.#.#.#############.#.###.#.###.###########.###.#.#.###.#.#.###.#.#.#.#v#####.#.#.###.#########.###.###.#.#####.#.#.###########.#####.#
            #...#.#.#.#...#.........#.#.#...#...#.......#.....#...#...#...#...###...#...#.....#.#.#.#...###.......###...#.#...#...#.#.........###.......#
            ###.#.#.#.###.#.#######.#.#.#.#####.#######.#.#####.#######.#####################.#.#.#.#.#####.###########.#.###.#.###.#########.###########
            #...#...#.###...#...#...#...#.....#.#.......#...#...#.....#.........#.............#...#.#.#.....#...#...###...#...#...#.#...#...#.....#######
            #.#######.#######.#.#.###########.#.#.#########.#.###.###.#########.#.#################.#.#.#####.#.#.#.#######.#####.#.#.#.#.#.#####.#######
            #...#...#.#...#...#.#...#...#...#...#.........#.#.###...#.......#...#.........#.......#...#.......#...#.....###.#.....#.#.#.#.#.#...#.......#
            ###.#.#.#.#.#.#.###.###.#.#.#.#.#############.#.#.#####.#######.#.###########.#.#####.#####################.###.#.#####.#.#.#.#.#v#.#######.#
            #...#.#.#...#...###.....#.#...#.............#.#.#.#.....#.....#...#...#.......#.#.....###...............#...#...#.....#.#.#...#.>.#.#.......#
            #.###.#.#################.#################.#.#.#.#.#####.###.#####.#.#.#######.#.#######.#############.#.###.#######.#.#.#######v#.#.#######
            #.....#.................#...#.....#.......#.#.#.#.#.#.....###.......#.#.........#.......#.............#.#.###...#...#.#.#.#.......#...###...#
            #######################.###.#.###.#.#####.#.#.#.#.#.#.###############.#################.#############.#.#.#####.#.#.#.#.#.#.#############.#.#
            #.......................###...#...#.#.....#.#.#.#.#.#.#...............#...........#.....#.............#...#.....#.#.#.#.#.#...#...#.......#.#
            #.#############################.###.#.#####.#.#.#.#.#.#.###############.#########.#.#####.#################.#####.#.#.#.#.###.#.#.#.#######.#
            #...........................#...#...#.....#...#.#.#...#...............#.........#.#.....#.#...............#.....#.#.#.#.#.###...#...#.......#
            ###########################.#.###.#######.#####.#.###################.#########.#.#####.#.#.#############.#####.#.#.#.#.#.###########.#######
            #...#.....#.....#...#.....#.#.....#...#...#...#...###...#...#.........###...#...#...#...#...#.............#.....#.#...#...#.........#.......#
            #.#.#.###.#.###.#.#.#.###.#.#######.#.#.###.#.#######.#.#.#.#.###########.#.#.#####.#.#######.#############.#####.#########.#######.#######.#
            #.#.#...#.#...#...#.#...#.#.#.......#...#...#.#.....#.#...#.#.....#...#...#...#...#...###...#.............#.......#.....#...#.....#...#.....#
            #.#.###.#.###.#####.###.#.#.#.###########.###.#.###.#.#####.#####.#.#.#.#######.#.#######.#.#############.#########.###.#.###.###.###.#.#####
            #.#.....#...#.....#.....#...#.............#...#...#.#...#...###...#.#.#.........#...#.....#...#...........#...#...#...#.#.....###...#...#...#
            #.#########.#####.#########################.#####.#.###.#.#####.###.#.#############.#.#######.#.###########.#.#.#.###.#.###########.#####.#.#
            #.........#.#.....#...#...#...#.............#...#.#.#...#.#...#.....#.#...###.......#...#.....#.......#...#.#.#.#.#...#.#...###...#.....#.#.#
            #########.#.#.#####.#.#.#.#.#.#.#############.#.#.#.#.###.#.#.#######.#.#.###.#########.#.###########.#.#.#.#.#.#.#.###.#.#.###.#.#####.#.#.#
            #.....#...#.#.....#.#.#.#.#.#.#.........#...#.#.#.#.#...#.#.#.#.......#.#.#...#...#...#.#.#...#...#...#.#.#.#.#.#.#...#.#.#.###.#.#.....#.#.#
            #.###.#.###.#####.#.#.#.#.#.#.#########.#.#.#.#.#.#.###.#.#.#.#v#######.#.#.###.#.#.#.#.#.#.#.#.#.#v###.#.#.#.#.#.###.#.#.#.###.#.#v#####.#.#
            #...#...###.......#.#.#.#.#.#.#...#.....#.#...#.#.#.#...#.#.#.>.>.....#.#.#...#.#...#.#.#.#.#...#.>.>...#.#.#.#.#.#...#.#.#...#.#.>.#.....#.#
            ###.###############.#.#.#.#.#.#.#.#.#####.#####.#.#.#.###.#.###v#####.#.#.###.#.#####.#.#.#.#######v#####.#.#.#.#.#.###.#.###.#.###v#.#####.#
            #...#...###.....#...#...#.#.#.#.#...#...#.....#.#.#.#.#...#...#...###...#...#.#...#...#.#.#...#...#.....#.#.#...#.#.###.#.#...#.#...#.#.....#
            #.###.#v###.###.#.#######.#.#.#.#####.#.#####.#.#.#.#.#.#####.###.#########.#v###.#.###.#.###.#.#.#####.#.#.#####.#.###.#.#.###.#.###.#.#####
            #.....#.>.#.#...#.....#...#.#.#.......#.###...#.#.#.#.#...#...###...#...###.>.>...#.....#.#...#.#.......#.#.#.....#.#...#.#.....#...#.#.#...#
            #######v#.#.#.#######.#.###.#.#########v###.###.#.#.#.###.#.#######.#.#.#####v###########.#.###.#########.#.#.#####.#.###.#########.#.#.#.#.#
            #.......#...#.#...#...#...#.#...#...#.>.>.#...#...#...#...#...#...#...#.#...#.........###...###.....#...#...#.....#.#...#.###...###...#...#.#
            #.###########.#.#.#.#####.#.###.#.#.#.#v#.###.#########.#####.#.#.#####.#.#.#########.#############.#.#.#########.#.###.#.###.#.###########.#
            #.....#...###...#...###...#...#.#.#...#.#...#.........#.......#.#.#.....#.#...........#.......#.....#.#.........#.#.#...#.#...#.#...###...#.#
            #####.#.#.#############.#####.#.#.#####.###.#########.#########.#.#.#####.#############.#####.#.#####.#########.#.#.#.###.#.###.#.#.###.#.#.#
            #.....#.#.............#.......#...#...#...#.#.....#...#...#...#.#.#.....#.....#.....#...#.....#.......#.......#.#...#.....#...#...#.....#...#
            #.#####.#############.#############.#.###.#.#.###.#.###.#.#.#.#.#.#####.#####.#.###.#.###.#############.#####.#.#############.###############
            #.....#.#...........#.#.....###.....#.....#.#...#...#...#...#...#.....#.###...#...#.#...#.#.....###.....#...#...#.....#.......#.............#
            #####.#.#.#########.#.#.###.###.###########.###.#####.###############.#.###v#####.#.###.#.#.###.###.#####.#.#####.###.#.#######.###########.#
            #.....#.#...###...#...#...#...#...........#...#.#...#...#...#...#...#...#.>.>.....#.#...#.#...#...#.......#.#...#.#...#.....#...#...###...#.#
            #.#####.###.###.#.#######.###.###########.###.#.#.#.###.#.#.#.#.#.#.#####.#v#######.#.###.###.###.#########.#.#.#.#.#######.#.###.#.###.#.#.#
            #.......###...#.#...###...#...#...........###...#.#...#...#...#...#.#.....#.#.......#...#...#.#...###.......#.#.#.#.#...###...#...#.....#.#.#
            #############.#.###v###.###.###.#################.###.#############.#.#####.#.#########.###.#.#.#####.#######.#.#.#.#.#.#######.#########.#.#
            ###...#######...###.>...#...###...#.......#...###.#...#.......#...#.#.....#.#...#.......#...#.#.#...#...#...#.#.#.#.#.#.#...###.........#.#.#
            ###.#.#############v#####.#######.#.#####.#.#.###.#.###.#####.#.#.#.#####.#.###.#.#######.###.#.#.#.###v#.#.#.#.#.#.#.#.#.#.###########.#.#.#
            #...#...#.........#.....#...#...#...#...#.#.#...#.#...#.....#...#...#.....#...#.#.....#...#...#.#.#...>.>.#.#.#.#.#.#.#.#.#.###.........#...#
            #.#####.#.#######.#####.###.#.#.#####.#.#.#.###.#.###.#####.#########.#######.#.#####.#.###.###.#.#####v###.#.#.#.#.#.#.#.#.###.#############
            #.....#.#.......#.......###...#.#.....#.#.#.#...#.#...#...#.#.......#.....#...#...#...#...#...#...###...###...#...#.#.#.#.#...#.............#
            #####.#.#######.###############.#.#####.#.#.#.###.#.###.#.#.#.#####.#####.#.#####.#.#####.###.#######.#############.#.#.#.###.#############.#
            #.....#...#...#.#.......#...#...#.....#...#.#.#...#...#.#.#...#...#...#...#.#...#.#.#...#.#...###.....###...###.....#.#.#.#...#...#...#...#.#
            #.#######.#.#.#.#.#####.#.#.#.#######.#####.#.#.#####.#.#.#####.#.###.#.###.#.#.#.#.#.#.#.#.#####.#######.#.###.#####.#.#.#.###.#.#.#.#v#.#.#
            #.#.....#...#...#.#.....#.#...#...###.#...#.#.#...#...#.#.#...#.#.#...#...#...#.#...#.#.#...#...#.......#.#.....#...#.#.#.#...#.#...#.>.#.#.#
            #.#.###.#########.#.#####.#####.#.###v#.#.#.#.###.#.###.#.#.#.#.#.#v#####.#####.#####.#.#####.#.#######.#.#######.#.#.#.#.###.#.#######v#.#.#
            #.#.#...#...#.....#...#...#...#.#...>.>.#.#.#.#...#.#...#.#.#.#.#.>.>.....#.....#.....#.#.....#.........#.......#.#.#.#.#...#.#.###...#.#...#
            #.#.#.###.#.#.#######.#.###.#.#.#####v###.#.#.#.###.#.###.#.#.#.###v#######.#####.#####.#.#####################.#.#.#.#.###.#.#.###.#.#.#####
            #...#.....#...#.......#.....#.#.#.....###.#.#.#...#.#.#...#.#.#...#.......#.....#...#...#.#...#...#...#.....###...#.#.#...#.#...#...#.#.....#
            ###############.#############.#.#.#######.#.#.###.#.#.#.###.#.###.#######.#####.###.#.###.#.#.#.#.#.#.#.###.#######.#.###.#.#####.###.#####.#
            #.............#.....#...#...#...#.......#.#.#.#...#...#...#.#.#...#.....#.#...#.....#...#...#.#.#.#.#...#...#.....#...###.#.#...#...#.#...#.#
            #.###########.#####.#.#.#.#.###########.#.#.#.#.#########.#.#.#.###.###.#.#.#.#########.#####.#.#.#.#####.###.###.#######.#.#.#.###.#.#.#.#.#
            #.#.........#.......#.#.#.#.#...#...#...#.#.#.#...#.......#.#...#...###.#.#.#...........#...#...#...#...#...#...#.###...#...#.#.#...#.#.#...#
            #.#.#######.#########.#.#.#.#.#.#.#.#.###.#.#.###.#.#######.#####.#####.#.#.#############.#.#########.#.###.###.#.###.#.#####.#.#.###.#.#####
            #...#...#...#...#...#.#.#.#.#.#...#...###...#.....#...#...#.#...#.....#.#.#.#.............#.#.....#...#.....#...#...#.#...#...#.#...#...#...#
            #####.#.#.###.#.#.#v#.#.#.#.#v#######################.#.#.#.#.#.#####.#.#.#.#.#############.#.###.#.#########.#####.#.###.#.###.###.#####.#.#
            #.....#...#...#...#.>.#...#.>.>.......#...#.........#.#.#...#.#.......#.#.#...#...#.........#...#.#...#.....#.....#.#.#...#...#...#.......#.#
            #.#########.#######v#########v#######.#.#.#.#######.#.#.#####.#########.#.#####.#.#.###########.#.###.#.###.#####.#.#.#.#####v###.#########.#
            #...........###.....#...#.....#.......#.#.#.......#.#...###...#.....###.#.#.....#...#...#.......#.....#...#.#...#.#.#.#...#.>.###.#.......#.#
            ###############.#####.#.#.#####.#######.#.#######.#.#######.###.###.###.#.#.#########.#.#.###############.#.#.#.#.#.#.###.#.#v###.#.#####.#.#
            #.....#...#.....#.....#.#.....#...#...#.#.#.......#...#...#.....###...#...#.........#.#.#.#.....#.....###.#.#.#.#.#.#.###...#...#.#.....#...#
            #.###.#.#.#.#####.#####.#####.###.#.#.#.#.#.#########.#.#.###########.#############.#.#.#.#.###.#.###.###.#.#.#.#.#.#.#########.#.#####.#####
            #...#.#.#.#.......#...#.#.....#...#.#.#.#.#.....#.....#.#...#...#.....#...#.........#.#.#...###...#...#...#.#.#.#.#.#.#.........#.#...#.....#
            ###.#.#.#.#########.#.#.#.#####.###.#.#.#.#####.#.#####.###.#.#.#.#####.#.#v#########.#.###########.###.###.#.#.#.#.#.#.#########.#.#.#####.#
            #...#...#.#.....#...#...#.....#...#.#.#.#.#...#.#.#...#.#...#.#.#...#...#.>.>.#...###.#.#...#...###...#...#.#.#.#.#.#.#.....#...#...#.......#
            #.#######.#.###.#.###########.###.#.#.#.#.#.#.#.#.#.#.#.#.###.#.###v#.#####v#.#.#.###.#.#.#.#.#.#####v###.#.#.#.#.#.#.#####.#.#.#############
            #.......#.#...#.#.......#.....###...#...#.#.#.#.#.#.#.#.#...#.#.#.>.>.###...#...#...#.#...#.#.#.#...>.>...#...#...#...###...#.#...###...#####
            #######.#.###.#.#######.#.###############.#.#.#.#.#.#.#.###.#.#.#.#v#####.#########.#.#####.#.#.#.###v###################.###.###.###.#.#####
            #.......#.....#.........#.....#...#...###...#.#.#...#...#...#.#...#.....#.......#...#.....#.#.#...###.....#...###...#...#...#.###.....#.....#
            #.###########################.#.#.#.#.#######.#.#########.###.#########.#######.#.#######.#.#.###########.#.#.###.#.#.#.###.#.#############.#
            #...................#...###...#.#.#.#.#.....#.#.#.........#...#.........#.......#.....#...#.#.#...........#.#.#...#...#.###...#.............#
            ###################.#.#.###.###.#.#.#.#.###.#.#.#.#########.###.#########.###########.#.###.#.#.###########.#.#.#######.#######.#############
            #.........#.........#.#...#...#.#.#.#.#.#...#...#.......#...###.........#.........###...#...#.#...#...#.....#.#.#...#...#.....#.........#...#
            #.#######.#.#########.###.###.#.#.#.#.#.#.#############.#.#############.#########.#######.###.###.#.#.#.#####.#.#.#.#.###.###.#########.#.#.#
            #.......#...#####...#.#...#...#.#...#.#.#.#...#...#.....#...#...#...#...#.........#.....#.....###...#...#.....#.#.#.#...#...#...........#.#.#
            #######.#########.#.#.#.###.###.#####.#.#.#.#.#.#.#.#######.#.#.#.#.#.###.#########.###.#################.#####.#.#.###.###.#############.#.#
            #...###.........#.#...#...#.....#...#...#...#...#.#...#...#.#.#...#...###.........#.#...#...###...#...#...#...#.#.#.....###...........###.#.#
            #.#.###########.#.#######.#######.#.#############.###.#.#.#.#.###################.#.#.###.#.###.#.#.#.#.###.#.#.#.###################.###.#.#
            #.#.............#.......#.....###.#.#.............#...#.#.#.#.............###...#...#...#.#.....#...#.#...#.#...#...#.....#...###.....#...#.#
            #.#####################.#####.###.#.#.#############.###.#.#.#############.###.#.#######.#.###########.###.#.#######.#.###.#.#.###v#####.###.#
            #...#.......#######...#.#.....#...#.#.............#...#.#...#...........#.#...#...#.....#...........#.#...#.#.......#.#...#.#.#.>.#...#.#...#
            ###.#.#####.#######.#.#.#.#####.###.#############.###.#.#####.#########.#.#.#####.#.###############.#.#.###.#.#######.#.###.#.#.#v#.#.#.#.###
            ###...#...#.....#...#.#.#.#...#.#...#...#.........###...#...#.........#...#.....#...###...#...#...#.#.#.....#.....###.#.#...#.#.#.#.#...#...#
            #######.#.#####.#.###.#.#.#.#.#.#.###.#.#v###############.#.#########.#########.#######.#.#.#.#.#.#.#.###########v###.#.#.###.#.#.#.#######.#
            #...#...#.......#...#.#.#...#.#.#...#.#.>.>.###...#...#...#.###.......###.......#...###.#...#.#.#.#.#.#...#...#.>.>...#.#...#.#.#.#.#.......#
            #.#.#.#############.#.#.#####.#.###.#.#####.###.#.#.#.#.###.###.#########.#######.#.###.#####.#.#.#.#.#.#.#.#.#.#######.###.#.#.#.#.#.#######
            #.#.#.........#...#.#.#.....#.#.###...#####...#.#.#.#.#...#...#.....#...#.....###.#...#.....#.#.#...#...#.#.#.#.#.......#...#.#.#.#.#.#.....#
            #.#.#########.#.#.#.#.#####.#.#.#############.#.#.#.#.###.###.#####.#.#.#####.###.###.#####.#.#.#########.#.#.#.#.#######.###.#.#.#.#.#.###.#
            #.#...........#.#...#.......#.#...#...###.....#.#.#.#.###.#...#.....#.#...#...#...#...#...#.#.#.........#.#.#.#.#...#...#.###...#...#...###.#
            #.#############.#############.###.#.#.###.#####.#.#.#.###.#.###.#####.###.#.###.###.###.#.#.#.#########.#.#.#.#.###.#.#.#.#################.#
            #.#...#...#...#...........###.....#.#.....#...#.#...#.#...#...#.....#.#...#...#...#...#.#.#.#...#...#...#.#.#.#.###.#.#.#.......###.........#
            #.#.#.#.#.#.#.###########.#########.#######.#.#.#####.#.#####.#####.#.#.#####v###.###.#.#.#.###.#.#.#.###.#.#.#.###.#.#.#######.###.#########
            #.#.#...#.#.#...#.......#.....#.....#.....#.#.#.#.....#...#...###...#.#...#.>.>...#...#.#.#...#.#.#...###...#.#...#...#.#.......#...#.......#
            #.#.#####.#.###.#.#####.#####.#.#####.###.#.#.#.#.#######.#.#####v###.###.#.#######.###.#.###.#.#.###########.###.#####.#.#######.###.#####.#
            #.#.#.....#...#.#.....#.#...#.#.#...#...#.#.#.#.#.#...#...#.#...>.>.#.###.#.....#...#...#.#...#.#...#.........#...#.....#.......#.#...#.....#
            #.#.#.#######.#.#####.#.#.#.#.#.#.#.###.#.#.#.#.#.#.#.#.###.#.#####.#.###.#####.#.###.###.#.###.###.#.#########.###.###########.#.#.###.#####
            #...#.........#.......#...#...#...#.....#...#...#...#...###...#####...###.......#.....###...###.....#...........###.............#...###.....#
            ###########################################################################################################################################.#""";
}
