(ns patterning.examples.basics
  (:require [quil.core :refer :all])
  (:require [patterning.geometry :refer :all])
  (:require [patterning.layouts :refer :all])
  (:require [patterning.complex_elements :refer :all])
  (:require [patterning.view :refer :all])
  (:require [patterning.color :refer :all])
  (:require [patterning.examples.design_language1 :refer :all])
  )

;; Some examples to get you started with Patterning

;; To run these examples, make sure basics.clj is included in core.clj
;;   (:require [patterning.examples.basics :refer :all])
;; Then call one of the functions below


;; 5 triangles in a ring
;; ---------------------
(def red-style {:color (p-color 255 100 100) :stroke-weight 2 })

;; poly creates a polygon, the arguments are x-centre, y-centre, radius,
;;number-of-sides, style

(def triangles (clock-rotate 5 (group ( poly 0.5 0.5 0.3 3 red-style) )) )

;; Stack 5 triangles on a blue pentagon
;; ------------------------------------

(def blue-style {:color (p-color 200 200 255) :fill (p-color 150 150 255) :stroke-weight 3})
(def pentagon (group (poly 0 0 0.7 5 blue-style)))
(def pen-tri (stack pentagon triangles))
