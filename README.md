# Patterning

**Note : this Patterning repository is now deprecated**

Please look at the following links to learn more about and start using Patterning.

- [Patterning Core](https://github.com/interstar/Patterning-Core). The core library for generating patterns in your Clojure / ClojureScript projects. (The code is in cljx)

- Alternatively, you can just incorporate a compiled jar from Clojars [here](https://clojars.org/com.alchemyislands/patterning/versions/0.3.0-SNAPSHOT) in your own Clojure project.

- [a Quil / Processing wrapper](https://github.com/interstar/Patterning-Quil). This project shows how to write a short  Clojure program that uses Quil to render the patterns. This is the best place to start if you just want to use Patterning in your own Clojure projects.

- Allows artists who are more familiar with Processing itself and want to write Java in the Processing environment can now use [Patterning for Processing](https://github.com/interstar/Patterning-for-Processing). That project is the source-code for building a Processing Library that wraps Patterning.


More info [here](http://sdi.thoughtstorms.info/?p=901).

# DEPRECATED OLD README #


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
