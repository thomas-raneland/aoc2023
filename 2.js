const output1 = input
  .map(line => parse(line))
  .filter(game => possible(game))
  .reduce((sum, game) => sum + game.id, 0);

console.log(output1);

const output2 = input
  .map(line => parse(line))
  .map(game => power(game))
  .reduce((a,b) => a + b);

console.log(output2);

function parse(line) {
  return {
  	id: +line.split(":")[0].substring(5),
    rounds: line.split(":")[1].substring(1).split("; ")
      .map(round => round.split(", ")
        .map(cc => ({
     	  count: +cc.split(" ")[0],
          color: cc.split(" ")[1]
        }))
      )
  };
}

function possible(game) {
  const valid = cc =>
  	cc.color === "red" && cc.count <= 12 ||
  	cc.color === "green" && cc.count <= 13 ||
  	cc.color === "blue" && cc.count <= 14;

  return !game.rounds.flat().some(cc => !valid(cc));
}

function power(game) {
  const max = new Map();

  for (let round of game.rounds) {
    for (let cc of round) {
      max.set(cc.color, Math.max(cc.count, max.get(cc.color) || 0));
    }
  }

  return Array.from(max.values()).reduce((a,b) => a * b, 1);
}