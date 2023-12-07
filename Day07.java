import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

public class Day07 {
    private static final String INPUT = """
            32T3K 765
            T55J5 684
            KK677 28
            KTJJT 220
            QQQJA 483""";

    public static void main(String... args) {
        for (boolean jForJoker : List.of(false, true)) {
            var hands = INPUT
                    .lines()
                    .map(line -> parseHand(line, jForJoker))
                    .sorted()
                    .toList();

            var winnings = IntStream.rangeClosed(1, hands.size())
                                    .mapToLong(rank -> hands.get(rank - 1).bid() * rank)
                                    .sum();

            System.out.println(winnings);
        }
    }

    private static Hand parseHand(String line, boolean jForJoker) {
        var parts = line.split(" ");
        var bid = Long.parseLong(parts[1]);
        var cards = parts[0].chars()
                            .mapToObj(ch -> Card.valueOf((Character.isDigit(ch) ? "C" : "") + (char) ch))
                            .map(card -> jForJoker && card == Card.J ? Card.Joker : card)
                            .toList();

        return new Hand(cards, bid);
    }

    @SuppressWarnings("unused")
    enum Card {
        Joker, C2, C3, C4, C5, C6, C7, C8, C9, T, J, Q, K, A
    }

    enum Type {
        HIGHEST, PAIR, TWO_PAIRS, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND
    }

    record Hand(List<Card> cards, long bid) implements Comparable<Hand> {
        @Override
        public int compareTo(Hand o) {
            return IntStream.concat(IntStream.of(type().compareTo(o.type())),
                                    IntStream.range(0, cards.size())
                                             .map(i -> cards.get(i).compareTo(o.cards.get(i))))
                            .filter(i -> i != 0)
                            .findFirst()
                            .orElse(0);
        }

        private Type type() {
            var counts = new HashMap<Card, Integer>();
            cards.forEach(c -> counts.merge(c, 1, Integer::sum));
            var jokers = counts.remove(Card.Joker);

            if (jokers != null) {
                Card highest = counts.keySet().stream().max(Comparator.comparingInt(counts::get)).orElse(Card.Joker);
                counts.merge(highest, jokers, Integer::sum);
            }

            var maxCount = counts.values().stream().mapToInt(i -> i).max().orElse(0);

            return switch (counts.size()) {
                case 1 -> Type.FIVE_OF_A_KIND;
                case 2 -> maxCount == 4 ? Type.FOUR_OF_A_KIND : Type.FULL_HOUSE;
                case 3 -> maxCount == 3 ? Type.THREE_OF_A_KIND : Type.TWO_PAIRS;
                case 4 -> Type.PAIR;
                default -> Type.HIGHEST;
            };
        }
    }
}
