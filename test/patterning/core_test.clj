(ns patterning.core-test
  (:require [clojure.test :refer :all]
            [patterning.maths :as maths]
            [patterning.maths :refer [mol= molv=]]
            [patterning.sshapes :as sshapes]
            [patterning.groups :as groups]
            [patterning.color :as color]
            [patterning.layouts :as layouts]
            [patterning.library.l_systems :as l-systems]
            [patterning.library.turtle :as turtle]
            [patterning.library.complex_elements :as complex-elements]            
            [patterning.core :refer :all]))

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

(deftest flatten-group
  (let [s1 (sshapes/->SShape {} [[0 0] [1 1]])
        s2 (sshapes/->SShape {} [[2 2] [3 3]])
        g1 (groups/group s1)
        g2 (groups/group s1 s2)]
    (testing "extracting points"
      (is (= (groups/extract-points s1)
             [[0 0] [1 1]]  ))
      (is (= (groups/extract-points  (groups/flatten-group {:color 1} g1))
             [[0 0] [1 1]]))
      (is (= (groups/extract-points (groups/flatten-group {:color 2} g2))
             [[0 0] [1 1] [2 2] [3 3]]) )
      )))

(deftest process
  (let [s1 (sshapes/->SShape {} [[0.2 0.2] [0.4 -0.4]])
        ]
    (testing "reframing"
      (is (= (sshapes/top s1) -0.4))
      (is (= (sshapes/leftmost s1) 0.2))
      (is (= (sshapes/bottom s1) 0.2))
      (is (= (sshapes/rightmost s1) 0.4))
      
      (is (mol= (sshapes/width s1) 0.2))
      (is (mol= (sshapes/height s1) 0.6))
      (is (mol= (groups/reframe-scaler s1) (/ 2.0 0.6)))
      
     )))

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

(deftest filtering
  (let [inside? (fn [[ x y]] (< (+ (* y y) (* x x)) 4 ))
        points [[0 0] [1 1] [2 2] [3 3]]
        gp (groups/group (sshapes/->SShape {} points) (sshapes/->SShape {} points))
        gp2 (groups/group {:style {} :points [[(- 1) (- 1)] [0 0]]} {:style {} :points [[0 0] [2 2] [4 4]]})
        ]
    (testing "filter points from shape, sshape and groups. filtering sshapes from group"
      (is (= (sshapes/filter-shape inside? []) []))
      (is (= (sshapes/filter-shape inside? points) [[0 0] [1 1]]))
      (is (= (sshapes/ss-filter inside? (sshapes/->SShape {} points)) (sshapes/->SShape {} [[0 0] [1 1]])))
      (is (= (groups/filter-group inside? gp) [  (sshapes/->SShape {} [[0 0] [1 1]])
                                                 (sshapes/->SShape {} [[0 0] [1 1]])]))
      (is (= (groups/filter-sshapes-in-group inside? gp2)
             [{:style {} :points [[-1 -1] [0 0]]}] ))
      )))

(deftest clipping
  (let [inside? (fn [[ x y]] (< (+ (* y y) (* x x)) 4 ))
        s (sshapes/->SShape {} [[0 0] [1 1] [2 2] [1 1] [0 0]])
        g [s s]]
    (testing "clipping. always returns a group of sshapes"
      (is (= (groups/clip-sshape inside? s)
             [{:style {} :points [[0 0] [1 1]]} {:style {} :points [[1 1] [0 0]]}]))
      (is (= (groups/clip inside? g)
             [{:style {} :points [[0 0] [1 1]]} {:style {} :points [[1 1] [0 0]]}
              {:style {} :points [[0 0] [1 1]]} {:style {} :points [[1 1] [0 0]]} ]) )
      )
    
    ))



