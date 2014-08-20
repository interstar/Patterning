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

basics.clj gives a guided tour of the basic functions of Patterning 

to see each of the examples in action change src/patterning/core.clj 
so that final-pattern gets one of the patterns defined in basics.clj. 

Then re-run with 

    lein run

## License

Copyright © 2014 Phil Jones

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
