(ns patterning.quil.quilview
  (:require [quil.core :refer :all])
  (:require [patterning.sshapes :as sshapes])
  (:require [patterning.groups :refer []])
  (:require [patterning.color :refer []])
  (:require [patterning.view :refer [transformed-sshape make-txpt xml-tpl ]])
  )


;; Interactive Bit. Only this bit should be Quil / Processing dependent. 

;; we are now using our p-color to represent colors, this converts it
;; into Processing format
(defn mk-color [[r g b a]] (color r g b a))

(defn draw-sshape "using vertices"
  [txpt {:keys [style points] :as sshape}]
  (if (sshapes/hidden? sshape) ()
      (let [tsshape (transformed-sshape txpt sshape)]    
        (push-style)
        (if (contains? style :stroke) (stroke  (mk-color (get style :stroke))))
        (if (contains? style :fill) (fill (mk-color (get style :fill))))
        (if (contains? style :stroke-weight) (stroke-weight (get style :stroke-weight)))
        (if (contains? style :bezier)
          (let [ls (sshapes/flat-point-list tsshape) ]             
            (apply bezier ls) 
            )
          
          (do (begin-shape)
            (dorun (map (fn [[x y]] (vertex x y)) (get tsshape :points)))
            (end-shape ))
           )
        (pop-style)
        ))
  )

(defn draw-group 
  [txpt group]
  (dorun (map (partial draw-sshape txpt) group))  )

(defn write-svg "writes svg" [width height group]
  (spit "out.svg" (xml-tpl (make-txpt [-1 -1 1 1] [0 0 width height]) width height group))   )
