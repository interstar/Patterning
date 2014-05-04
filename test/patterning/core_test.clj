(ns patterning.core-test
  (:require [clojure.test :refer :all]
            [patterning.geometry :refer :all]
            [patterning.core :refer :all]))

(deftest geometry-line-to-segments
  (testing "line-to-segments"
    (is (= (line-to-segments [])
           []))
    (is (= (line-to-segments [[0 0]])
           []))
    (is (= (line-to-segments [[0 0] [1 1]])
           [[ [0 0] [1 1] ]]))
    (is (= (line-to-segments [[0 0] [1 1] [2 2]])
           [[ [0 0] [1 1]] [[1 1] [2 2]]]))
    (is (= (line-to-segments [[0 0] [1 1] [2 2] [3 3]])
           [[ [0 0] [1 1]] [[1 1] [2 2]] [[2 2] [3 3]]]))
    ))

(deftest geometry-basic-points
  (testing "basic point functions"
    (is (= (diff [3 3] [5 5]) [2 2]))
    (is (= (add-points [1 1] [2 2]) [3 3]))
    (is (= (magnitude [3 4]) 5 ))
    (is (= (distance [0 0] [0 10]) 10))
    (is (p-eq (unit [10 0]) [1 0] ))
    ))
