(defproject net.synaesmedia/patterning "0.2.0-SNAPSHOT"
  :description "Generating Patterns with Clojure / Quil"
  :url "http://synaesmedia.net/patterning"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/math.numeric-tower "0.0.4"]                 
                 [quil "1.7.0"]
                 [com.taoensso/timbre "3.2.0"]
                 ]

  :plugins [[com.keminglabs/cljx "0.4.0"]
            [lein-cljsbuild "1.0.3"]
            [lein-localrepo "0.4.0"]
            ]

  :cljx {:builds [{:source-paths ["cljx-src"]
                 :output-path "target/classes"
                 :rules :clj}

                {:source-paths ["cljx-src"]
                 :output-path "src-cljs"
                 :rules :cljs}]}

  :cljsbuild {:builds [{
                        :source-paths ["target/classes" "src-cljs" ]
                        :compiler { 
                                   :output-to "browser-based/js/main.js"
                                   :optimizations :whitespace
                                   :pretty-print true }
                        } ]}

  :hooks [cljx.hooks leiningen.cljsbuild]

  
  :aot [patterning.core]
  :main patterning.core)
   
