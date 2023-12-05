const input = `seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4

water-to-light map:
88 18 7
18 25 70

light-to-temperature map:
45 77 23
81 45 19
68 64 13

temperature-to-humidity map:
0 69 1
1 0 69

humidity-to-location map:
60 56 37
56 93 4`;

const [seeds, rulesList] = parse(input);
const locations = convertSeedsToLocations(seeds);
console.log(locations.reduce((a,b) => Math.min(a, b)));

const seedRanges = toRanges(seeds);
const locationRanges = convertSeedRangesToLocationRanges(seedRanges);
console.log(locationRanges.reduce((a,b) => a.min < b.min ? a : b).min);

function parse(input) {
  let rulesList = [];
  let seeds;
  let rules;

  for (line of input.split("\n")) {
    if  (line.startsWith("seeds: ")) {
      seeds = line.substring("seeds: ".length).split(" ").map(n => +n);
    } else if (line.endsWith("map:")) {
      rules = [];
      rulesList.push(rules);
    } else if (line != "") {
     [ destStart, sourceStart, length ] = line.split(" ").map(n => +n);
     rules.push({ destStart, sourceStart, length });
    }
  }

  return [seeds, rulesList.map(rules => addDefaultRules(rules)) ];
}

function addDefaultRules(rules) {
  rules.sort((a,b) => a.sourceStart - b.sourceStart);
  let defaultRules = [];
  let start = 0;

  for (rule of rules) {
    if (rule.sourceStart > start) {
      defaultRules.push({ destStart: start, sourceStart: start, length: rule.sourceStart - start });
    }

    start = rule.sourceStart + rule.length;
  }

  defaultRules.push({destStart: start, sourceStart: start, length: Number.MAX_VALUE - start});
  const all = [...rules, ...defaultRules];
  all.sort((a, b) => a.sourceStart - b.sourceStart);
  return all;
}

function toRanges(sources) {
  const ranges = [];

  for (let i = 0; i < sources.length; i += 2) {
    ranges.push({ min: sources[i], max: sources[i] + sources[i+1] });
  }

  ranges.sort((a,b) => a.min - b.min);
  return ranges;
}

function convertSeedsToLocations(sources) {
  for (let rules of rulesList) {
    sources = sources.map(s => convert(rules, s));
  }

  return sources;
}

function convert(rules, source) {
  for (rule of rules) {
    if (source >= rule.sourceStart && source < rule.sourceStart + rule.length) {
      return rule.destStart + (source - rule.sourceStart);
    }
  }

  throw new Error("Cannot convert " + source);
}

function convertSeedRangesToLocationRanges(ranges) {
  for (rules of rulesList) {
    ranges = ranges.flatMap(s => convertRange(rules, s));
  }

  return ranges;
}

function convertRange(rules, range) {
  let destRanges = [];

  for (rule of rules) {
    if (range.min < rule.sourceStart + rule.length && range.max > rule.sourceStart) {
      const min = convert([rule], Math.max(range.min, rule.sourceStart));
      const max = convert([rule], Math.min(range.max, rule.sourceStart + rule.length) -1 ) +1;
      destRanges.push({ min, max });
    }
  }

  return destRanges;
}