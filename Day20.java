import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Day20 {
    public static void main(String... args) {
        for (String input : List.of(TEST_INPUT, TEST_INPUT2, INPUT)) {
            var modules = parse(input);
            var states = new HashMap<String, State>();

            for (var m : modules.values()) {
                for (var r : m.receivers()) {
                    states.put(r, states.getOrDefault(r, State.OFF).updateMemory(m.name(), Pulse.LOW));
                }
            }

            partI(modules, states);
            partII(modules, states);
        }
    }

    private static void partI(Map<String, Module> modules, Map<String, State> states) {
        long low = 0;
        long high = 0;

        for (int pushes = 0; pushes < 1000; pushes++) {
            List<Signal> signals = pushButton(modules, states);

            for (var signal : signals) {
                if (signal.pulse() == Pulse.LOW) {
                    low++;
                } else {
                    high++;
                }
            }
        }

        System.out.println("Part I: " + low * high);
    }

    static void partII(Map<String, Module> modules, Map<String, State> states) {
        var dependencies = new HashMap<String, Set<String>>();

        for (var m : modules.values()) {
            for (var r : m.receivers()) {
                dependencies.merge(r, Set.of(m.name()), AocUtils::union);
            }
        }

        System.out.println("Part II: " + cycleLength("rx", states, modules, dependencies, new HashSet<>(), new HashMap<>()));
    }

    static long cycleLength(String name, Map<String, State> states, Map<String, Module> modules,
                            Map<String, Set<String>> dependencies, Set<String> seen, Map<String, Long> cache) {

        if (cache.containsKey(name)) {
            return cache.get(name);
        }

        long cycleLength = Integer.MAX_VALUE;

        if (seen.add(name)) {
            cycleLength = AocUtils.lcm(dependencies
                    .getOrDefault(name, Set.of())
                    .stream()
                    .mapToLong(dep -> cycleLength(dep, states, modules, dependencies, seen, cache)));
        } else { // cyclic dependency - cannot calculate
            var original = new HashMap<>(states);
            var current = new HashMap<>(original);
            var allDependenciesForName = allDependencies(name, dependencies);

            for (int pushes = 1; pushes < Integer.MAX_VALUE; pushes++) {
                pushButton(modules, current);

                if (equalSubMaps(original, current, allDependenciesForName)) {
                    cycleLength = pushes;
                    break;
                }
            }
        }

        cache.put(name, cycleLength);
        return cycleLength;
    }

    static Set<String> allDependencies(String name, Map<String, Set<String>> dependencies) {
        var seen = new HashSet<String>();
        var queue = new LinkedList<String>();
        queue.add(name);

        while (!queue.isEmpty()) {
            var dep = queue.poll();

            if (seen.add(dep)) {
                queue.addAll(dependencies.getOrDefault(dep, Set.of()));
            }
        }

        return seen;
    }

    static boolean equalSubMaps(Map<String, State> a, Map<String, State> b, Set<String> subsets) {
        return subsets.stream().allMatch(k -> Objects.equals(a.get(k), b.get(k)));
    }

    static List<Signal> pushButton(Map<String, Module> modules, Map<String, State> states) {
        var history = new ArrayList<Signal>();
        var queue = new LinkedList<Signal>();
        queue.add(new Signal("button", Pulse.LOW, "broadcaster"));

        while (!queue.isEmpty()) {
            Signal signal = queue.poll();
            history.add(signal);

            if (!signal.receiver().equals("output")) {
                if (modules.containsKey(signal.receiver())) {
                    var receiver = modules.get(signal.receiver());
                    var out = receiver.type().process(receiver.name(), states, signal.sender(), signal.pulse());

                    if (out != null) {
                        for (var r : receiver.receivers()) {
                            queue.offer(new Signal(receiver.name(), out, r));
                        }
                    }
                }
            }
        }

        return history;
    }

    static Map<String, Module> parse(String input) {
        return input
                .lines()
                .map(line -> {
                    var parts = line.split(" -> ");
                    var name = parts[0];
                    var receivers = List.of(parts[1].split(", "));

                    if (name.equals("broadcaster")) {
                        return new Module(name, ModuleType.BROADCAST, receivers);
                    } else if (name.startsWith("%")) {
                        return new Module(name.substring(1), ModuleType.FLIP_FLOP, receivers);
                    } else if (name.startsWith("&")) {
                        return new Module(name.substring(1), ModuleType.CONJUNCTION, receivers);
                    }

                    throw new IllegalArgumentException();
                })
                .collect(Collectors.toMap(Module::name, m -> m));
    }

    record State(boolean on, Map<String, Pulse> memory) {
        static final State OFF = new State(false, new HashMap<>());

        State flip() {
            return new State(!on, memory);
        }

        State updateMemory(String sender, Pulse pulse) {
            var newMemory = new HashMap<>(memory);
            newMemory.put(sender, pulse);
            return new State(on, newMemory);
        }
    }

    record Module(String name, ModuleType type, List<String> receivers) {}

    enum ModuleType {
        FLIP_FLOP,
        CONJUNCTION,
        BROADCAST;

        Pulse process(String name, Map<String, State> states, String sender, Pulse pulse) {
            return switch (this) {
                case FLIP_FLOP -> {
                    if (pulse == Pulse.HIGH) {
                        yield null;
                    } else {
                        var state = states.get(name);

                        if (state.on()) {
                            states.put(name, state.flip());
                            yield Pulse.LOW;
                        } else {
                            states.put(name, state.flip());
                            yield Pulse.HIGH;
                        }
                    }
                }
                case CONJUNCTION -> {
                    var state = states.get(name).updateMemory(sender, pulse);
                    states.put(name, state);
                    yield state.memory().values().stream().allMatch(p -> p == Pulse.HIGH) ? Pulse.LOW : Pulse.HIGH;
                }
                case BROADCAST -> pulse;
            };
        }
    }

    record Signal(String sender, Pulse pulse, String receiver) {}

    enum Pulse {LOW, HIGH}

    static final String TEST_INPUT = """
            broadcaster -> a, b, c
            %a -> b
            %b -> c
            %c -> inv
            &inv -> a""";

    static final String TEST_INPUT2 = """
            broadcaster -> a
            %a -> inv, con
            &inv -> b
            %b -> con
            &con -> output""";

    static final String INPUT = """
            &pr -> pd, vx, vn, cl, hm
            %hm -> qb
            %nm -> dh, jv
            %lv -> jv, tg
            %dg -> tm, jm
            %mt -> jv, zp
            &ln -> kj
            &kj -> rx
            &dr -> kj
            %dx -> ts
            &qs -> kf, dr, sc, rg, gl, dx
            %dh -> jv, mc
            %rg -> qs, vq
            %kt -> jv, mt
            %lh -> qs, dl
            %tp -> pf, jm
            %bf -> vx, pr
            %mv -> qs, gl
            %ts -> ng, qs
            %kf -> dx
            %gv -> jm, km
            %dl -> qs
            %nd -> dg
            %km -> jm
            %ns -> pr, pn
            %gl -> kf
            %pd -> pr, jp
            %xv -> nd, jm
            %hf -> nm
            %vx -> ns
            %vq -> bs, qs
            %sc -> mv
            &jv -> hj, rc, kt, ln, zp, hf
            %rc -> hj
            %jp -> mx, pr
            %mf -> gv, jm
            &zx -> kj
            %tg -> jv
            %bs -> sc, qs
            %ng -> qs, lh
            %tk -> pr
            %qb -> bf, pr
            %pn -> pr, cb
            %cl -> hm
            %pb -> tp
            broadcaster -> kt, pd, xv, rg
            &jm -> pb, tm, zx, mk, xv, nd
            %vc -> jv, hf
            %mc -> jv, lv
            %mk -> pb
            %tm -> mh
            %cb -> pr, tk
            %hj -> vc
            %zp -> rc
            %mh -> mk, jm
            %pf -> mf, jm
            %mx -> cl, pr
            &vn -> kj""";
}
