(ns patterning.examples.framedplant
  (:require [patterning.maths :as maths] 
            [patterning.sshapes :refer [->SShape ]]
            [patterning.groups :refer [group translate h-reflect reframe scale rotate over-style]]
            [patterning.layouts :refer [stack alt-rows-grid-layout checked-layout framed]]
            [patterning.library.std :refer [poly square]]
            [patterning.library.turtle :refer [basic-turtle]]
            [patterning.library.l_systems :refer [l-system]]

            [patterning.color :refer [p-color]]
            [patterning.examples.design_language1 :refer [square2]]
            [patterning.examples.design_language1 :as design-language])  )



(def l-system-1 (l-system [["F" "F+G++G-F--FF-G+"]
                                 ["G" "-F+GG++G+F--F-G"]]))

(defn sys-g1 [] (basic-turtle [0 0] 0.1 0 (/ maths/PI 3) (l-system-1 3 "F") {} {:stroke design-language/my-purple  :stroke-weight 1} ))

(def l-system-2  (l-system [["F" "F[+F]F[-F][GF]"] ["G" "H"] ["H" "IZ"] ]))
        
(defn sys-g2 [] (basic-turtle [0 0] 0.1 (/ maths/PI -2) (/ maths/PI 9) (l-system-2 4 "F")
                          {\Z (fn [x y a] (let [] ( group (poly x y 0.05 8 {:fill design-language/my-red}))))}
                          {:stroke design-language/my-green :stroke-weight 2}  ))

(defn sprey [] (translate -0.6 0 (h-reflect (reframe (sys-g2))))) 



(defn inner [] (stack (reframe ( sys-g1))
                  (alt-rows-grid-layout
                   4 (repeat (checked-layout 2 (repeat design-language/flower) (repeat [])))
                   (cycle [sprey sprey (h-reflect sprey) (h-reflect sprey)]))))

(defn trees [] (framed 6 (repeat design-language/corner) (repeat (sprey)) (checked-layout 4 (repeat design-language/pink-tile)
                                                                                          (repeat design-language/flower )) ))


(comment 
  
  final-pattern-final-paper (framed 9 (repeat (scale 0.75 ( rotate (/ maths/PI 4) square2)))
                                    (cycle [square2 (scale 0.8 square2)]) inner))

        

(defn framed-FASS [] (framed 9 (repeat (scale 0.75 (rotate (/ maths/PI 4) (square2))))
                             (cycle [(square2) (scale 0.8 (square2))])
                             (stack [(square {:fill (p-color 200 200 255)} )] (reframe (sys-g1))) ))

(defn framed-plant [] (framed 9 (repeat design-language/flower) (repeat  (square2)) (sprey) ))


