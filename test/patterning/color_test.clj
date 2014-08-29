(ns patterning.color-test
  (:require [clojure.test :refer :all]
            [patterning.color :as color]
))

(deftest color-stuff
  (let []
    (testing "color sequence"
      (is (= (color/color-seq ["red" "blue"])
             (list {:color "red"} {:color "blue"}) ))
      )
    (testing "color to fill"
      (is (= (color/color-to-fill {:color "red" :other "blah"})
             {:color "red" :fill "red" :other "blah"} ) ))
    
    (testing "setup-colors"
      (is (= ( ( color/edge-col "red") {:color "green"})
             {:color "red" :fill "green"} ))
      
      (is (= (color/setup-colors ["red" "blue"] "black")
             (list {:fill "red" :color "black"} {:fill "blue" :color "black"}) )))
    ))





