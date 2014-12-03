# Patterning

*A Clojure / Quil system for generating patterns, both at the small scale and the "layout" of smaller units. Units can be recursively nested.*

Note : this Patterning repository is getting deprecated. I'm refactoring Patterning into separate projects :

- a core which is purely Clojure (cljx) that has no Quil or Processing dependencies. This can be used to build the Patterning library in either Java or Javascript. [It's here](https://github.com/interstar/Patterning-Core). Or you can just incorporate a compiled jar from Clojars [here](https://clojars.org/com.alchemyislands/patterning/versions/0.3.0-SNAPSHOT).

- a Quil / Processing wrapper. The project with quilview.clj is moving [here](https://github.com/interstar/Patterning-Quil).

- a Java wrapper designed to be used to call Patterning from Processing itself. Allows artists who are familiar with the Processing language and environment to engage Patterning.

- a way to build interactive browser-based Javascript apps. that use Patterning.

More info [here](http://sdi.thoughtstorms.info/?p=901).

If, today, you just want to check out Patterning, you might still download and play with this project. But all new development will be taking place in those new projects, so fork / watch them rather than this.

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
