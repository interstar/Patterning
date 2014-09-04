(ns patterning.groups-test
  (:require [clojure.test :refer :all]
            [patterning.maths :as maths]
            [patterning.maths :refer [mol= molp=]]
            [patterning.sshapes :as sshapes]
            [patterning.groups :as groups]
))

(deftest more-or-less-equal-groups
  (let [g1 (groups/group (sshapes/->SShape {} [[0 0] [1 1]] ))
        g2 (groups/group (sshapes/->SShape {} [[0 0] [2 2]]))]
    (testing "more or less equal groups"
      (is (true? (groups/mol= (groups/empty-group) (groups/empty-group))))
      (is (false? (groups/mol= (groups/empty-group) g1) ))
      (is (false? (groups/mol= g1 g2)))
      (is (true? (groups/mol= g1 g1)))
      )
    )
  )


(deftest simple-transforms
  (let [g1 (groups/group (sshapes/->SShape {} [[0 0] [1 1]] ) (sshapes/->SShape {} [[-1 -1]] ) )
        g2 (groups/group (sshapes/->SShape {} [[1 1] [2 2]] ) (sshapes/->SShape {} [[0 0]] ) ) ]
    (testing "translate"
      (is (groups/mol= (groups/translate 1 1 g1) g2)))
    )
  )

(deftest flatten-group
  (let [s1 (sshapes/->SShape {} [[0 0] [1 1]])
        s2 (sshapes/->SShape {} [[2 2] [3 3]])
        g1 (groups/group s1)
        g2 (groups/group s1 s2)]
    (testing "extracting points"
      (is (= (groups/extract-points s1)
             [[0 0] [1 1]]  ))
      (is (= (groups/extract-points  (groups/flatten-group {:stroke 1} g1))
             [[0 0] [1 1]]))
      (is (= (groups/extract-points (groups/flatten-group {:stroke 2} g2))
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



