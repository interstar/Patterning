(ns patterning.core
  (:require [quil.core :refer :all])
  (:require [patterning.geometry :refer :all])
  (:require [patterning.layouts :refer :all])
  (:require [patterning.complex_elements :refer :all])
  (:require [patterning.view :refer :all])
  (:require [patterning.color :refer :all])

  (:require [patterning.examples.framedplant :refer :all])
  (:gen-class))


(defn setup []
  (no-loop)

  (let [        

        ;; +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        ;; THIS IS THE CURRENT PATTERN WHICH IS PRODUCED
        ;; (Note this is being :required above)
        
        final-pattern (framed-plant)

        ;; +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


        ;; THIS MAKES THE FUNCTION THAT MAPS FROM THE VIEWPORT (ALL
        ;; PATTERNS ARE DEFINED IN CO-ORDINATES [-1 -1] TO [1 1])
        ;; TO THE SCREEN. DON'T LOSE THIS        
        txpt (make-txpt [-1 -1 1 1] [0 0 (width) (height)])
        ]

        ;; THIS IS THE BIT THAT
        (stroke-weight 1)
        (color 0)
        (no-fill)
        (background 255)
        ;; THIS IS WHERE WE ACTUALLY DRAW THE PATTERN ON THE SCREEN
        (draw-group txpt final-pattern) 
        (smooth)
        (smooth)
        ;; AND THIS IS WHERE WE WRITE IT TO out.svg
        (write-svg txpt 800 800 final-pattern)
        ) )  

(defn draw [])


(defn -main [& args]
  (sketch
   :title "Patterning"
   :setup setup
   :draw draw
   :size [700 700]
   :on-close #(System/exit 0)))
