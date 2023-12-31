import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Day12 {
    public static void main(String... args) {
        for (String input : List.of(TEST_INPUT, INPUT)) {
            var lines = input.lines().map(Day12::parse).toList();
            System.out.println(lines.stream().mapToLong(parsed -> count(parsed.bookEnd(), new HashMap<>())).sum());
            System.out.println(lines.stream().mapToLong(parsed -> count(parsed.unfold().bookEnd(), new HashMap<>())).sum());
        }
    }

    static Line parse(String line) {
        String[] parts = line.split(" ");

        List<Spring> springs = parts[0]
                .chars()
                .mapToObj(c -> c == '.' ? Spring.OPERATIONAL : c == '#' ? Spring.DAMAGED : Spring.UNKNOWN)
                .toList();

        List<Integer> lengths = Stream.of(parts[1].split(","))
                                      .map(Integer::parseInt)
                                      .toList();

        return new Line(springs, lengths);
    }

    static long count(Line line, Map<Line, Long> cache) {
        if (cache.containsKey(line)) {
            return cache.get(line);
        }

        long total = 0;

        if (line.lengths().isEmpty()) {
            total = line.springs().stream().anyMatch(s -> s == Spring.DAMAGED) ? 0 : 1;
        } else {
            int midLength = line.lengths().get(line.mid());
            int end = line.springs().size() - line.minRightLength() - midLength;
            int start = line.minLeftLength() + 1;

            for (int pos = start; pos < end; pos++) {
                if (fits(line.springs(), pos, midLength)) {
                    var left = count(line.left(pos), cache);

                    if (left != 0) {
                        var right = count(line.right(pos + midLength), cache);
                        total += left * right;
                    }
                }
            }
        }

        cache.put(line, total);
        return total;
    }

    static boolean fits(List<Spring> springs, int i, int size) {
        boolean left = (i == 0) || springs.get(i - 1) != Spring.DAMAGED;
        boolean right = (i + size == springs.size()) || springs.get(i + size) != Spring.DAMAGED;
        boolean mid = springs.subList(i, i + size).stream().noneMatch(s -> s == Spring.OPERATIONAL);
        return mid && left && right;
    }

    enum Spring {OPERATIONAL, DAMAGED, UNKNOWN}

    record Line(List<Spring> springs, List<Integer> lengths, int mid) {
        Line(List<Spring> springs, List<Integer> lengths) {
            this(springs, lengths, lengths.size() / 2);
        }

        Line unfold() {
            var springs5 = new ArrayList<Spring>();
            var lengths5 = new ArrayList<Integer>();

            for (int i = 0; i < 5; i++) {
                springs5.addAll(springs);
                lengths5.addAll(lengths);

                if (i < 4) {
                    springs5.add(Spring.UNKNOWN);
                }
            }

            return new Line(springs5, lengths5);
        }

        Line bookEnd() {
            List<Spring> newLine = new ArrayList<>(springs);
            newLine.add(0, Spring.OPERATIONAL);
            newLine.add(Spring.OPERATIONAL);
            return new Line(newLine, lengths);
        }

        Line left(int pos) {
            return new Line(springs.subList(0, pos), lengths.subList(0, mid));
        }

        Line right(int pos) {
            return new Line(springs.subList(pos, springs.size()), lengths.subList(mid + 1, lengths.size()));
        }

        int minLeftLength() {
            return lengths.stream().limit(mid).mapToInt(i -> i).sum();
        }

        int minRightLength() {
            return lengths.stream().skip(mid + 1).mapToInt(i -> i).sum();
        }
    }

    private static final String TEST_INPUT = """
            ???.### 1,1,3
            .??..??...?##. 1,1,3
            ?#?#?#?#?#?#?#? 1,3,1,6
            ????.#...#... 4,1,1
            ????.######..#####. 1,6,5
            ?###???????? 3,2,1""";

    private static final String INPUT = """
            ??.???.#?? 1,1,2
            .?.#??.?##?? 3,4
            ??..###??##??.?.??? 1,9,2
            ???????#???#?? 1,3,3,3
            ??????????..?.?? 3,4,1
            #??.?####.?????? 1,1,5,2,1
            ?####???#.? 5,1,1
            ???###??.?#?? 8,3
            ????##?..?.? 5,1
            ??#????#?.?. 1,1,3,1
            ?#?????.??????. 4,1
            #..?.?#?#????#? 1,1,1,1,2
            ??##??.?????#?#. 6,1,1,2,1
            ????###?.?? 2,4,2
            ???#?..?.??#? 1,1,1,4
            .????#?..?? 1,2,2
            ?.???####?????# 1,10
            .?????#??#????# 1,3,2,2
            ?#???##?#???. 3,5
            ?????##### 1,5
            #..??????#.??? 1,2,2,1
            ??????.?.??#? 4,1,1,3
            #.?#???.?#??????? 1,5,1,2,3
            ?#??#??#????????? 1,5,1,1,1
            .??.#??.??? 1,1,2
            ?.??#?#????.?? 1,7,1
            ???#????#?????? 1,2,3,1,2
            ???#????#?????#?## 1,2,4,1,5
            .?##?##??.??????#??# 7,2,1,2,2
            ??##.?.#?.???? 4,1,1,2
            ??#?????????? 2,4
            ??????.???##?????? 1,4,3,1,2
            ?????.##?#??#?##.? 1,1,10
            ??????.?##?.?? 1,1,4,2
            ?????.?#??#.??? 1,1,5,1,1
            #??###?.??..?? 6,1,1
            ?....??.???.?? 1,2,3,1
            ???????#??##????? 1,4,6
            ??.?.#.?.??#????? 1,1,1,7
            ?????.?????. 1,3,2,1
            ???.?????##? 1,1,1,3
            ?#?.??##???.???? 2,4,3
            ?.??#.#...##??# 1,1,1,2,1
            .?#??.????##??????#? 1,1,3,6
            .#?.#???#???#????.#? 2,1,3,3,1,1
            .?????.?.???#?#?#? 4,1,1,1,3
            .?.???#??#.???.? 1,4,1
            #???.???##????#.# 3,1,2,1,1
            #???..???#?.# 2,1,4,1
            ??.???###??.? 1,4
            .?#.#????.?. 2,1,3,1
            ?????????#?? 5,1
            #?.?##?#?#?#??????.# 1,8,1,1,1
            ???#?????#?#????? 1,7,1,1
            ???????#?#????..??# 8,1,1,1,1
            #?.?????.#??#?. 1,1,1,1,5
            ??.?#????..?.?? 1,5,1,1
            ????##?#???????? 2,6,2
            ?#???????.#???? 7,1,3
            ?##??????? 3,2
            ????#?.????.??? 1,2,1,2
            ?#?#????#.?? 3,1,2,1
            .?.?##??????.??. 3,2
            .##??#??.#? 2,2,2
            .??????#?? 3,2
            ?????????#?..??. 2,1,1,1
            ???##.??????#?##?? 1,2,3,6
            ?#????#??##??#?.??? 1,12,2
            .??#?.??#???..??. 3,6,1
            #?#.??????#???????#? 1,1,6,2,1
            ?#??.???#??# 2,1,1,1
            .#????.??#.??#. 3,1,1,1,1
            ???.???#.?#?..?### 2,3,2,4
            #??#??#.???#?#?..? 1,3,1,7,1
            ??.#.??#??#??????? 1,1,3,4,1,1
            ???#??#?.???#? 4,1,1,2
            ?..#?.#?#????????#?? 1,1,1,11
            ##????.??.?????#??# 2,1,1,9
            ??.?????#?##??.? 2,7
            ?##??.??????????? 4,1,1,6
            ??.???.?..#? 1,1,1,2
            ????.#????#?# 1,1,2,5
            ??#??.??##?.#? 2,4,1
            .???##?.#???# 5,5
            ??.?..??.... 2,1,2
            ?..??#??#???.?#??? 7,4
            #.?#?#.##????.#?## 1,1,1,3,1,4
            ????#?.????????.?. 6,3,1,1,1
            .?????????##?? 6,5
            ??#??????#?. 5,2
            ..???.??.??????.? 3,3
            ???.???#?.?? 1,4
            .#????#??? 1,4,1
            ?.#???##????.??????. 8,3
            .?##?#?#???.#?????? 7,1,1,4
            .#?#??.???#? 5,2
            ..??#??????.?? 4,2,1
            ?.????#?????### 1,3,1,4
            .???.?.?..##?????? 2,1,1,8
            ???#?#????#?##? 5,7
            ??#??##?#..??#???? 3,4,5
            ???###?#???????? 1,6,6
            ?#????????. 2,3
            ?#????#??#?.??#???? 3,2,1,5,1
            ??????#??#. 1,4,1
            ..?.???###??#?##. 1,11
            ?.#..?????#??#??#??? 1,7,3
            ????#####?#??##??.?? 1,14
            ?.????.?#? 2,2
            ?????#????.?# 8,2
            ???.?##???##.?#?.?? 1,8,2,1
            ??..#????#?.??# 1,4,2,1
            ???##?#?#?? 1,7
            ???#????????#? 7,5
            ????#.??#???..? 3,6,1
            ??#???.????##? 1,1,3,2
            ##..#.??#?.??.#? 2,1,1,1,2
            .??.#?#?????#? 1,1,5,1
            .?.?#????.? 1,2,1
            #?#???#????#?#?????? 7,1,1,2,1,2
            .?####???? 5,1
            ?????????##. 3,3
            ???.??#????..#? 3,3,1,1
            ???##??.?.#?#?. 1,3,4
            ???????#??? 1,5
            ???.??.??#??? 1,2,3
            #?.?.##????.?.??? 2,5,1
            ?#????##????????.## 8,1,1,2
            ???###????.?? 2,3,1,1
            ??#?##??#? 6,2
            .#??.??.??#?.#? 3,1,2,1
            ???.??????????????? 1,5,1,2,1
            .???.???#. 2,2
            .?#??#.?????? 2,1,2
            ??...###??? 1,4
            ???#??#?????????. 1,11
            #??##???.??#??# 6,4,1
            #??#????##??.? 1,3,2
            ??????.#?#????##? 2,9
            ???##?????? 5,1
            ###?#.#?#???????? 3,1,1,2,4
            ?#???#?????#????#?#. 2,1,1,9,1
            ?#??.?#???#.???#???# 2,1,1,3,7
            ???#???????.?#.?.# 4,5,1,1,1
            #????#????.???#????? 1,1,1,3,7,1
            ##?#??????#??####?? 4,1,8,1
            ??????.??. 1,1,1
            ????##???????#. 9,2
            ?#??????????#. 5,3
            ?????##?.#??#?. 1,2,4
            .#??###???.# 6,1,1
            ?????.??##?#?????? 4,1,2,3,1,1
            .???????#?????#?## 7,7
            ????#?#?????# 7,1
            ?.??#.???????.??. 1,1,1,4,1
            ?##??#.?????.? 5,1,2,1
            #?#?.##??# 3,2,1
            ?.???#????#??.??.?# 5,2,1,1,1
            ???.?###??????.? 1,3,3,1
            #??#??????????# 8,1,1
            ????????#??????? 2,8,1
            #...?#????#??##?? 1,2,8,1
            ??#???.#?? 5,1
            ??#?.??#?????? 4,4,1,1
            ???##????#?#?#??? 6,1,1,1,1
            #???#??#?#?.???.?? 1,1,7,1,2
            ??#???..??. 4,1
            ???#???.?#??#???#?#? 2,1,5,3
            ????###?.??##??? 7,7
            ???##???#???.??#? 10,3
            ?..??????.????. 5,3
            ###??.??#?...??? 3,1,3,1
            ??.?#?..???? 1,3,2
            .#?.?#?#???? 2,7
            ?#??#?#.??#.#.?.?#?? 2,3,3,1,1,3
            ?????#??????. 1,5,1
            ?.????.#.. 3,1
            .??#.???#?#?????# 1,1,4,1,1
            ##??.??#??. 4,1,1
            .??.#???????.? 2,1,6,1
            ??#..?.??.?? 2,1,1,1
            #?..???????????????. 1,1,2,1,1,4
            ???#??.#??###???? 2,1,1,6
            ???.?#?????.??#??.? 2,3,2,5
            .?#????#??#.?.???. 6,2,1,1,1
            ?#?????????##??? 5,4
            ??.???..#.???#?? 1,1,1,4,1
            #???.????##..?? 1,1,6,1
            .??????.??? 4,1
            #?.???#??? 1,1,2
            ??.??.????##.#??.? 1,2,6,3,1
            ??#?#?????##???????# 8,5,4
            .?#???.??????.?.? 5,1
            ?##..??#??#?.? 3,6
            ?###??.?.????.# 4,3,1
            ???.???#.??# 2,1,2
            ???##??.?? 1,3,1
            ??#??#????#???#. 9,1,1,1
            ??????.??. 3,1
            ??#?.???#?##? 1,1,6
            ????.?????#???? 3,9
            ###?#??????##? 6,6
            ##???.?.?#??#? 5,2,1
            ??????#?#??? 3,4,2
            ?##?#.??#?????#?? 5,1,2,1,2
            ??##?.??????.??#???. 5,2,3
            ???##?#?????.? 4,1,4,1
            .???#.??????? 3,1,2
            ???????.##??#????#? 3,10
            .????.????. 2,1
            ?#??#?????.#?????? 5,1,1,2,1,1
            ??..????.??.?? 1,3,1,1
            ?#???????# 1,2,1
            ???#?????#?.?#?#??.? 8,6
            ?#.#...?###?????# 1,1,7,2
            ?#.??#?.???. 1,2,2
            ???.#..?.##??. 1,4
            ?#?#..??#????????#? 3,2,1,2,4
            ??????#?##???#??.? 3,1,8
            ###????.??? 5,2
            ?.??.??#.##.?????? 1,1,3,2,1,1
            ?##??????.???.. 6,3
            ?.??.#??.??.#??? 3,2
            #??????..?.#?#?#?? 1,2,1,1,5
            ?.??.????? 1,5
            ????##..????.??... 3,1
            ??##?#?..? 2,2,1
            ??????#????????. 1,2,1,1,2
            ??????#????#??##?? 2,1,12
            #?.????#??####?..# 2,10,1
            ??#??????#??? 5,1
            ??#.#???#?#??.???.? 1,1,9,2,1
            ???????.#? 5,1
            ???###?##???.#???#.. 1,7,5
            ??.???#????????. 2,1,1,1,2
            ?##?????##?.#??? 2,2,3,1,1
            ??#?????????#?#? 4,8
            #?#???#???.? 4,2,1,1
            #??.????#.#???. 3,2,1,1,1
            ???###???.? 5,1
            ?.?#?????#????# 5,3,3
            ?.???.????????? 1,1,2,3
            .??#?#.?#..????#. 1,3,1,1,2
            ????..?#.?.? 4,2
            ???.????.#???.? 1,1,1,2,1
            ??.??????. 1,1,1
            #??.?#.?????#??????? 1,1,1,6,1,1
            ????#?#????##??? 1,5,6
            #.?.?.?#??? 1,2,1
            ????#?#??.??##.?? 1,1,2,2,1
            ????##??##?#??????? 1,15
            ?.???.??????#?#?? 1,6
            ..????????#??#??.?# 1,1,1,1,2,2
            ????#???????.??.. 7,1,1,1
            ?.?.?.#??? 1,1,2
            .?#???????. 5,1
            ?#?.#?.#??.?##.?# 1,1,3,3,1
            ?#?#..??## 4,4
            .?#??#?????#?..? 3,3,3
            ?.?#??.??????????? 2,8
            ??#?#?#??????#?#.?. 7,1,6
            .??#??#??##?#? 1,1,7
            ??????..???? 1,1,1,4
            ?##.?.?##?#??#.??# 3,1,7,3
            ??.#?????????.? 6,1
            .??#?.??#???? 4,3,1
            ??######???.?? 9,1
            ??#?????.. 1,1,1
            .??????.??.???#.?? 5,2,1,1,1
            ??#?##???###??#????? 1,1,9,1,1,1
            ???.###??? 1,6
            .?????.#??.?# 1,2,2,1
            .?#..??#?#?##??.? 1,10
            ???????????#?? 7,3
            ?.#?#?.??##?#???#??? 4,10
            ?????????#???#?#? 1,1,9
            #????###????#.?#.?? 3,7,1,2,1
            #??##?#??#?..?##..?. 11,2,1
            #####??????.??#??. 11,1,1,1
            ??..??.#??##?#??? 2,1,8
            ..??.?#??.?? 1,4,1
            #..?##?#?#?..?. 1,3,4,1
            ?#.????#??.? 1,2,1,1
            ?#?#?##?#????.??#?? 12,2
            ???.#.?#???#??#?#.#. 1,1,2,6,1
            ###.?##?.??.??#?? 3,2,2,1,1
            ?#?.?.#??????.?# 1,1,4,2
            ???#????.#.???? 1,2,1,1,3
            .???##.??? 4,2
            ???##?????? 7,1
            .##..????. 2,3
            ???###??..????### 2,3,1,1,4
            ??#?##?.#?##?#??.# 6,8,1
            ???????#?#?? 1,5,1
            ..???????????...? 3,3
            ????.?????#?.#?.. 2,3,2,2
            .??????.?##?? 2,3
            ????.##?..?.# 3,2,1,1
            ??#???.??? 3,1
            ..???.#??? 1,1
            ?.#??.??#. 3,1
            ?..??.?#?####????.? 1,1,1,8,1
            ?????????#?..?? 11,2
            ##.????????#??? 2,3,1,1
            .????.?.?? 2,1
            #??#?#?.#?#.?#????? 1,4,1,1,5,1
            ???????#???#??????.# 1,6,2,1,1,1
            .??#?????.????.?# 1,5,3,2
            ##?????###. 4,3
            ???.????#.??.?##?.. 5,3
            ???????##??????.#??? 11,1,1,1
            ?????.?#????#??.?# 1,1,1,8,2
            .#?.??.?????#? 1,2,4,1
            ???#????#?#.# 1,6,1,1
            ?????.?????? 3,1
            #????#?????#? 8,3
            ?????#???..? 1,6
            ??.???#????.??.#?. 1,4,1,2,1
            ?#???.?.?.??????? 4,1,1,1,1
            .?#??#???.? 6,1
            ?.#????#???? 1,8
            ???#???????#????##? 2,2,1,3,4
            #???.??.??? 2,1,1
            ###??#?#???#????.? 3,8,3
            ????#?.?.?????..? 2,3
            .??#??###??.? 1,7,1
            ?#??.???.?????? 2,2,1,1,2
            ??#??????#????.?#.?? 10,3,1,1
            .???.????.#..??????. 1,1,2,1,6
            ???.?#?###.?? 2,6
            .??.??.????#?.##?. 1,3,1,2
            ..#?..#???#? 1,6
            ?..??????????? 1,1,2,3
            .???#.??..?? 2,1,1,1
            .???.??#??#?????? 1,9,1
            ???.?????.?? 1,3
            ?????????#??###??#? 4,2,10
            ?#???#.?.???? 5,1,1,1
            ?.???????#???#????#? 1,12,2,1
            #??#??#.?.???#?. 7,2
            ?#??#??#?##???? 1,1,9
            ?#????#??#?.?????? 1,1,2,3,2,1
            #??.?..#??#?????.??? 2,1,1,6,1,1
            ?#?????#???? 3,2,1
            ..??.??.?..??#???#.? 2,7
            ?.???.????.? 1,3,2
            ??#???.???? 3,1,1
            ##???.??.#?#?? 4,4
            ??...#??????? 2,1,1,3
            ??????#?.#?? 2,1,2,1
            ???????.???.???.? 3,2
            ?????????????# 1,9,1
            #???#..??? 2,1,2
            ?.??#???????..??. 5,1
            ?????..???. 1,1,2
            .#?..?????###???.? 2,9
            .???#?##?????.. 1,9
            #??#??????.??#. 2,1,4,1
            ?#.???#???.? 1,1,4,1
            #.?.?#?????? 1,1,4,1
            .??.?.????# 1,1,2
            #??#??.??????#????? 1,3,2,3,1
            ??##...??#??? 4,3
            #?#???.?#. 4,1,1
            #?????#??? 4,1,1
            ??????????? 3,4
            ??#??#.?.?.. 5,1
            #?????#?#?#? 3,7
            ?#?##.#????## 1,2,7
            ?#?##??#?#???#?..# 12,2,1
            ?.#????##? 1,3
            ??.??#?.?????? 2,1
            ???###?????????#??. 12,2
            .??.????#????????? 1,2,9,1
            ???#?.???.???.?###? 5,3,1,1,4
            ??.#?????? 1,1,5
            ????????????#? 3,2,5
            ???#???????# 3,1,1
            ???.#.?#.?#? 1,1,2,1
            #.?????#??#? 1,1,6
            ??##???.????????#?.# 3,1,1,1,4,1
            #?#??????.#?#.?? 1,7,3,1
            ?#?..#???##??#.? 1,9
            #?????.????.????.??# 2,2,1,1,1
            .?#.##.?.#.? 2,2,1
            ???#???#??#??#?.#? 1,1,3,1,2,1
            ?#?.#????# 1,1,3
            #?#?##?#??.?.?.???.? 10,1,1,2,1
            #.?###?..#?###?? 1,3,6
            ??.???#???.?#. 6,2
            ?.??##???#???#?#?.? 1,12
            ????.??.??###?#? 2,1,6
            ?#??##????.?? 1,3,3,1
            .???.????? 2,1,2
            ?.?.?###.? 1,4
            ?#???.????????#?#? 3,11
            ????.??.?? 2,1
            ???#?????????.?.??# 3,5,1,2
            ??????.??.?#? 5,1,3
            ?#?????.?#????#? 6,1,1,2
            ???#?#????#..# 1,5,1,1
            #..??#???? 1,7
            ?.?.??..??? 1,1,2
            ?????..#?. 1,1,1
            ?#.????##?#?????#?.? 1,12
            ??.??.?????.??###?. 1,1,1,1,5
            ???.?.?#?#????? 1,1,4,2
            ??#????.?#??? 1,1,1,2
            ???..#?????. 1,1,1,2
            #??##.??????. 5,1,1,1
            .????#??#???. 1,3,1,1
            ??.????????#?? 1,1,1,3
            ??.?#.???????? 1,2,3,1
            #????.?#??.???????#? 2,1,4,1,7
            ???????????.?#??? 9,3
            ..??#?????###??? 1,5
            ?#?#????.#?.? 6,1
            ?.????#?#?..??.#? 1,6,1,1
            ??.?#?#?.??.? 1,4,1,1
            ????...??.??.?#??? 2,2,2
            #????..#?. 3,2
            ???.#.??.?#.??#? 1,1,1,1,3
            ??.?##??#???.#...??. 8,1,1
            ??..???#????#????#? 1,2,10
            .?..??#??? 1,1,3
            ?#????..???????. 5,6
            ???#?.#?#???? 3,1,2,1
            ??#?.???..#.? 1,2,2,1
            .?#?????.?#? 5,1
            .???#??#?????. 4,3
            #??.??#???????#.?? 1,1,1,7,1,1
            ?##?.#?????.. 2,1,1,1
            ??#????????#.????? 1,3,1,2,4
            ???#?##.##. 1,4,2
            .??#?.#?.?????? 4,1,2,1
            ?????#?.?.? 3,2
            .#?#???#.??.#???? 7,1,1,2
            ?.#???????. 2,1,1
            #???#.#??#??.?. 2,2,2,1,1
            ?#?.#?##.#???#? 1,1,2,2,2
            .??????.??.? 6,1
            ????????#?#? 1,5,1
            ??????#?.??.#??#??. 1,3,1,1,4
            ????#?#??????##?.? 1,1,1,8,1
            ??.??.??... 1,1
            ?#?#??#?#??#.????. 12,1,1
            ????.???.#?..#? 1,1,2,1,2
            ??????..#?#?.? 3,4
            ???.??????? 1,7
            ??.?##...?.? 3,1
            ??#???????.??.#???. 1,8,1,1,1
            .??????.????? 4,2,1
            .#?#?##???#??.?## 1,1,4,1,2
            ???##???#?#??#??.?#? 5,7,3
            ???????.??? 6,1
            ???#????#?? 3,2
            ??????????###?? 1,8
            ??????#?.##???#??# 3,2,2,1,1
            .?????#.??#??#?# 2,1,1,1,3
            #??.???.????##??? 1,1,1,2,3
            ?#?.#?##??##????#?? 2,13
            .#?#??.?.#??. 3,1,1
            ..#..?####?#?? 1,5,3
            .??#?#?.?# 1,4,1
            ??????.??#?.?? 4,4,1
            ?#?????..?????? 6,4
            ##?#?..???.. 5,1
            ????##.?????##?? 1,3,2,4,1
            .????#.????. 1,1,1,2
            ???.????#???#?. 2,1,1,4
            ??.????????? 1,1,1,2
            ???#?#.?#?# 6,3
            ?.??.?????????????# 1,1,3,5,1,1
            .#??##??#????# 6,4,1
            .???..?????#??#??? 1,5,1
            #?#?.???????##?????? 4,1,1,10
            ##.?#.?#??#??? 2,1,1,5
            #?#??????#?????? 1,6,2,1,1
            #?????.#??#?#?#???# 2,1,1,5,2
            ??####????.??.????.. 7,4
            .#???#?##??????.?? 5,2,1,1,1
            .????????.? 1,4,1
            ??.???.????.#????##? 1,2,2,2,4
            .????????.? 1,1
            ?#???.#??.#?#?.#?? 1,1,1,3,1,1
            ???????.?.. 6,1
            ?..?#?##..#?.. 5,1
            ????##???? 6,2
            #?#??????? 1,2,4
            ??????.??? 1,3,1
            ?..#??#???????# 1,5,1,1,2
            ?.?.?.???#.????#?? 1,1,2,1,3,2
            .##???????. 4,1,1
            ?#?????#??#??#???# 2,10,1
            ??#?##.??.#?##?.?? 4,4
            ????.?#?.#??.????? 4,3,1,1,1,1
            #?.???..?#????#?? 1,2,9
            ?.??#.??#..?##??###? 3,1,8
            ??.??###?#???#?? 1,13
            ?#????????? 6,1
            ??????#????##?#? 1,13
            .#??.?..?##?? 2,3
            ???#????..?#?#?.? 5,1,4
            ????????????#?#? 4,5,1,1
            ??#??.??#?#?#??? 1,7
            ??..#.??????#?. 1,1,1,1,2
            ??#.?####?##???? 2,11
            ?????????#..# 1,1,4,1
            .?????????##????#.? 1,9,1,2,1
            ????###?#???????? 6,5,2
            ??###??###?????.??? 1,8,1,1,1,1
            ????.?????#?????.? 1,1,11,1
            ###??##?.?#???#???? 7,3,2,1
            .?#????..????#?##? 5,1,5
            ???????#?. 2,2
            ?.?.?.??.#? 1,2
            ?#?.?#??.?.#?? 3,1,1,2
            ?????#?.?#. 1,1,2
            .#.???.?#?#?? 1,3,3,1
            ????#.???#?## 1,1,4,2
            #???#???#? 1,3,1
            ?##???##?#?. 4,3,2
            .#??.??.?? 2,1
            .???????#???????. 3,6,1
            ?#?####??.????. 8,1
            ?.?#??????????#??#?. 13,1
            ?#?.#?.??.?? 1,1,1,1
            ????#.?#??. 1,1,3
            ?.#??????.??#? 4,3
            .?##?????# 3,1
            ????.#?### 2,5
            ???#?#?????? 5,1
            ???.#?????# 1,5,1
            ??????.??#??#?#??? 1,3,1,1,7
            ?????#???#??????#? 2,9,1
            .#?#?.##??. 4,4
            ????????.??.? 1,1,1,2
            ?#?.?????#..? 1,1,4,1
            ??.???#?????????# 1,2,9,1
            ..???????#? 1,2
            ?#?.???##??.??? 2,5,1
            ??.#??#?.?? 2,1,2
            .????????##? 1,7
            ??#????#??#??.#?#?# 2,6,1,3
            ?#?????#????#.#.??? 8,1,1,1,1,1
            ????#.?#???? 2,1,5
            .???????.?? 1,2,1
            ??.????????????# 1,5,6
            ????????##.? 2,3
            ??#?##?????. 5,1
            ??##????.?.#??? 8,1,1,1
            ??#.?#??#???#?##.#? 1,9,2,1
            ???????..?. 2,1,1
            ?.#???????????????? 1,1,1,2,1,3
            #.?????#???#??..?? 1,1,1,5,1
            ??#...??#? 1,1,1
            ??.???????##?. 1,6,2
            ??#?.??#??????.?.# 2,4,1,1,1
            ?????#??.?????. 2,4,1,2
            ???.#????????#?. 1,1,1,6
            ??####??.??? 6,1
            ??#??.?#??#?? 2,4
            ????#??#?#????#??.?. 1,9,3,1
            ?#.??#??#. 2,6
            ???????.??. 4,2
            ??????.#??#??.# 1,1,5,1
            ?????#??.???????#?. 1,4,7
            ??.??????.?#?? 2,2
            ??????#.?# 4,1,1
            ?..??#??###?? 1,2,5
            ?.???#?????#??.??##? 4,4,4
            #??.#??.?#? 2,2,1
            ?#?#.????.???? 3,3,1
            ?#?#????????. 1,1,6
            ???##?????#????#? 11,1
            #?###?.?#???#?#? 5,6,1
            ??#????.???#??.. 7,6
            ?#?.#..?#?? 2,1,4
            ????????.#?#.##???#? 1,1,1,3,7
            ?#?#?????.??.#?? 7,1
            ?.?#??#???##???##? 4,8
            #..?.??#?????#??? 1,1,4,2
            ?.???.??????. 3,1,2
            ??.???#??#?#????#?#? 4,1,2,5
            ???##?##???.????. 8,1
            ?#?..?#?????# 2,1,1,1
            .#???#?#???????# 3,1,9
            ??####.?.??????????? 4,9
            ????.??##?.???? 1,1,5,2
            ????#.???? 1,1,1
            ??????.?#??? 1,3,4
            ##???#??#??##.??. 4,5,2
            ?.?#?.??.???????. 2,2
            ????????##????????? 2,2,4,3,1,1
            #????????????#??.?. 1,4,2,6,1
            ???#???????#??. 4,1,2,1,1
            ..????#?#?#???????? 10,3
            #???.?###? 1,1,3
            #??.?#?#??#.?#?.. 1,1,4,1,1
            ###.##?????#.???? 3,2,5,1,1
            ##?.#??????. 2,1,3
            .???#?#?.?.##??? 6,4
            .?.???.#??.????????# 1,3,3,3
            ???.#?#???#?? 1,3,3
            .?.??????.#??#??. 1,1,1,1,4
            ??##??..?#???? 4,3
            .??.???#?#??# 2,1,1,4
            ???..?#?##??#??##??? 2,8,3,1
            ????.??.#???#??? 2,1,1,7
            ?..???.#???#????. 3,5,2
            ?#???#?#??. 3,3
            ??.??#????????#??# 2,1,2,7
            #?#.???###?. 1,1,1,5
            .??##??#???..????.. 8,1
            ?#.#??????????#. 1,1,1,2,4
            .#????#??##??.?. 12,1
            ????????.??.##? 5,1,2,2
            .?????.???.?###????. 3,4
            ?????????????????? 9,5
            ???#???##.????#?. 9,1,2
            ???#?##?.??????#? 7,2,2
            .???.?#.??#?..#????? 1,3,2
            ?#?#?#.????? 6,2
            ?#??.#.?????# 3,1,3,2
            ????#????#???????. 1,5,7
            ?????...?. 3,1
            ?.???.#??#.???? 1,1,1,2,4
            ???.?.???#?? 1,1,1,1
            ?.?##??..??# 3,3
            .??????.??##?????.? 4,7
            .#????##?????. 1,5,1
            #??#????#?? 2,1,5
            .#?#???#??? 4,2
            #?????###??.##? 1,1,5,3
            ..?#?###?????.#? 7,1,2
            ?#?#???#???.?? 2,6,2
            .?#.#?????????.??.?# 2,3,1,2,2,2
            .????#?????#.????? 4,3,3
            ?#?#.??#???? 4,2
            ???#?.?.?##.. 4,3
            ????#?#?????.???.?? 7,1,1,1,1,1
            .#??#?????#????##?#? 5,4,4
            ?????.?????? 1,1,2,1
            ?#??.????????#.???. 3,6
            .?##??.??? 4,1
            #.?#??#?#????##???.? 1,6,1,3,1
            .?.#???.???????#???? 1,1,1,4,3,1
            ??.?#???#?.?#.????# 2,1,3,1,1,1
            ?#.#.??#?????? 1,1,9
            ???..?????#??..# 1,1,5,1
            .#?#?#??.#???????#. 7,1,2,1,1
            ??..?###?###?.#?# 1,7,1,1
            ??#??#?#?.?#??#?? 1,1,1,1,7
            ????.?##??# 1,1,6
            ????.??.#?? 4,1,2
            ??#?#???#..?????? 1,2,1,1,1
            ??.???????#?? 1,1,6
            ???#?..#??#?? 5,1,1,1
            ?#??.#???.??#?? 2,2,1,2
            ?.???????.?# 1,1,1,2
            .?????#?#?? 1,6
            .?????#???.#.?? 4,1,1,1
            ????.????? 2,4
            ?#???????????? 5,1,1
            #??#?#??????#?#??. 1,4,5,1
            .??????.?#.#??? 4,1,1,1,2
            ????????#??#?###??? 1,2,13
            ??#?#.#.?????????? 5,1,1,1,1,1
            ??#.??#????? 3,5,1
            ?????#??#.?? 1,5
            ?.??.?.??#?##?..?# 1,1,1,5,1
            ??#??#???#.????.? 6,1,1,1
            ???#?#?#.????#??? 6,1,7
            ?#???..???#?? 1,1,1,4
            ?#??#???.#..? 2,5,1,1
            ?.??#?.?#?#?.?#?# 4,2,1,1,1
            ????#?????#?? 3,8
            ?#?#?..????#??.. 4,6
            .??.????#??#?#?????. 1,10,4
            ???.????#???????? 1,1,7,4
            ??.?##???? 1,2,1
            .????.##?? 1,3
            ?#.??.??##?.#.? 1,1,4,1,1
            .???..#?#?. 2,3
            ???##????#??.#? 5,4,1
            ?.?##????#? 1,5,2
            ???#?#??#.?#. 4,1,1
            #???????###.??# 1,1,5,1
            .?#??.???#. 4,1
            ?????#???.#. 9,1
            ?#.???.?#??? 1,3,1
            ??#??.?????##??? 2,1,3
            .??.??.???## 2,1,5
            ??...???.?###..? 1,3
            ???????#?#??#.???..? 1,2,7,1,1,1
            ?????.?????? 3,1,1
            ?#.???#???####? 1,9
            ?#?.?#???? 2,1,1
            ?#??#?#????? 9,1
            ???###???###?.??.#. 12,1
            ???..??.?? 1,1,1
            ?..#??##.? 1,5
            ?.##??..??#??#?#?.? 2,8
            ?.??#?#???#####.??.? 12,1
            .#???????? 1,5
            #?????####???????. 2,1,6,2,1
            ?????.?#?#? 2,1,1
            .#?.????.???##??# 1,1,1,1,5
            #???##?#?????#??.? 11,3
            .#???#???.## 1,4,2
            ?????..??????? 1,5
            .#????.?#? 1,3,1
            ??????.??????? 1,2
            ##??.???????#?. 4,4,1
            .?###?#?????????##? 9,7
            ###?#?????#???# 8,2,1
            ?.????????#?#? 1,1,2,5
            .?#??#????#. 3,3,2
            ?..????????##??##?? 1,1,1,8
            ??????#?#???? 7,4
            ?.?##??#.?? 1,6,1
            .?????.###???. 2,1,6
            ##?????????..??# 6,1,1,3
            ??..?????.#? 1,4,1
            .?#?????#. 3,1
            ???.#???#??????#??. 1,2,1,1,1,3
            ##???#???????????# 11,2
            ?#??.#??#??##?#??? 3,2,7,1
            ..#.???.#??#??. 1,1,5
            #??????#???.??#? 1,4,1,3
            .#.?.??#?? 1,3,1
            ???????#??..#??. 9,1
            ??.???#?##.?#? 1,5,1
            ???#??##?##?#??????? 5,7,1,1,1
            .#.???..?? 1,1,2
            .???#?#.???? 1,3,1
            #?#?###???##.???#? 7,1,2,4
            ??.#??#.?#?.???? 1,4,1,1,1
            .??????.?#?##?? 5,1,3
            .???????##.?? 3,4,1
            #??#?#???##??????? 12,2
            ?.?????#??????#..?? 1,1,7,1,1,2
            ??????#??.?????#.? 1,6,1,3,1
            ?#??????????? 9,1
            .??..???#????????? 2,1,7
            ?#??.?..?#?.?? 2,1,2,2
            ?#????..#?? 2,2,2
            .???.##?#???.??.#? 1,1,4,1,1,1
            .???..###??#?.? 1,6,1
            ?.???????##?#??##? 2,9
            #?.#.??.?????#?#? 2,1,1,4,4
            ????#?????. 2,1
            ?#?.#?#####?#? 1,9
            ???#.?#?.#???.?????? 4,2,3,1,2,1
            ?????.?.???.??? 3,1,2,1
            ??###.#?#? 5,1,1
            .?#?#????.???.#? 7,1,1,2
            #?.??.???.#?#? 1,1,2,4
            #.?#?#.?###????#??.. 1,1,1,5,4
            ?.??##?#??.?# 7,2
            ?#?#?#??#??.?????.? 1,6,1
            ?..??.?.??#?.#? 1,2,1,2,1
            .?#??.#??????? 2,3,2
            ????.?????#?? 2,1,2,4
            .#..???????#?#??#? 1,1,6,2
            ??????#.?.##??#? 2,1,1,1,6
            ##???#????#?????? 8,2,2
            #??#?##????#???. 7,4
            ?.#?##??##.#?? 1,6,2
            #??#???.#??. 1,2,1,3
            .#?..#????#?.. 2,3,2
            ????.#??..?. 1,3
            .??.#?#??.??#????#?# 1,5,4,4
            ?.#????#???????????? 1,1,1,5,1,3
            ??#??.#.#??..#?#? 2,1,3,3
            ?.?.?####??###? 1,1,10
            ???#??..#??.#??#?. 4,1,2,5
            ?????##?.??# 3,3,3
            #??????#??????#? 2,5,2,1,1
            ?..???.????#?????# 1,10
            ?#???#?#???.?? 3,5
            ?#??#????#?#??? 5,4
            ??#????##??#?. 8,3
            .????????.#. 5,1
            ..???..?????#? 1,1,2
            #??#.?????.?# 4,1,2,2
            #?#?.##????? 1,2,3,1
            ?#?#??????????#???. 7,3,4
            .??????#.?#?#?# 5,1,6
            ??????????#????.??? 1,1,7,2
            ???????#??.??????? 9,1,1
            .????##???## 2,8
            ###.????.? 3,4
            #??#?#?#??? 1,1,4
            ?????????##???.??#? 2,2,6,3
            ?#?????????? 2,1,1,2
            .#???###??.? 1,7,1
            ?..?????#.??#???. 6,1,1,2
            ??.?.????????##??? 1,1,3,1,4,1
            ??.??#??..# 1,2,1
            #?.?#.??????. 2,1,1,4
            #?#.???.?#?????? 3,2,1,3
            ??????.???.?? 3,2
            #???.???#??.?????. 2,1,1,3,1,1
            ?#???????.???? 3,2,2,1
            #??#????..?# 5,1,1
            ?.#?????????#?#???. 1,11
            #????#?#????#???# 2,5,1,3,1
            ??.#?????# 2,1,4
            ?...#??#???.? 1,6
            ??#??????.???#?#??? 9,3,4
            .?.#..####????..#? 1,1,4,1,1,1
            ??.?#???????## 1,6,1,2
            ?#???????##?#?.#?? 1,5,2,1,1,1
            #?#..#??#?.?.#.?? 3,4,1,1,1
            .??.#?#???##???.? 2,11,1
            ??#?#?.??????####. 1,4,1,1,1,4
            ??#?##??#??#.? 2,8
            .???.??#??? 1,5
            #?..??..#? 2,1,1
            ?????##??#.?.? 10,1
            .??.??.??.????????? 1,6
            #..??????#??#?#?#? 1,1,11
            ?.????????.??? 4,2
            ?#.#??#.???? 1,4,1,1
            ?#??.??????????????. 2,1,6,1,1
            ??#??????##??? 4,2
            .??##???#?? 4,3
            #??.?.#???????. 1,1,1,1,2
            ?###??#??. 4,1,1
            ???.??#???? 1,3,3
            ????#???#????####?#. 1,1,3,3,4,1
            ?.##?#??.??????.? 6,1,2
            ????##.??????#? 1,1,2,2,4
            ??#?.#??#??. 1,1,1,1
            ?.?????#?.??#???##? 3,1,8
            ?.??##???..#? 5,1,1
            ??.????###?#??#? 2,10
            .??.#?#.?#.??????# 1,1,1,1,4,2
            #???????.????? 1,1,3,4
            .?#??#??.?.??. 7,1
            ?.##?###??.???. 8,3
            ??.?.#????#??####?. 2,1,13
            ????#?????#?? 3,4
            #????????????? 3,7
            ?#.##?.???#??#????# 1,3,8,2
            ?????.?#####??#. 1,8
            #?.???...??? 1,1,1,2
            ?.###?#?#??.? 1,7
            ??.?#???..#??? 3,2
            .????????.??.? 3,2
            #.??.?.?#???? 1,2,3,2
            .??#??.?##? 3,3
            ??#????.????#??? 4,2,5,1
            ?????.#??????????? 3,1,1,1,1,3
            .???#.????? 2,1,1
            .?.?????.????. 4,1
            ??????.??#.?##??#? 2,3,3,2,2
            ?.??#.???? 1,1,1
            ???##?#??#.???#?## 1,8,1,4
            .#.?.#.?#???. 1,1,3,1
            ???????#??.? 1,6
            ??.?#?#?##?????# 1,13
            ????????????? 3,3
            #???#??.?????#??#? 1,1,3,9
            .???#?.?..?#?? 5,2
            .##.???#???? 2,3
            .?.??#?##?#??##??#?? 1,13
            ???#??#??????#?.? 9,1,2,1
            ????.#?#??.??. 1,5,1
            #.?????##?????#?? 1,13
            ????#?.??#?..?. 4,2,1
            ????##?##?#????????# 11,2,1
            ?.?#?##.?.?? 5,1
            ????????#??.? 1,2,3,1
            .?.??.#??????..??? 2,1
            ..??#???##???? 1,1,3,2
            ##..?#?#???.?? 2,4,1
            #??#??.????#??? 2,1,1,4
            .???#???????#?? 1,4,1,5
            ?.???#???#?#.?..?? 1,1,7,1,1
            .##??##???##? 7,2
            ????#?#????#?. 1,4,1,1
            ??????????.??#? 6,3
            ?##??.?????? 3,4
            ?##.??.?#??.#?? 2,2,4,1
            ??.?#??????? 1,3,1,2
            ?#??##?#.??????.?? 8,1,3,1
            .??????????.#??? 6,1,1,1
            ?????.#???????? 1,3,1,4,1
            .?.??#?.???????#??? 1,2,9,1
            ?.???????.? 1,2
            ?#??#.#???#????.??? 2,1,1,1,3,1
            ????#.?????. 1,3,1,1
            #.????#..?..??????? 1,5,1,2,1,1
            #??##???.? 1,4
            #??###??#?.#.?# 2,6,1,1
            ?#??????????? 5,2
            .???.?#???.? 1,1
            ?.#???????? 1,5,2
            ##??????#?.?????##? 9,6
            ????#????#???. 1,10
            ?.???###?? 1,5
            ???.?#??##????. 1,8
            #???#?#?????? 1,5,2
            ??#??#?.??#?.?.? 5,2
            ?.#?#????###??#? 4,7
            ?????.????#?? 2,3
            ???##.???.?????.# 4,3,5,1
            ?#.?#??..?##?.?? 1,4,3,2
            ??.?????.?????. 1,2,1,1,1
            ??????.??#??#?..??? 2,6
            ?????##?#????#??#??? 3,13,1
            #???.??#??? 1,1,4
            ??.?????.? 1,5,1
            ?.??????#?.??#? 4,2,2
            ????#???????.? 6,3,1
            ??#.??.??#??? 3,1,5
            .??.#?#????????#?? 1,4,7
            ??.????#?#?..##?.??. 1,1,5,2,2
            ?.????#??##???# 3,1,3,2
            ??.??.?#?#?#?? 1,2,5
            .????##.????????#??? 5,11
            ?##??????##.???? 4,5,2
            ?????????????#??#? 1,1,1,9
            ??.?#?.???? 2,1
            ???##???.???? 6,1
            .?????#????#?.??.? 1,7,1,1,1
            ?..????####???????? 1,1,11
            ?.??????#. 2,3
            ?#?????#?#??????.? 4,4,3,1
            ..???#.???#??? 3,1
            ???.#??.?#?. 2,1,1,3
            ??.?????#.??#?. 1,1,1,4
            .?..?????? 1,5
            ??.????#?.?#? 1,5,1
            ??????.??? 1,2
            ?###?#?#???##?#?#?#? 6,1,8
            ???#???#??????#???? 5,1,1,1,2
            ?.?#.????????#?? 2,3,4
            ???####??????.???##? 11,1,2
            ?.##???#???#?.??? 4,6,1
            #?#?.?#.??#?#??? 3,2,1,5
            #.???????#??.?. 1,5,3,1
            ?????????#?#?#?.# 3,3,6,1
            .?#??##???.??.?? 9,1,1
            ?????#?..? 1,2
            ??#?#????.. 4,2
            #.#???????? 1,5,2
            #???#??????#???#? 1,5,3,3
            ???..?#?#? 1,4
            ?.????#?.?#?? 1,3,1,4
            ?#????.#??? 2,1,1
            ??.##??????#????..?? 2,4
            ..??..??????# 2,2,2
            ..???????????#???? 2,3,3
            ?????????#?..???# 10,4
            ??#??#?#??#???? 6,4
            #..?#???#??????.? 1,12,1
            ?##??#???#??.##?? 6,3,4
            .?????#?.?#.????? 1,5,1,1,1
            ?.??#.#??. 1,3,2
            .?#?#??###.??.?? 8,1
            .???#?#?.?? 3,2
            ###?#?#????? 8,2
            ???#.?.??##?##??.. 3,7
            ????#???#? 5,2
            ?.#.??.??????###? 1,1,1,1,4
            ??????##.??.?##?. 2,1,2,1,4
            ????....?## 3,3
            .##??#??#??# 3,7
            ?#?#??##????.? 3,4,2
            ?????????? 1,2
            ?.??????#?????#?#??. 8,5
            .#???.?#???#?#?#.?. 2,2,6,1
            ???##????# 3,1,1
            ??#.?#?##??#???? 1,1,7,1,1
            .??##?#?.?????# 1,2,2,1,4
            ??.?????#?#?.?##? 2,7,3
            .?.??#?.?#??#?????? 1,3,5,2
            ?.????.??????#?? 2,3
            ?#.???????##? 1,3,3
            ?##???..?#????.#??#? 6,3,1,1,3
            ????????..##. 4,2
            .?#.?.????##?????# 1,1,1,5,1,2
            .#????#?#?##?. 1,1,6
            .#?.###??#???#? 2,11
            ????#??????? 5,1,1
            ??#?###?#??? 5,1,1
            ?.???.#?.??##? 1,1,2,3
            ?????????#?.#??? 1,3,4,3
            ??.?#??#?????#?#? 2,4,1,1,1
            .??#???.?#?.? 5,2,1
            ????##??##??#???? 12,1
            #?????.??.?# 2,3,1
            .#???.?.?????. 3,1,4
            .?.?????### 1,8
            .#?????.??..???? 1,1,1,2,3
            ?.??#?.????#?????? 4,4
            .##??#?#????#? 2,6,1""";
}
