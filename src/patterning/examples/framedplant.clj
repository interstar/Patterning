(ns patterning.examples.framedplant
  (:require [patterning.maths :as maths])
  (:require [patterning.sshapes :as sshapes])
  (:require [patterning.groups :refer :all])
  (:require [patterning.layouts :refer :all])
  (:require [patterning.complex_elements :refer :all])
  (:require [patterning.view :refer :all])
  (:require [patterning.color :refer :all])
  (:require [patterning.examples.design_language1 :refer :all])
  )



(def l-system-1 (l-system [["F" "F+G++G-F--FF-G+"]
                                 ["G" "-F+GG++G+F--F-G"]]))

(def sys-g1 (basic-turtle [0 0] 0.1 0 (/ maths/PI 3) (l-system-1 3 "F") {} {:color my-purple  :stroke-weight 1} ))

(def l-system-2 (l-system [["F" "F[+F]F[-F][GF]"] ["G" "H"] ["H" "IZ"] ]))
        
(def sys-g2 (basic-turtle [0 0] 0.1 (/ maths/PI -2) (/ maths/PI 9) (l-system-2 4 "F")
                          {\Z (fn [x y a] (let [] ( group (sshapes/poly x y 0.05 8 {:fill my-red}))))}
                          {:color my-green :stroke-weight 2}  ))

(def sprey (translate-group -0.6 0 (h-reflect-group (reframe-group sys-g2)))) 

(def square2 (stack (group (sshapes/make {:fill my-cream :color my-blue :stroke-weight 3}
                                   [[-1 -1] [-1 1] [1 1] [1 -1] [-1 -1]]))
                    less-complex-diamond
                    simple-clock  ))

(def inner (stack (reframe-group sys-g1)
                  (alt-rows-grid-layout
                   4 (repeat (checked-layout 2 (repeat flower) (repeat [])))
                   (cycle [sprey sprey (h-reflect-group sprey) (h-reflect-group sprey)]))))

(def trees (framed 6 (repeat corner) (repeat sprey) (checked-layout 4 (repeat pink-tile) (repeat flower )) ))


(comment 
  
  final-pattern-final-paper (framed 9 (repeat (scale-group 0.75 ( rotate-group (/ maths/PI 4) square2)))
                                    (cycle [square2 (scale-group 0.8 square2)]) inner))

        

(def framed-FASS (framed 9 (repeat (scale-group 0.75 (rotate-group (/ maths/PI 4) square2)))
                         (cycle [square2 (scale-group 0.8 square2)])
                         (stack (over-style-group {:fill (p-color 200 200 255)} [(sshapes/square)]) (reframe-group sys-g1)) ))

(def framed-plant (framed 9 (repeat flower) (repeat  square2) sprey ))
