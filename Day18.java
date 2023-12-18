import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Stream;

public class Day18 {
    public static void main(String... args) {
        for (var input : List.of(TEST_INPUT, INPUT)) {
            System.out.println("Part I: " + partI(input));
            System.out.println("Part II: " + partII(input));
        }
    }

    static long partI(String input) {
        var instructions = parse(input);
        var loop = trench(instructions);
        return lagoonSize(loop);
    }

    static long partII(String input) {
        var instructionss = parseII(input);
        var trench = trench(instructionss);
        return lagoonSize(trench);
    }

    static List<Instruction> parse(String input) {
        var directions = Map.of(
                "L", Direction.LEFT,
                "U", Direction.UP,
                "R", Direction.RIGHT,
                "D", Direction.DOWN);

        return input.lines()
                    .map(line -> line.split(" "))
                    .map(parts -> {
                        var direction = directions.get(parts[0]);
                        var distance = Integer.parseInt(parts[1]);
                        return new Instruction(direction, distance);
                    })
                    .toList();
    }

    static List<Instruction> parseII(String input) {
        var directions = Map.of(
                "0", Direction.RIGHT,
                "1", Direction.DOWN,
                "2", Direction.LEFT,
                "3", Direction.UP);

        return input.lines()
                    .map(line -> line.split(" "))
                    .map(parts -> {
                        var direction = directions.get(parts[2].substring(7, 8));
                        var distance = Integer.parseInt(parts[2].substring(2, 7), 16);
                        return new Instruction(direction, distance);
                    })
                    .toList();
    }

    static Trench trench(List<Instruction> instructions) {
        var edges = new HashMap<Pos, Tile>();
        var cornerXCoords = new TreeSet<Integer>();
        var cornerYCoords = new TreeSet<Integer>();
        var pos = new Pos(0, 0);

        edges.put(pos, Tile.S);
        int minX = 0;
        int maxX = 0;
        int minY = 0;
        int maxY = 0;

        for (var ix = 0; ix < instructions.size(); ix++) {
            var instr = instructions.get(ix);

            for (var step = 0; step < instr.distance(); step++) {
                pos = pos.move(instr.direction());
                var exit1 = instr.direction().opposite();
                var exit2 = step < instr.distance() - 1
                            ? instr.direction()
                            : instructions.get((ix + 1) % instructions.size()).direction();

                var tile = Tile.find(exit1, exit2);
                edges.put(pos, tile);

                if (tile != Tile.NS && tile != Tile.EW) {
                    cornerYCoords.add(pos.y());
                    cornerXCoords.add(pos.x());
                }

                minX = Math.min(minX, pos.x());
                maxX = Math.max(maxX, pos.x());
                minY = Math.min(minY, pos.y());
                maxY = Math.max(maxY, pos.y());
            }
        }

        return new Trench(edges, cornerXCoords, cornerYCoords, new Pos(minX, minY), new Pos(maxX, maxY));
    }

    static long lagoonSize(Trench trench) {
        long size = 0;
        long lastRowSize = 0;
        var inside = false;

        for (var y = trench.min().y(); y <= trench.max().y(); y++) {
            if (!trench.cornerYs().contains(y) && !trench.cornerYs().contains(y - 1)) {
                int nextY = Objects.requireNonNullElse(trench.cornerYs().ceiling(y), Integer.MAX_VALUE);
                size += lastRowSize * (nextY - y);
                y = nextY - 1;
            } else {
                lastRowSize = 0;

                for (var x = trench.min().x(); x <= trench.max().x(); ) {
                    var pos = new Pos(x, y);

                    if (!trench.cornerXs().contains(x)) {
                        int nextX = Objects.requireNonNullElse(trench.cornerXs().ceiling(x), Integer.MAX_VALUE);

                        if (inside || trench.edges().containsKey(pos)) {
                            size += nextX - x;
                            lastRowSize += nextX - x;
                        }

                        x = nextX;
                    } else {
                        var tile = trench.edges().get(pos);

                        if (tile == Tile.NS || tile == Tile.NE || tile == Tile.NW) {
                            inside = !inside;
                        }

                        if (inside || tile != null) {
                            size++;
                            lastRowSize++;
                        }

                        x++;
                    }
                }
            }
        }

        return size;
    }

    record Instruction(Direction direction, int distance) {}

