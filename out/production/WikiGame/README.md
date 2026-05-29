# WikiGame

There’s a wikipedia article to explain it! [Read up](https://en.wikipedia.org/wiki/Wikipedia:Wiki_Game).


This repository contains some skeleton code to start you off. Feel free to use it or not. Feel free to use it and change it. There are many different approaches that will work.

### Your goal
Code a slightly simpler variation of the game, one where you give your program the wikipedia pages for two actors, and the program outputs a path of links to get from one to the other, as well as the number of clicks the path took (the "depth").

You should start by getting it to find a very obvious link from the first page before trying more distant searches.

I also recommend starting with just `System.out.println` to see the link path before you try to save `path` as a variable of any kind.

For the recursion, think about what you want your base case (or cases) to be, and what your general recursion step will look like.

### Basic stuff you need
- HTML reading
- String parsing
- Recursion
- Program to successfully find and output a path between actors

### Extra things to boost your grade from a B+ to something higher
- UI (user interface) (similar to the one from the link puller assignment)
- Shortest path (program not only gives a path, but it gives the best path) (you may want to read about [depth search vs breadth search](https://www.codecademy.com/article/tree-traversal), though there are also other ways to do it)
- Efficiency (generallly, the faster the program runs the better)
- Expand beyond actors (any page to any page) (do not attempt until you have achieved high efficiency)
