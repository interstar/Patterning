(ns patterning.core
  (:require [patterning.maths :as maths])
  (:require [patterning.sshapes :refer [poly ->SShape ]])
  (:require [patterning.sshapes :as sshapes])
  
  (:require [patterning.groups :as groups])
  (:require [patterning.layouts :refer [framed clock-rotate stack grid-layout diamond-layout
                                        four-mirror four-round nested-stack checked-layout
                                        half-drop-grid-layout random-turn-groups h-mirror]])
  (:require [patterning.library.turtle :refer [basic-turtle]])
  (:require [patterning.library.complex_elements :refer [vase zig-zag]])
  (:require [patterning.view :refer [make-txpt ]])
  (:require [patterning.color :refer [p-color]]) 

  (:require [patterning.examples.tutorial :as tutorial])
  (:require [patterning.examples.framedplant :as framedplant])
  (:require [patterning.examples.design_language1 :as design-language])
  (:require [patterning.library.symbols :as symbols])

  (:require [patterning.examples.interactive :as interactive])
  
  (:require [quil.core :refer :all])
  (:require [patterning.quil.quilview :refer :all]) )


;; +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
;; THIS IS THE CURRENT PATTERN 
;; assign to "final-pattern" the result of creating a pattern,

(def main (clock-rotate
           4 (groups/group
              ( sshapes/drunk-line 10 0.1 {:color (p-color 200 200 100) :stroke-weight 4}))))

(def final-pattern main)


(def my-width 700)
(def my-height 700)

(defn setup []
  ;; Set up sketch, these correspond to standard Processing calls
  (no-loop)
  (stroke-weight 1)
  (color 0)
  (no-fill)
  (background 0)

  ;; THIS IS WHERE WE ACTUALLY DRAW THE PATTERN ON THE SCREEN
  ;; note we call the function make-txpt which creates a mapping
  ;; function from our co-ordinate system to the actual window
  ;; draw-group uses this to transform all points in our pattern
  (draw-group (make-txpt [-1 -1 1 1] [0 0 my-width my-height]) final-pattern) 
  ;;(display-filter 11)
  
  ;; AND THIS IS WHERE WE WRITE IT TO out.svg
  (write-svg 800 800 final-pattern)

  )  

;; This is not an interactive sketch so we don't do anything else in
;; the (draw) function
(defn draw []  )


(defn -main [& args]
  (sketch
   :title "Patterning"
   :setup setup
   :draw draw
   :size [my-width my-height]
   :on-close #(System/exit 0)))