    record Trench(Map<Pos, Tile> edges, TreeSet<Integer> cornerXs, TreeSet<Integer> cornerYs, Pos min, Pos max) {}

    record Pos(int x, int y) {
        Pos move(Direction direction) {
            return switch (direction) {
                case UP -> new Pos(x, y - 1);
                case RIGHT -> new Pos(x + 1, y);
                case DOWN -> new Pos(x, y + 1);
                case LEFT -> new Pos(x - 1, y);
            };
        }
    }

    enum Tile {
        NS, EW, NE, NW, SW, SE, G, S;

        static Tile find(Direction exit1, Direction exit2) {
            return Stream.of(values())
                         .filter(t -> t != S)
                         .filter(t -> t.hasExit(exit1) && t.hasExit(exit2))
                         .findFirst()
                         .orElseThrow();
        }

        boolean hasExit(Direction n) {
            return switch (this) {
                case NS -> List.of(Direction.UP, Direction.DOWN).contains(n);
                case EW -> List.of(Direction.RIGHT, Direction.LEFT).contains(n);
                case NE -> List.of(Direction.UP, Direction.RIGHT).contains(n);
                case NW -> List.of(Direction.UP, Direction.LEFT).contains(n);
                case SW -> List.of(Direction.DOWN, Direction.LEFT).contains(n);
                case SE -> List.of(Direction.DOWN, Direction.RIGHT).contains(n);
                case G -> false;
                case S -> true;
            };
        }
    }

    enum Direction {
        UP, RIGHT, DOWN, LEFT;

        public Direction opposite() {
            return values()[(ordinal() + 2) % 4];
        }
    }

    private static final String TEST_INPUT = """
            R 6 (#70c710)
            D 5 (#0dc571)
            L 2 (#5713f0)
            D 2 (#d2c081)
            R 2 (#59c680)
            D 2 (#411b91)
            L 5 (#8ceee2)
            U 2 (#caa173)
            L 1 (#1b58a2)
            U 2 (#caa171)
            R 2 (#7807d2)
            U 3 (#a77fa3)
            L 2 (#015232)
            U 2 (#7a21e3)""";

