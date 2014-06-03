(ns patterning.examples.symbols
  (:require [patterning.maths :as maths])
  (:require [patterning.sshapes :as sshapes])
  (:require [patterning.groups :refer :all])
  (:require [patterning.layouts :refer :all])
  (:require [patterning.complex_elements :refer :all])
  (:require [patterning.view :refer :all])
  (:require [patterning.color :refer :all])
  (:require [patterning.examples.design_language1 :refer :all])
  )

(defn khatim [style]
  (clock-rotate 8 (group (sshapes/poly 0 -0.45 0.25 4 style)) ))

(defn seed-of-life [style]
  (let [pos (flower-of-life-positions 0.4 1 [0 0] )]
    (place-groups-at-positions (repeat (group (sshapes/poly 0 0 0.4 80 style))) pos  )  ))

(defn flower-of-life
  ([sides style]
      (let [r 0.4
            r3 (* r 3)
            pos (flower-of-life-positions r 3 [0 0])]
        (clip-group (fn [[x y]] (> (* r3 r3) (+ (* x x) (* y y))))
                    (place-groups-at-positions (repeat (group (sshapes/poly 0 0 r sides style))) pos ))))
  ([style] (flower-of-life 80 style)))

(defn ringed-flower-of-life
  ([sides style] (stack (flower-of-life sides style) (group (sshapes/poly 0 0 1.2 sides style))))
  ([style] (ringed-flower-of-life 80 style ) ))

(defn god-pattern []
  (let [s {:color (p-color 0 0 0) :stroke-weight 2}
        for-x (fn [x a d] (+ x (* d (maths/cos a))) )
        for-y (fn [y a d] (+ y (* d (maths/sin a))) )
        sq (* 0.1 (Math/sqrt 5))
        diag (fn [x y a] (group (sshapes/make s [[x y] [(for-x x a sq) (for-y y a sq)] ] )))
        part1 (basic-turtle [0 0] 0.1 0 (/ maths/PI 4)
                           "F++F++F++F++FFF++F++F++FF++FF++FFF+FFF+++FF"
                           {"M" diag} s)
        ]
    (stack
     (clock-rotate 4 part1)
     (rotate-group (/ maths/PI 8) (group (sshapes/poly 0 0 0.55 8 s))))  ))
