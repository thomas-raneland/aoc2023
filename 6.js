const testInput = `Time:      7  15   30
Distance:  9  40  200`;

const input = `Time:        60     94     78     82
Distance:   475   2138   1015   1650`;

const times = parseLine(input, 0);
const distances = parseLine(input, 1);
const waysToWinProduct = times
  .map((time, i) => waysToWin(+time, +distances[i]))
  .reduce((a, b) => a * b, 1);
console.log(waysToWinProduct);

const time = parseLine(input, 0).join("");
const distance = parseLine(input, 1).join("");
console.log(waysToWin(+time, +distance));

function parseLine(input, line) {
  return input.split("\n")[line].split(/\s+/).slice(1);
}

function waysToWin(time, recordDistance) {
  let count = 0;

  for (let load = 0; load < time; load++) {
    if ((time - load) * load > recordDistance) {
      count++;
    }
  }

  return count;
}