var totalScore = 0;
var totalCount = 0;
const counts = new Map();

for (card of input.split("\n").map(line => parse(line))) {
  const count = counts.get(card.nbr) || 1;
  totalCount += count;

  const matches = card.winning.filter(w => card.mine.includes(w)).length;
  const score = matches ? Math.pow(2, matches - 1) : 0;
  totalScore += score;

  for (var i = card.nbr + 1; i < card.nbr + 1 + matches; i++) {
    counts.set(i, (counts.get(i) || 1) + count);
  }
}

console.log(`Part I: ${totalScore} and Part II: ${totalCount}`);

function parse(line) {
  const nbr = +line.substring("Card ".length, line.indexOf(":"));
  const lists = line.substring(line.indexOf(":") + 1).split("|");
  const [ winning, mine ] = lists.map(list => list.trim().split(/\s+/).map(n => +n));
  return { nbr, winning, mine };
}