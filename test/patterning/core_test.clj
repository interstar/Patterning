(ns patterning.core-test
  (:require [clojure.test :refer :all]
            [patterning.geometry :refer :all]
            [patterning.color :refer :all]
            [patterning.layouts :refer :all]
            [patterning.complex_elements :refer :all]
            [patterning.core :refer :all]))

(defn mol= "more or less equal" [x y] (< (Math/abs (- x y)) 0.0000001) )
(defn molv= "more or less equal vectors" [[x1 y1] [x2 y2]] (and (mol= x1 x2) (mol= y1 y2)))

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

(deftest geometry-flatten-point-list
  (let [ss (sshape {} [[0 0] [1 1] [2 2]])  ] 
    (testing "flatten-point-list"
      (is (= (flat-point-list ss)
             (list 0 0 1 1 2 2))))))

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

(deftest groups-process
  (let [s1 (sshape {} [[0.2 0.2] [0.4 -0.4]])
        ]
    (testing "reframing"
      (is (= (top-sshape s1) -0.4))
      (is (= (left-sshape s1) 0.2))
      (is (= (bottom-sshape s1) 0.2))
      (is (= (right-sshape s1) 0.4))
      
      (is (mol= (width-sshape s1) 0.2))
      (is (mol= (height-sshape s1) 0.6))
      (is (mol= (reframe-scaler s1) (/ 2.0 0.6)))
      
     )))

(deftest color-stuff
  (let []
    (testing "color sequence"
      (is (= (color-seq ["red" "blue"])
             (list {:color "red"} {:color "blue"}) ))
      )
    (testing "color to fill"
      (is (= (color-to-fill {:color "red" :other "blah"})
             {:color "red" :fill "red" :other "blah"} ) ))
    
    (testing "setup-colors"
      (is (= ( ( edge-col "red") {:color "green"})
             {:color "red" :fill "green"} ))
      
      (is (= (setup-colors ["red" "blue"] "black")
             (list {:fill "red" :color "black"} {:fill "blue" :color "black"}) )))
    ))



(deftest l-system-testing
  (let [rule1 ["A" "B"]
        rule2 ["A" "DE"]
        rule3 ["D" "CE"]
        rule4 ["A" "AB"]
        rule5 ["B" "C"] ]
    (testing "basic string sub"
      (is (= (apply-rule-to-char rule1 "A")
             "B"))
      (is (= (apply-rule-to-char rule1 "C")
             "C"))
      (is (= (apply-rule-to-char rule2 "A")
             "DE"))
      )
    
    (testing "rules on a string"
      (is (= (apply-rules-to-char [rule1] "A") "B") )
      (is (= (apply-rules-to-char [rule1] "C") "C"))
      (is (= (apply-rules [rule1] "ADAM") "BDBM"))
      (is (= (apply-rules [rule1 rule3] "ADAM") "BCEBM"))
      (is (= (apply-rules [rule4 rule5] "A") "AB"))
      (is (= (apply-rules [rule4 rule5] "AB") "ABC"))
      (is (= (multi-apply-rules 2 [rule4 rule5] "A") "ABC" ))
      (is (= (multi-apply-rules 4 [rule4 rule5] "A") "ABCCC"))
      (let [ls (l-system [rule4 rule5])]
        (is (= (ls 2 "A") "ABC")))
      )

    (testing "string to group"
      (is (= (l-string-to-group [0 0] 0.1 0 0 "F" {:color "red"})
             [{:style {:color "red"} :points [[0 0] [0.1 0.0]]}]))
      
      (let [stg (l-string-to-group [0 0] 0.1 0 1.5707963705062866 "F+F")
            ps (get (first stg) :points)] 
        (is (= (first ps) [0 0]))
        (is (= (get ps 1) [0.1 0.0]))
        (is (mol= (second (get ps 2)) 0.1))
        )
      )
    
    (is (= (second (l-string-turtle-to-group-r [0 0] 0.1 0 1.5707963705062866 "F" ))
             [{:style {} :points [[0 0] [0.1 0.0]]}]))
      
    (let [stg (second (l-string-turtle-to-group-r [0 0] 0.1 0 1.5707963705062866 "F+F"))
            ps (get (first stg) :points)]
        (is (= (first ps) [0 0]))
        (is (= (get ps 1) [0.1 0.0]))
        (is (molv= (get ps 2) [0.1  0.1]))
        )
      
    (let [stg (second (l-string-turtle-to-group-r [0 0] 0.1 0 1.5707963705062866 "F+F[FF]" ))
            s2 (get stg 0)
            s1 (get stg 1)
            ps1 (get s1 :points)
            ps2 (get s2 :points)
            ]
        (println stg)
        (println "ps1 " ps1)
        (println "ps2 " ps2)
        
        (is (= (count stg) 2))
        (is (= (count ps1) 3))
        (is (= (first ps1) [0 0]))
        (is (= (get ps1 1) [0.1 0.0]))
        (is (mol= (second (get ps1 2)) 0.1))
        (is (= (count ps2) 3))
        )
      
    ))

