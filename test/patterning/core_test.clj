(ns patterning.core-test
  (:require [clojure.test :refer :all]
            [patterning.geometry :refer :all]
            [patterning.layouts :refer :all]
            [patterning.complex_elements :refer :all]
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

(deftest groups-flatten
  (let [s1 (sshape {} [[0 0] [1 1]])
        s2 (sshape {} [[2 2] [3 3]])
        g1 (group s1)
        g2 (group s1 s2)]
    (testing "extracting points"
      (is (= (extract-points s1)
             [[0 0] [1 1]]  ))
      (is (= (extract-points  (flatten-group {:colour 1} g1))
             [[0 0] [1 1]]))
      (is (= (extract-points (flatten-group {:colour 2} g2))
             [[0 0] [1 1] [2 2] [3 3]]) )
      )))
