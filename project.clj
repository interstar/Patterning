(defproject patterning "0.1.0-SNAPSHOT"
  :description "Generating Patterns with Clojure / Quil"
  :url "http://synaesmedia.net/patterning"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [quil "1.7.0"]]
  :aot [patterning.core]
  :main patterning.core)  
   
