(ns patterning.core
  (:require [patterning.maths :as maths])
  (:require [patterning.sshapes :refer [poly]])
  
  (:require [patterning.groups :as groups])
  (:require [patterning.layouts :refer [framed clock-rotate stack grid-layout diamond-layout
                                        four-mirror four-round nested-stack]])
  (:require [patterning.complex_elements :refer [basic-turtle]])
  (:require [patterning.view :refer [make-txpt ]])
  (:require [patterning.color :refer [p-color]]) 

  (:require [patterning.examples.basics :as basics])
  (:require [patterning.examples.framedplant :as framedplant])
  (:require [patterning.examples.design_language1 :as design-language])
  (:require [patterning.examples.symbols :as symbols])

  (:require [quil.core :refer :all])
  (:require [patterning.quil.quilview :refer :all])

  (require  [taoensso.timbre.profiling :as profiling
             :refer (pspy pspy* profile defnp p p*)])
  
  (:gen-class))


(defn f-left [count]
  (cond (= count 1) "F"
        :else (str (f-left (- count 1)) "+" (apply str (repeat count "F" )) )) )

(defn f-right [count]
  (cond (= count 1 ) "F"
        :else (str (apply str (repeat count "F" )) "-" (f-right (- count 1)))))

(defn all [count] (str (f-left count) "-" (f-right (- count 1)) ) )

(defn scroll [[x y] d da number weight]
  (basic-turtle [x y] d 0
      da (all number)
      {\Z (fn [x y a] (groups/group
           (poly x (- y 0.08) 0.03 16 {:fill (p-color 100 100 150)})))
       \Y (fn [x y a] (groups/group
           (poly x (+ y 0.08) 0.03 16 {:fill (p-color 230 100 150)})))
      }
      {:color (p-color 150 210 120) :stroke-weight weight}))

(def r-scroll (groups/reframe (scroll [0 0] 0.01 (/ maths/PI 10) 16 2) ))

(def vase (stack r-scroll (groups/v-reflect r-scroll) ))


(defn middle [] (stack ;; design-language/less-complex-diamond
                 (groups/over-style {:color (p-color 255 255 200)}
                                    (groups/scale 0.8 (groups/reframe (framedplant/sys-g1))))) )

(defn framed-FASS [] (framed 7
                             (repeat (framedplant/square2))
                             (repeat vase)
                             (middle)  ))
(defn setup []
  (no-loop)

  (let [        
        ;; +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        ;; THIS IS THE CURRENT PATTERN 
        ;; assign to "final-pattern" the result of creating a pattern,
        ;; 
        ;; Here's an example,

     
        final-pattern (groups/scale 0.99 (framed-FASS))
        

        ;;final-pattern ( symbols/god-pattern)
        ;;pent (group (sshapes/poly 0.2 -0.7 0.2 5 {:color (p-color 255 255 100)} ))
        ;;final-pattern (grid-layout 3 (repeat (clock-rotate 3 pent)))
        ;;final-pattern framedplant/framed-plant
        ;; +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        ]

        ;; Set up sketch, these correspond to standard Processing calls
        (stroke-weight 1)
        (color 255)
        (no-fill)
        (background  50 80 100)

        ;; THIS IS WHERE WE ACTUALLY DRAW THE PATTERN ON THE SCREEN
        ;; note we call the function make-txpt which creates a mapping
        ;; function from our co-ordinate system to the actual window
        ;; draw-group uses this to transform all points in our pattern
        (draw-group (make-txpt [-1 -1 1 1] [0 0 (width) (height)]) final-pattern) 
        ;;(display-filter 11)
        
        ;; AND THIS IS WHERE WE WRITE IT TO out.svg
        (write-svg 800 800 final-pattern)

        ;; Profiling
        ;(profile :info :Arithmetic (dotimes [n 100] (make-final-pattern)))        
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
