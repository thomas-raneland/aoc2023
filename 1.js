const input =
`1six7396484
[...]
seven443six8three31`.split("\n");

function leftSum(a) {
	return a
    .map(line => line.match(/[0-9]/g))
    .map(matches => +matches[0])
    .reduce((sum, x) => sum + x);
}

function rightSum(a) {
  return a
    .map(line => line.match(/[0-9]/g))
    .map(matches => +matches[matches.length - 1])
    .reduce((sum, x) => sum + x);
}

console.log(leftSum(input) * 10 + rightSum(input));

function replace(s, i) {
  var digit = 1;

  for (var nbr of ["one", "two", "three", "four", "five", "six", "seven", "eight", "nine"]) {
    if (s.substring(i).startsWith(nbr)) {
      s = s.substring(0, i) + digit + s.substring(i + nbr.length);
    }

    digit++;
  }

  return s;
}

const replacedFromLeft = input
  .map(line => {
    for (var i = 0; i < line.length; i++) {
      line = replace(line, i);
    }

    return line;
  });

const replacedFromRight = input
  .map(line => {
    for (var i = line.length; i >= 0; i--) {
      line = replace(line, i);
    }

    return line;
  });

console.log(leftSum(replacedFromLeft) * 10 + rightSum(replacedFromRight));