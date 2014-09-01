(ns patterning.library.turtle-test
  (:require [clojure.test :refer :all]
            [patterning.maths :as maths]
            [patterning.maths :refer [mol= molp=]]
            [patterning.sshapes :as sshapes]
            [patterning.groups :as groups]
            [patterning.color :as color]
            [patterning.layouts :as layouts]
            [patterning.library.l_systems :as l-systems]
            [patterning.library.turtle :as turtle]

            [patterning.core :refer :all]))


(deftest l-system-testing
  (let [rule1 ["A" "B"]
        rule2 ["A" "DE"]
        rule3 ["D" "CE"]
        rule4 ["A" "AB"]
        rule5 ["B" "C"] ]
    (testing "basic string sub"
      (is (= (l-systems/apply-rule-to-char rule1 "A")
             "B"))
      (is (= (l-systems/apply-rule-to-char rule1 "C")
             "C"))
      (is (= (l-systems/apply-rule-to-char rule2 "A")
             "DE"))
      )
    
    (testing "rules on a string"
      (is (= (l-systems/apply-rules-to-char [rule1] "A") "B") )
      (is (= (l-systems/apply-rules-to-char [rule1] "C") "C"))
      (is (= (l-systems/apply-rules [rule1] "ADAM") "BDBM"))
      (is (= (l-systems/apply-rules [rule1 rule3] "ADAM") "BCEBM"))
      (is (= (l-systems/apply-rules [rule4 rule5] "A") "AB"))
      (is (= (l-systems/apply-rules [rule4 rule5] "AB") "ABC"))
      (is (= (l-systems/multi-apply-rules 2 [rule4 rule5] "A") "ABC" ))
      (is (= (l-systems/multi-apply-rules 4 [rule4 rule5] "A") "ABCCC"))
      (let [ls (l-systems/l-system [rule4 rule5])]
        (is (= (ls 2 "A") "ABC")))
      )

    (testing "string to group"
      (is (= (turtle/basic-turtle [0 0] 0.1 0 0 "F" {} {:stroke "red"})
             [(sshapes/->SShape {:stroke "red"} [[0 0] [0.1 0.0]])]))
      
      (let [stg (turtle/basic-turtle [0 0] 0.1 0 1.5707963705062866 "F+F" {} {} )
            ps (get (first stg) :points)] 
        (is (= (first ps) [0 0]))
        (is (= (get ps 1) [0.1 0.0]))
        (is (mol= (second (get ps 2)) 0.1))
        )
      )
    
    (is (= (second (turtle/l-string-turtle-to-group-r [0 0] 0.1 0 1.5707963705062866 "F" {} {} ))
           [(sshapes/->SShape {} [[0 0] [0.1 0.0]])]))
      
    (let [stg (second (turtle/l-string-turtle-to-group-r [0 0] 0.1 0 1.5707963705062866 "F+F" {} {}))
            ps (get (first stg) :points)]
        (is (= (first ps) [0 0]))
        (is (= (get ps 1) [0.1 0.0]))
        (is (molp= (get ps 2) [0.1  0.1]))
        )
      
    (let [leaf (fn [x y a] (let [] (println "in leaf function") (groups/group ( sshapes/->SShape {} [[-10 -10]]))))
          stg (second (turtle/l-string-turtle-to-group-r [0 0] 0.1 0 1.5707963705062866 "F+F[FF]Z" {\Z leaf} {}))
            s2 (get stg 0)
            s3 (get stg 1)
            s1 (get stg 2)
            ps1 (get s1 :points)
            ps2 (get s2 :points)
            ps3 (get s3 :points)
            ]
        
        (is (= (count stg) 3))
        (is (= (count ps1) 3))
        (is (= (first ps1) [0 0]))
        (is (= (get ps1 1) [0.1 0.0]))
        (is (mol= (second (get ps1 2)) 0.1))
        (is (= (count ps2) 3))
        (is (= (first ps3) [-10 -10]))
        )
      
    ))
