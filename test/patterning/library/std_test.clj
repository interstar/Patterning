(ns patterning.library.std-test
  (:require [clojure.test :refer :all]          
            [patterning.maths :as maths]
            [patterning.groups :refer [mol=]]
            [patterning.library.std :refer [poly bez-curve]]

))

(deftest test-std-library
  (let []
    (testing "bezier"
      (is (= 
           (bez-curve [[-0.9 0] [0.8 0.8] [-0.5 -0.8] [0.6 -0.5]] {} )
           [{:points [[-0.9 0] [0.8 0.8] [-0.5 -0.8] [0.6 -0.5]] 
             :style {:bezier true}}]
           ))
      )
    (testing "primitives"               
      (is (mol= (poly 0 0 0.5 4 {})
             [#patterning.sshapes.SShape{:style {}, :points [[-0.5 -6.123233995736766E-17] [2.1855695062163546E-8 -0.4999999999999995] [0.4999999999999981 4.3711390063094715E-8] [-6.556708506402579E-8 0.4999999999999957] [-0.5 -6.123233995736766E-17]]}]
)))))
