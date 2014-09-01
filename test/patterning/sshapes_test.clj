(ns patterning.sshapes-test
  (:require [clojure.test :refer :all]
            [patterning.maths :as maths]
            [patterning.maths :refer [mol= molp=]]
            [patterning.sshapes :as sshapes]           ))

(deftest line-to-segments
  (testing "line-to-segments"
    (is (= (maths/line-to-segments [])
           []))
    (is (= (maths/line-to-segments [[0 0]])
           []))
    (is (= (maths/line-to-segments [[0 0] [1 1]])
           [[ [0 0] [1 1] ]]))
    (is (= (maths/line-to-segments [[0 0] [1 1] [2 2]])
           [[ [0 0] [1 1]] [[1 1] [2 2]]]))
    (is (= (maths/line-to-segments [[0 0] [1 1] [2 2] [3 3]])
           [[ [0 0] [1 1]] [[1 1] [2 2]] [[2 2] [3 3]]]))
    ))

(deftest flatten-point-list
  (let [ss (sshapes/->SShape {} [[0 0] [1 1] [2 2]])  ] 
    (testing "flatten-point-list"
      (is (= (sshapes/flat-point-list ss)
             (list 0 0 1 1 2 2))))))

(deftest basic-points
  (testing "basic point functions"
    (is (= (maths/diff [3 3] [5 5]) [2 2]))
    (is (= (maths/add-points [1 1] [2 2]) [3 3]))
    (is (= (maths/magnitude [3 4]) 5.0 ))
    (is (= (maths/distance [0 0] [0 10]) 10.0))
    (is (maths/p-eq (maths/unit [10 0]) [1 0] ))
    ))