    private static final String INPUT = """
            L 4 (#3b05c0)
            U 7 (#9ccb53)
            L 7 (#b37500)
            D 2 (#88c1c3)
            L 2 (#17a960)
            D 10 (#d78923)
            L 4 (#5e2830)
            D 7 (#889403)
            L 9 (#172fc2)
            U 7 (#1dff43)
            L 2 (#5b9832)
            U 12 (#121513)
            L 3 (#be4c92)
            U 5 (#121511)
            L 7 (#3337d2)
            U 5 (#3a59f3)
            R 7 (#775a10)
            U 6 (#161c23)
            L 7 (#134750)
            U 6 (#e05843)
            R 8 (#69cd12)
            U 2 (#467933)
            R 3 (#8f52f2)
            U 5 (#6706d3)
            L 7 (#840c60)
            U 3 (#7e7463)
            L 4 (#7513a0)
            U 3 (#7bf2d3)
            R 3 (#ae9d42)
            U 8 (#8a9dd3)
            R 3 (#536812)
            U 3 (#36b703)
            R 8 (#7144f2)
            U 7 (#83ae83)
            R 5 (#238092)
            U 3 (#83ae81)
            R 4 (#758bb2)
            U 5 (#00eaf3)
            R 6 (#c78c12)
            U 5 (#8501b3)
            L 4 (#6a6d90)
            U 5 (#663513)
            R 6 (#a57700)
            U 4 (#677833)
            L 6 (#a22710)
            U 5 (#13f923)
            L 5 (#1fd1a0)
            D 6 (#441a23)
            L 3 (#b3dca0)
            D 8 (#05b553)
            L 3 (#4e28b0)
            U 9 (#95e4d3)
            L 4 (#3def80)
            U 4 (#44afe1)
            L 4 (#21ee50)
            U 8 (#428591)
            L 6 (#576120)
            U 7 (#7839a1)
            R 8 (#8686f0)
            U 2 (#babf33)
            R 4 (#3d8f90)
            U 7 (#44afe3)
            R 3 (#a4aa30)
            U 7 (#7461a3)
            R 4 (#3738c0)
            U 7 (#6e5413)
            L 7 (#122532)
            U 5 (#372073)
            R 7 (#122530)
            U 3 (#755d93)
            R 5 (#32b3d2)
            U 7 (#c58be3)
            R 7 (#7fe1e2)
            U 4 (#5888e3)
            R 7 (#06b0b2)
            U 6 (#57fb61)
            R 4 (#932122)
            U 4 (#64bac1)
            R 5 (#400e72)
            U 8 (#615ea1)
            R 6 (#cac272)
            U 9 (#370f93)
            L 5 (#bf3d40)
            U 4 (#8bd443)
            L 6 (#1d6be0)
            U 4 (#5c9943)
            L 4 (#ad0200)
            U 4 (#4ecdb3)
            R 10 (#678230)
            U 5 (#5d6fb1)
            R 5 (#84c0e0)
            U 2 (#5d6fb3)
            R 3 (#44cbe0)
            U 9 (#5db673)
            L 3 (#15f5a0)
            U 3 (#4b6dd3)
            L 2 (#3ea330)
            U 2 (#1be133)
            L 10 (#643292)
            D 5 (#8702b3)
            L 10 (#53c452)
            U 3 (#1e2753)
            R 6 (#353852)
            U 5 (#41b5c3)
            R 8 (#6a8132)
            U 2 (#2d5753)
            R 4 (#874862)
            U 2 (#2d5751)
            R 7 (#53b102)
            U 5 (#2d3f23)
            R 4 (#1161b0)
            U 7 (#b8f5c3)
            R 5 (#036990)
            U 7 (#138ad3)
            L 8 (#47a7d0)
            U 4 (#813631)
            L 6 (#1c4ee0)
            U 5 (#421741)
            L 3 (#909fd0)
            D 5 (#a2bd43)
            L 9 (#0321e0)
            U 5 (#209033)
            L 6 (#35ba20)
            U 9 (#138ad1)
            R 7 (#414390)
            D 6 (#2272c1)
            R 6 (#5df4c0)
            U 6 (#968301)
            R 5 (#62c5d0)
            U 4 (#366641)
            R 3 (#1e9952)
            D 9 (#8c71d1)
            R 7 (#1e9950)
            U 9 (#287e11)
            R 4 (#21e850)
            U 3 (#90fd21)
            R 4 (#5b1c52)
            D 5 (#3dd851)
            R 2 (#0353a0)
            D 7 (#a37241)
            R 4 (#0353a2)
            U 7 (#046661)
            R 2 (#5b1c50)
            U 5 (#228711)
            R 6 (#2820a0)
            D 7 (#2f77a1)
            R 2 (#92c4e0)
            D 5 (#038e51)
            R 5 (#617e32)
            D 4 (#8240e1)
            R 3 (#84f1d0)
            D 5 (#4d0051)
            R 6 (#84f1d2)
            D 3 (#4b6571)
            R 8 (#617e30)
            D 4 (#6712c1)
            R 6 (#703990)
            D 2 (#11f981)
            R 7 (#e3f2e0)
            D 7 (#06a011)
            R 3 (#86de00)
            D 3 (#673f33)
            R 3 (#8c8820)
            D 7 (#4eb723)
            R 7 (#645e80)
            D 3 (#b5f651)
            R 10 (#8a2430)
            U 2 (#89f4d1)
            R 2 (#050a70)
            U 5 (#45ab31)
            L 6 (#3daa70)
            U 2 (#8ed951)
            R 6 (#25e9c0)
            U 6 (#693c91)
            R 5 (#93df80)
            U 8 (#9644d3)
            R 2 (#7a8230)
            U 4 (#9644d1)
            R 7 (#affd70)
            D 6 (#54ddb1)
            R 8 (#0d9e70)
            D 4 (#0888b1)
            R 7 (#0b2b30)
            D 9 (#0dcc91)
            L 7 (#64dcc0)
            D 6 (#91d211)
            R 4 (#1a0d80)
            D 2 (#53c0f1)
            R 5 (#4c0310)
            D 4 (#0122b1)
            R 2 (#d61882)
            D 11 (#6cb921)
            R 5 (#82d122)
            D 2 (#61e653)
            R 8 (#5d4ab2)
            U 5 (#7ec313)
            R 6 (#4e5192)
            U 3 (#99ef93)
            R 3 (#b18092)
            U 6 (#1fc273)
            R 5 (#665bc2)
            D 5 (#afc9d1)
            R 2 (#0f03f2)
            D 4 (#a92be1)
            R 9 (#6b0542)
            D 5 (#30f231)
            R 9 (#7a0930)
            U 5 (#107381)
            R 4 (#22d782)
            U 10 (#34af91)
            L 4 (#70f2c2)
            U 5 (#34af93)
            R 7 (#278b62)
            D 4 (#1169b1)
            R 8 (#556022)
            D 2 (#159651)
            R 6 (#6547c2)
            D 4 (#a2f3e3)
            L 2 (#43cf72)
            D 2 (#a2f3e1)
            L 9 (#5e80a2)
            D 3 (#439c61)
            L 3 (#5af2d2)
            D 5 (#538dc1)
            R 6 (#1b7680)
            D 7 (#a265e1)
            R 5 (#938a70)
            D 6 (#459c41)
            L 8 (#55f7e0)
            D 3 (#47c5f1)
            R 8 (#30fc72)
            D 4 (#066f11)
            R 5 (#875572)
            U 5 (#7eaa11)
            R 3 (#4ca6f2)
            U 8 (#1c3191)
            R 3 (#60edd2)
            U 3 (#1621c1)
            R 9 (#42bb90)
            U 7 (#84add1)
            R 4 (#a3c300)
            D 6 (#84add3)
            R 6 (#1d6e70)
            D 4 (#028311)
            R 12 (#57ec70)
            D 2 (#70a6e1)
            R 4 (#9a3380)
            D 9 (#6e24f1)
            R 5 (#4cba10)
            D 7 (#decbd3)
            R 7 (#2f3010)
            U 7 (#028313)
            R 3 (#0ad150)
            D 4 (#6b7271)
            R 3 (#4ba390)
            U 6 (#574e51)
            R 8 (#9ce7e0)
            U 5 (#2223e3)
            R 10 (#8b33b0)
            U 3 (#a09ce3)
            R 2 (#23f6e0)
            U 11 (#91a151)
            R 5 (#28c8e0)
            U 6 (#4b1ea1)
            L 5 (#58c292)
            U 6 (#5a4961)
            L 8 (#29f382)
            D 6 (#2a72e1)
            L 4 (#c70bb2)
            U 9 (#2a72e3)
            R 4 (#1f2592)
            U 5 (#57e761)
            R 7 (#4cda90)
            U 2 (#035b73)
            R 3 (#d3a710)
            U 3 (#035b71)
            R 4 (#4865b0)
            U 5 (#6dc6a1)
            R 6 (#28c8e2)
            D 3 (#3efa01)
            R 7 (#779652)
            U 3 (#966c01)
            R 2 (#08e862)
            U 4 (#0e11a1)
            R 8 (#3a6142)
            D 7 (#5c49c1)
            R 4 (#054370)
            D 5 (#1f0881)
            L 2 (#054372)
            D 5 (#752fa1)
            L 7 (#6b97c2)
            U 5 (#29adc1)
            L 4 (#5ee480)
            D 5 (#657901)
            L 4 (#628ed0)
            D 3 (#708781)
            L 4 (#c17352)
            D 7 (#7ee9d1)
            R 6 (#a7e4b2)
            D 10 (#0287d1)
            R 6 (#7233e2)
            D 9 (#08e581)
            R 4 (#588172)
            D 2 (#d6cf61)
            R 5 (#3ca6b2)
            D 5 (#8ff473)
            R 2 (#24ffa2)
            D 6 (#4fc073)
            R 4 (#0057f2)
            D 8 (#6e9b91)
            L 3 (#089c12)
            D 5 (#56c251)
            L 3 (#827ba2)
            D 9 (#07f9f1)
            L 4 (#2c0de2)
            U 2 (#2d9f71)
            L 6 (#a6bdd2)
            U 8 (#534e41)
            L 3 (#8d2960)
            U 4 (#592ee1)
            L 3 (#ad94c0)
            D 5 (#69bd71)
            L 7 (#3c5d40)
            D 3 (#a25bf1)
            L 2 (#88dda0)
            D 8 (#687061)
            L 2 (#92cef0)
            D 2 (#690113)
            L 10 (#0e3ce0)
            D 3 (#a1cb43)
            L 7 (#07c0b0)
            D 8 (#5426f1)
            L 7 (#7d1c80)
            D 3 (#06cb31)
            R 6 (#392870)
            D 2 (#d2c171)
            R 9 (#0d53c0)
            D 4 (#3baf61)
            L 9 (#4ec030)
            D 4 (#02f2c3)
            L 6 (#8aadf0)
            D 3 (#02f2c1)
            L 8 (#0aaae0)
            D 3 (#03cfa1)
            R 4 (#087c20)
            D 10 (#3cd711)
            L 4 (#66d142)
            D 3 (#08e551)
            L 5 (#a75212)
            D 7 (#2277d1)
            L 4 (#8409a2)
            D 7 (#a66481)
            L 7 (#1ffb60)
            D 9 (#2041f1)
            L 6 (#a7a760)
            D 6 (#315b31)
            L 5 (#352770)
            D 4 (#67d6c1)
            L 6 (#91fdd0)
            D 6 (#461601)
            L 7 (#7732a0)
            D 4 (#3fe621)
            L 4 (#b1d530)
            D 5 (#364e31)
            R 4 (#3740c0)
            D 7 (#38bd21)
            L 6 (#52e6f2)
            D 5 (#390041)
            L 6 (#1abff0)
            D 2 (#a3d5b1)
            L 3 (#1abff2)
            D 8 (#0816d1)
            L 4 (#468af2)
            D 6 (#264353)
            R 4 (#4d46e2)
            D 6 (#b33e83)
            L 8 (#4d46e0)
            D 3 (#0b6af3)
            R 7 (#4fa412)
            D 3 (#43c8c1)
            R 4 (#01b542)
            D 5 (#6690a3)
            L 4 (#d6fab2)
            D 4 (#6f0f53)
            L 7 (#82b282)
            D 3 (#a6ecc1)
            L 5 (#0ecea2)
            D 4 (#2eb331)
            L 6 (#3315f2)
            D 5 (#a3b781)
            L 7 (#68b3a2)
            U 6 (#6d7571)
            L 7 (#8c7ea0)
            U 3 (#26c711)
            L 8 (#14bba0)
            U 5 (#ac23a1)
            L 8 (#a13a42)
            U 4 (#3ea631)
            L 6 (#1dcc02)
            U 6 (#5b70a1)
            L 3 (#1d51a2)
            U 6 (#991d63)
            L 3 (#8f1392)
            U 4 (#991d61)
            L 7 (#419f52)
            U 3 (#75fba3)
            L 3 (#324ec2)
            U 3 (#adb673)
            L 3 (#9f02c2)
            U 3 (#3ac471)
            L 5 (#17eda2)
            U 4 (#15b3e1)
            L 5 (#3a32a2)
            U 5 (#d339c1)
            L 3 (#3abd12)
            U 5 (#5b70a3)
            R 6 (#53bc52)
            U 4 (#b2d4c3)
            R 3 (#530272)
            U 5 (#2dbb53)
            R 5 (#43fcd2)
            U 3 (#603603)
            R 8 (#4835d2)
            U 5 (#93a603)
            L 5 (#1e0e82)
            U 2 (#37fff3)
            L 8 (#8fed80)
            U 4 (#4cbd33)
            L 6 (#1a53a0)
            U 2 (#aa1d43)
            L 3 (#530270)
            U 6 (#a87e03)
            L 4 (#07da10)
            U 9 (#7b0c33)
            L 4 (#2412f0)
            U 3 (#3ac983)
            L 8 (#7ffb80)
            U 5 (#ad4a21)
            L 6 (#660320)
            U 3 (#ad4a23)
            L 3 (#1e60b0)
            U 6 (#03a1c3)
            R 6 (#0d92f2)
            U 6 (#83cb03)
            R 2 (#b4c392)
            U 4 (#83cb01)
            R 4 (#6df5d2)
            U 4 (#347e43)
            R 2 (#3b9262)
            U 4 (#4d0963)
            R 7 (#9c3dc2)
            U 3 (#303963)
            L 3 (#9d4772)
            U 10 (#374ab3)
            L 3 (#24ea32)
            U 9 (#0e5ba3)
            L 5 (#3ca720)
            U 3 (#292293)
            L 11 (#993be0)
            U 3 (#2a88b3)
            L 4 (#177f92)
            U 5 (#843e43)
            L 9 (#be6372)
            D 5 (#0d45a3)
            R 5 (#2d63f2)
            D 4 (#1509f3)
            R 4 (#a5eea2)
            D 3 (#764283)
            R 7 (#5070a2)
            D 4 (#2180e3)
            R 5 (#34e012)
            D 5 (#757a13)
            L 6 (#34e010)
            D 5 (#729d13)
            L 7 (#5a6472)
            U 5 (#112493)
            L 4 (#7bc2c2)
            D 5 (#2510e3)
            L 4 (#9c0a92)
            D 4 (#131863)
            L 4 (#8be052)
            D 4 (#4caca1)
            L 3 (#20b432)
            D 9 (#60a703)
            L 3 (#5275e2)
            U 4 (#dc4313)
            L 3 (#395152)
            U 9 (#dc4311)
            L 3 (#6c9ad2)
            U 5 (#60a701)
            L 2 (#22a1a2)
            U 6 (#0ac771)
            R 8 (#73bf42)
            U 4 (#275271)
            L 8 (#5ce540)
            U 5 (#4fba31)
            L 3 (#581a70)
            U 3 (#528d11)
            R 9 (#b4ffb2)
            U 2 (#4bcb41)
            R 2 (#1304f0)
            U 5 (#423c71)
            L 11 (#441b40)
            U 5 (#27b871)
            L 3 (#bfcce0)
            D 7 (#27b873)
            L 3 (#988a00)
            D 3 (#4841f1)
            L 7 (#1f5e02)
            D 3 (#471301)
            L 5 (#674332)
            D 5 (#4077b1)
            L 4 (#9d03b2)
            D 6 (#7bf3b1)
            L 2 (#04b082)
            D 4 (#ba5663)
            L 5 (#95ffd2)
            D 5 (#249f53)
            L 8 (#03a442)
            D 3 (#7911a3)
            L 4 (#03a440)
            D 3 (#992453)
            L 4 (#467c72)
            U 13 (#5c2e63)
            L 3 (#c60ca2)
            D 11 (#43b263)
            L 2 (#c60ca0)
            D 2 (#69c963)
            L 4 (#3ef652)
            D 5 (#05a3a3)
            R 8 (#166472)
            D 3 (#a398f3)
            R 5 (#037ee2)
            D 3 (#4d21a3)
            R 4 (#a90f62)
            D 5 (#4ca5c3)
            R 8 (#a00b90)
            D 2 (#140e23)
            R 3 (#8d5c80)
            D 3 (#05fa23)
            R 10 (#78c0b0)
            D 6 (#603a53)
            R 6 (#4b7682)
            D 4 (#2b2333)
            R 7 (#7bc3a2)
            D 6 (#5eaa03)
            L 8 (#760b92)
            D 4 (#881bb3)
            L 3 (#8e47f2)
            D 4 (#297df3)
            L 8 (#56ad00)
            D 4 (#0408e3)
            L 5 (#56ade0)
            D 4 (#d627f3)
            L 12 (#2c1580)
            D 3 (#395e43)
            L 2 (#971660)
            D 3 (#815ff3)
            R 5 (#08d170)
            D 9 (#16e141)
            R 3 (#176840)
            U 9 (#9575f1)
            R 6 (#0cd362)
            D 4 (#266661)
            L 3 (#0cd360)
            D 10 (#b46e81)
            L 5 (#176842)
            D 8 (#0dc2f1)
            L 7 (#523570)
            D 3 (#6bd893)
            R 4 (#72c810)
            D 7 (#129a11)
            R 2 (#3d0540)
            D 8 (#5ea281)
            R 3 (#401cd2)
            U 6 (#1c7f41)
            R 2 (#be6752)
            U 7 (#1c7f43)
            R 2 (#53f6c2)
            U 2 (#4797a1)
            R 4 (#17f752)
            U 4 (#6ebfc1)
            R 9 (#3ce9a0)
            D 5 (#5f5a23)
            R 5 (#6b2ff0)
            D 9 (#263df3)
            L 5 (#7e0220)
            D 5 (#859811)
            R 3 (#445680)
            D 3 (#5672a1)
            L 4 (#40d370)
            D 9 (#59d2e1)
            L 3 (#5b3140)
            D 3 (#359a61)
            L 9 (#6899d0)
            D 4 (#140e21)
            L 4 (#380660)
            U 7 (#4ccd23)
            L 9 (#82a732)
            D 5 (#7983e3)
            L 8 (#82a730)
            D 8 (#3fb533)
            L 7 (#56cb72)
            D 7 (#588163)
            L 4 (#366172)
            U 13 (#543c33)""";
}
