(ns patterning.examples.interactive
  (:require [patterning.maths :as maths] 
            [patterning.sshapes :refer [->SShape poly ]]
            [patterning.groups :refer [group clip rotate scale]]
            [patterning.layouts :refer [clock-rotate stack flower-of-life-positions place-groups-at-positions framed]]
            [patterning.library.turtle :refer [basic-turtle]]
           
            [patterning.color :refer [p-color]]
            [patterning.examples.design_language1 :as design-language]
            [patterning.examples.basics :as basics]
            [patterning.library.symbols :as symbols]
            )  )

; "Interactive" examples ... these are paramaterised shapes designed
; to be called from Javascript 

(defn framed-fol [frame-size rotated] (stack basics/m2 (scale 0.7 (symbols/folexample)) )
  (framed frame-size (repeat (clock-rotate rotated basics/dline))
           (repeat basics/triangles) (symbols/folexample))
  )
