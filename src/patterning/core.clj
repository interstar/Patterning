(ns patterning.core
  (:require [patterning.sshapes :refer []])
  (:require [patterning.groups :as groups])
  (:require [patterning.layouts :refer [framed]])
  (:require [patterning.complex_elements :refer []])
  (:require [patterning.view :refer [make-txpt ]])
  (:require [patterning.color :refer [p-color]]) 

  (:require [patterning.examples.basics :as basics])
  (:require [patterning.examples.framedplant :as framedplant])
  (:require [patterning.examples.symbols :as symbols])

  (:require [quil.core :refer :all])
  (:require [patterning.quil.quilview :refer :all])
  (:gen-class))


(defn setup []
  (no-loop)

  (let [        
        ;; +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        ;; THIS IS THE CURRENT PATTERN 
        ;; assign to "final-pattern" the result of creating a pattern,
        ;; 
        ;; Here's an example,
        
        ;;final-pattern (framed 8 (repeat basics/m2) (repeat basics/triangles)                      (groups/scale 0.8 (symbols/ringed-flower-of-life 70 {:color (p-color 200 150 255)  :stroke-weight 3})))

        final-pattern ( symbols/god-pattern)
        ;;pent (group (sshapes/poly 0.2 -0.7 0.2 5 {:color (p-color 255 255 100)} ))
        ;;final-pattern (grid-layout 3 (repeat (clock-rotate 3 pent)))
        ;;final-pattern framedplant/framed-plant
        ;; +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        ]

        ;; Set up sketch, these correspond to standard Processing calls
        (stroke-weight 1)
        (color 0)
        (no-fill)
        (background 255)

        ;; THIS IS WHERE WE ACTUALLY DRAW THE PATTERN ON THE SCREEN
        ;; note we call the function make-txpt which creates a mapping
        ;; function from our co-ordinate system to the actual window
        ;; draw-group uses this to transform all points in our pattern
        (draw-group (make-txpt [-1 -1 1 1] [0 0 (width) (height)]) final-pattern) 
        ;;(display-filter 11)
        
        ;; AND THIS IS WHERE WE WRITE IT TO out.svg
        (write-svg 800 800 final-pattern)
        ) )  

;; This is not an interactive sketch so we don't do anything else in
;; the (draw) function
(defn draw [])


(defn -main [& args]
  (sketch
   :title "Patterning"
   :setup setup
   :draw draw
   :size [700 700]
   :on-close #(System/exit 0)))
