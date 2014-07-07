(ns patterning.examples.framedplant
  (:require [patterning.maths :as maths])
  (:require [patterning.sshapes :refer [->SShape poly square]])
  (:require [patterning.groups :refer [group translate h-reflect reframe scale rotate over-style]])
  (:require [patterning.layouts :refer [stack alt-rows-grid-layout checked-layout framed]])
  (:require [patterning.complex_elements :refer [l-system basic-turtle]])
  (:require [patterning.view :refer :all])
  (:require [patterning.color :refer [p-color]])
  (:require [patterning.examples.design_language1 :as design-language])
  )



(def l-system-1 (l-system [["F" "F+G++G-F--FF-G+"]
                                 ["G" "-F+GG++G+F--F-G"]]))

(def sys-g1 (basic-turtle [0 0] 0.1 0 (/ maths/PI 3) (l-system-1 3 "F") {} {:color design-language/my-purple  :stroke-weight 1} ))

(def l-system-2 (l-system [["F" "F[+F]F[-F][GF]"] ["G" "H"] ["H" "IZ"] ]))
        
(def sys-g2 (basic-turtle [0 0] 0.1 (/ maths/PI -2) (/ maths/PI 9) (l-system-2 4 "F")
                          {\Z (fn [x y a] (let [] ( group (poly x y 0.05 8 {:fill design-language/my-red}))))}
                          {:color design-language/my-green :stroke-weight 2}  ))

(def sprey (translate -0.6 0 (h-reflect (reframe sys-g2)))) 

(def square2 (stack (group (->SShape {:fill design-language/my-cream :color design-language/my-blue :stroke-weight 3}
                                   [[-1 -1] [-1 1] [1 1] [1 -1] [-1 -1]]))
                    design-language/less-complex-diamond
                    design-language/simple-clock  ))

(def inner (stack (reframe sys-g1)
                  (alt-rows-grid-layout
                   4 (repeat (checked-layout 2 (repeat design-language/flower) (repeat [])))
                   (cycle [sprey sprey (h-reflect sprey) (h-reflect sprey)]))))

(def trees (framed 6 (repeat design-language/corner) (repeat sprey) (checked-layout 4 (repeat design-language/pink-tile) (repeat design-language/flower )) ))


(comment 
  
  final-pattern-final-paper (framed 9 (repeat (scale 0.75 ( rotate (/ maths/PI 4) square2)))
                                    (cycle [square2 (scale 0.8 square2)]) inner))

        

(def framed-FASS (framed 9 (repeat (scale 0.75 (rotate (/ maths/PI 4) square2)))
                         (cycle [square2 (scale 0.8 square2)])
                         (stack (over-style {:fill (p-color 200 200 255)} [(square)]) (reframe sys-g1)) ))

(def framed-plant (framed 9 (repeat design-language/flower) (repeat  square2) sprey ))
