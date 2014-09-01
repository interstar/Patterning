(ns patterning.library.complex_elements
  (:require [patterning.maths :as maths] 
            [patterning.sshapes :as sshapes]
            [patterning.sshapes :refer [->SShape tie-together]]
            [patterning.groups :as groups]
            [patterning.layouts :as layouts]
            [patterning.library.std :as std]
            [patterning.library.turtle :as turtle]
            [patterning.color :refer [p-color]]))



(defn spoke-flake-group "The thing from my 'Bouncing' Processing sketch"
  [style]
  (let [outer-radius 0.05
        inner-radius (* 2.01 outer-radius)
        arm-radius (* 4.2 inner-radius)

        inner-circle (std/poly 0 0 inner-radius 30 style)
        sp1 [0 inner-radius]
        sp2 [0 (+ inner-radius arm-radius)]
      
        one-spoke (groups/group (->SShape style [sp1 sp2 sp1 sp2])
                                (std/poly 0 (+ outer-radius (last sp2)) outer-radius 25 style )  )
      ]
    (into [] (concat (std/poly 0 0 inner-radius 35 style)
                     (layouts/clock-rotate 8 one-spoke)       ) ) ) )


(defn polyflower-group "number of polygons rotated and superimosed"
  ( [sides-per-poly no-polies radius style]
      (layouts/clock-rotate no-polies (std/poly 0 0 radius sides-per-poly style)))
  
  ( [sides-per-poly no-polies radius] (polyflower-group sides-per-poly no-polies radius {})))



(defn face-group "[head, eyes, nose and mouth] each argument is a pair to describe a poly [no-sides color]"
  ( [[ head-sides head-color] [ eye-sides eye-color] [ nose-sides nose-color] [ mouth-sides mouth-color]] 
      (let [left-eye (groups/stretch 1.3 1 (std/poly -0.3 -0.1 0.1 eye-sides { :stroke eye-color }))
            right-eye (groups/h-reflect left-eye)
            ]
                
        (std/poly 0 0 0.8 head-sides {:stroke head-color } )
        (groups/stretch 1.3 0.4 (std/poly 0 1.3 0.2 mouth-sides {:stroke mouth-color }))
        (groups/translate 0 0.1
                           (groups/stretch
                            0.6 1.1
                            (groups/rotate
                             (/ maths/PI 2) (std/poly 0 0 0.2 nose-sides {:stroke nose-color}))))
        left-eye
        right-eye ) ) )


(defn petal-group "Using bezier curves" [style dx dy]
  (let [ep [0 0]] [ (sshapes/bez-curve style [ ep [(- dx) (- dy)] [(- (*  -2 dx) dx) (- dy)] ep])]  ))

(defn petal-pair-group "reflected petals" [style dx dy]
  (let [petal (petal-group style dx dy)] (layouts/stack petal (groups/h-reflect petal))))


;; Made with Turtle

(defn zig-zag [[x y]] (turtle/basic-turtle [x y] 0.1 (/ maths/PI 2)
                                      (/ maths/PI 3) "++F-FF+FF-FF+FF-FF+FF-FF+FF-FF+F" {}
                                      {:stroke (p-color 150 210 200) :stroke-weight 4}))

;; Scrolls

(defn f-left [count]
  (cond (= count 1) "F"
        :else (str (f-left (- count 1)) "+" (apply str (repeat count "F" )) )) )

(defn f-right [count]
  (cond (= count 1 ) "F"
        :else (str (apply str (repeat count "F" )) "-" (f-right (- count 1)))))

(defn all [count] (str (f-left count) "-" (f-right (- count 1)) ) )

(defn scroll [[x y] d da number style extras]
  (turtle/basic-turtle [x y] d 0 da (all number) extras style ))

(defn r-scroll [d da number style extras] (groups/reframe (scroll [0 0] d da number  
                                      style extras) ))

(defn vase [d da count style]
  (let [scroll (r-scroll d da count style {})]
    (layouts/stack scroll (groups/v-reflect scroll) )))



