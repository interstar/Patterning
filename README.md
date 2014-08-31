# Patterning

A Clojure / Quil system for generating patterns, both at the small scale and the "layout" of smaller units. Units can be recursively nested.

## Quick Start
Make sure you have [Clojure](http://clojure.org/), [Leiningen](http://leiningen.org/) and [Quil](https://github.com/quil/) installed.

    git clone https://github.com/interstar/Patterning.git patterning
    cd patterning
    lein run

The code to generate the pattern is in src/patterning/core.clj

To run unit tests.

    lein test
   
To run the REPL.

    lein repl


## Examples
Look in the cljx-src/patterning/examples/ directory for examples

[The tutorial](http://alchemyislands.com/tutorial/tutorial.html) gives a guided tour of the basic functions of Patterning. The code for this tutorial can be found in examples/tutorial.clj

to see each of the examples in action change src/patterning/core.clj to assign the pattern that's created to "final-pattern".

For example : 

    (def final-pattern tutorial/triangles)


Then re-run with 

    lein run


See [Alchemy Islands](http://alchemyislands.com) for more examples and discussion of Patterning.

## License

Copyright © 2014 Phil Jones

Distributed under the [Gnu Affero General Public License](http://www.gnu.org/licenses/agpl.html) 
either version 3.0 or (at your option) any later version.
