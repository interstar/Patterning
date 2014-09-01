(ns patterning.library.symbols
  (:require [patterning.maths :as maths]
            [patterning.sshapes :refer [->SShape  ]]
            [patterning.groups :refer [group clip rotate scale]]
            [patterning.layouts :refer [clock-rotate stack flower-of-life-positions place-groups-at-positions framed]]
            [patterning.library.std :refer [poly]]
            [patterning.library.turtle :refer [basic-turtle]]
           
            [patterning.color :refer [p-color]]
            )  )

(defn khatim [style]
  (clock-rotate 8  (poly 0 -0.45 0.25 4 style) ))

(defn seed-of-life [style]
  (let [pos (flower-of-life-positions 0.4 1 [0 0] )]
    (place-groups-at-positions (repeat (poly 0 0 0.4 80 style)) pos  )  ))

(defn flower-of-life
  ([sides style]
      (let [r 0.4
            r3 (* r 3)
            pos (flower-of-life-positions r 3 [0 0])]
        (clip (fn [[x y]] (> (* r3 r3) (+ (* x x) (* y y))))
              (place-groups-at-positions (repeat  (poly 0 0 r sides style)) pos ))))
  ([style] (flower-of-life 80 style)))

(defn ringed-flower-of-life
  ([sides style] (stack (flower-of-life sides style)  (poly 0 0 1.2 sides style)))
      ([style] (ringed-flower-of-life 80 style ) ))

(defn god-pattern []
  (let [s {:stroke (p-color 0 0 0) :stroke-weight 2}
        for-x (fn [x a d] (+ x (* d (maths/cos a))) )
        for-y (fn [y a d] (+ y (* d (maths/sin a))) )
        sq (* 0.1 (Math/sqrt 5))
        diag (fn [x y a] (group (->SShape s [[x y] [(for-x x a sq) (for-y y a sq)] ] )))
        part1 (basic-turtle [0 0] 0.1 0 (/ maths/PI 4)
                           "F++F++F++F++FFF++F++F++FF++FF++FFF+FFF+++FF"
                           {"M" diag} s)
        ]
    (stack
     (clock-rotate 4 part1)
     (rotate (/ maths/PI 8)  (poly 0 0 0.55 8 s)))  ))

;; example that can be called from javascript
(defn folexample []
  (scale 0.8 (ringed-flower-of-life 70 {:stroke (p-color 200 150 255)  :stroke-weight 3})))
