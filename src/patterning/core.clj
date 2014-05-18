(ns patterning.core
  (:require [quil.core :refer :all])
  (:require [patterning.geometry :refer :all])
  (:require [patterning.layouts :refer :all])
  (:require [patterning.complex_elements :refer :all])
  (:require [patterning.view :refer :all])
  (:require [patterning.color :refer :all])

  (:require [patterning.examples.basics :refer :all])
  
  (:require [patterning.examples.framedplant :refer :all])  
  (:gen-class))


(defn setup []
  (no-loop)

  (let [        
        ;; +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        ;; THIS IS THE CURRENT PATTERN 
        ;; assign to "final-pattern" the result of creating a pattern,
        ;; 
        ;; Here's an example, 
        final-pattern frame-it
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
        (smooth)
        (smooth)
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
