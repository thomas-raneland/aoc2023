const xyToNbrIds = new Map();
const nbrIdToNbrs = new Map();
let nbrId = 1;

for (let y = 0; y < input.length; y++) {
  for (let x = 0; x < input[y].length; x++) {
    if ("0123456789".indexOf(input[y][x]) != -1) {
      const start = x;

      while (x < input[y].length && "0123456789".indexOf(input[y][x]) != -1) {
     	xyToNbrIds.set({x,y}, nbrId);
        x++;
      }

      nbrIdToNbrs.set(nbrId, +input[y].substring(start, x));
      nbrId++;
    }
  }
}

let found = new Set();

xyToNbrIds.forEach((id, xy) => {
  for (let y = xy.y-1; y < xy.y+2; y++) {
    for (let x = xy.x-1; x < xy.x+2; x++) {
      if (y >= 0 && x >= 0 && y < input.length && x < input[y].length) {
   		if (".0123456789".indexOf(input[y][x]) === -1) {
       	  found.add(id);
      	}
      }
    }
  }
});

let output1 = 0;
found.forEach(id => output1 += nbrIdToNbrs.get(id));
console.log(output1);

let output2 = 0;

for (let y = 0; y < input.length; y++) {
  for (let x = 0; x < input[y].length; x++) {
    if (input[y][x] === '*') {
      let around = near(x,y,input,xyToNbrIds);

  	  if (around.size == 2) {
        const values = Array.from(around.values());
        output2 += nbrIdToNbrs.get(values[0])*nbrIdToNbrs.get(values[1]);
      }
    }
  }
}

console.log(output2);

function near(ox, oy, input, xyToNbrIds) {
  var found = new Set();

  for (let y = oy-1; y < oy+2; y++) {
    for (let x = ox-1; x < ox+2; x++) {
      if (y >= 0 && x >= 0 && y < input.length && x < input[y].length) {
        xyToNbrIds.forEach((id, xy) => {
          if (xy.x == x && xy.y == y) {
   			found.add(id);
          }
      	});
      }
    }
  }

  return found;
}