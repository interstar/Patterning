(ns patterning.examples.basics
  (:require [patterning.sshapes :refer [->SShape poly bez-curve add-style drunk-line close-shape]] 
            [patterning.groups :refer [group scale ]]
            [patterning.layouts :refer [stack clock-rotate grid-layout checked-layout four-mirror framed
                                        half-drop-grid-layout]]
            [patterning.complex_elements :as complex_elements]

            [patterning.color :refer [p-color]]
            [patterning.examples.design_language1 :refer []])  )

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

(def blue-style {:color (p-color 200 200 255)  :stroke-weight 3})
(def pentagon (group (poly 0 0 0.7 5 blue-style)))
(def pen-tri (stack pentagon triangles))

;; Let's make a grid of these
;; Note grid takes a LIST of the patterns we're going to layout
;; Here we just use (repeat pen-tri) to make an infinite lazy list o them.
(def pt-grid (grid-layout 8 (repeat pen-tri)) )

;; Not massively exciting, instead let's do a checkered pattern
(def pt-checks (checked-layout 8 (repeat pentagon) (repeat triangles)))

;; OK. change of direction, a "drunkards walk" is a series of points
;; each of which is a random move from the previous end point
;; We have a function for that, 10 steps, each of length 0.1
;; (drunk-line 10 0.1)

;; BUT drunk-lines come unstyled, so we need to add a style to it
(def dline (group  (add-style {:color (p-color 100 255 100) :stroke-weight 3} (drunk-line 10 0.1)))  )

;; Why do we want a random wiggle? Well, they look a lot cooler when
;; we do some more things to them
;; Like clock-rotate them
(def dl-clock (clock-rotate 12 dline))
;; Or mirror them
(def dl-mirror (four-mirror dline))
;; Or both
(def clock-mirror (clock-rotate 5 (four-mirror dline)))

;; And did you want that mixed with our other shapes?
(def m2 (clock-rotate 5 (four-mirror (stack dline (scale 0.4  triangles) ))))

;; And perhaps you wanted that on a staggered grid?
;; (don't forget the repeat
(def m3 (half-drop-grid-layout 3 (repeat m2)))

;; Maybe bring back a bit of blue, every other
(def m4 (half-drop-grid-layout 6 (cycle [m2 pentagon])))

;; Don't forget we can keep stacking this stuff up
(def m5 (four-mirror m4))

;; By the way, this is getting way too convoluted to see on the screen
;; properly. But have a look at the vector output (out.svg) in
;; Inkscape or some other vector drawing package

;; OK. So that's line for you, but what about something smoother?
;; Bezier curves? We got 'em

(def orange-style  {:color (p-color 255 128 64) :stroke-weight 4})
(def bez1 (group (bez-curve orange-style [[-0.9 0] [0.8 0.8] [-0.5 -0.8] [0.6 -0.5]])))

;; Perhaps that would makethe corners of a nice frame
(def frame-it (framed 6 (repeat bez1)
                      (repeat
                       (stack (group (->SShape orange-style [[-0.5 -1] [-0.5 1]]))
                              (group (poly -0.8 0 0.1 12 {:fill (p-color 100 100 255) :color (p-color 255 255 200)}))) )
                      dl-clock ))

;; framed takes three arguments, a list of corner pieces (which it
;; reflects appropriately), a list of edge pieces (which it rotates
;; appropriately) and a single centre (NOT a list, just a group)

;; A half square triangle

(defn wedge [style] (group (->SShape style (close-shape  [[-1 -1] [-1 1] [1 -1]]))) )
