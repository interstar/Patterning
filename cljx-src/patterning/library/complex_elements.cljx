(ns patterning.complex_elements
  (:require [patterning.maths :as maths] 
            [patterning.sshapes :as sshapes]
            [patterning.groups :as groups]
            [patterning.layouts :as layouts]
            [patterning.library.turtle :as turtle]
            [patterning.color :as color]))


;; Complex patterns made as groups (these have several disjoint sshapes)

(defn cross-group "A cross, can only be made as a group (because sshapes are continuous lines) which is why we only define it now"
  [color x y] (groups/group (sshapes/set-color color (sshapes/horizontal-line y)) (sshapes/set-color color (sshapes/vertical-line  x)))  )


(defn ogee-group "An ogee shape" [resolution stretch style]
  (let [o-group (into [] (layouts/four-mirror (groups/group ( sshapes/ogee resolution stretch style))))        
        o0 (get (get o-group 0) :points)
        o1 (get (get o-group 1) :points)
        o2 (get (get o-group 2) :points)
        o3 (get (get o-group 3) :points)
        top (sshapes/tie-together o0 o1)
        bottom (sshapes/tie-together o2 o3) ]
    (groups/group (sshapes/->SShape style ( sshapes/tie-together top bottom))) )  )

(defn spoke-flake-group "The thing from my 'Bouncing' Processing sketch"
  [style]
  (let [outer-radius 0.05
        inner-radius (* 2.01 outer-radius)
        arm-radius (* 4.2 inner-radius)

        inner-circle (sshapes/add-style style (sshapes/poly 0 0 inner-radius 30))
        sp1 [0 inner-radius]
        sp2 [0 (+ inner-radius arm-radius)]
      
        one-spoke (groups/group (sshapes/->SShape style [sp1 sp2 sp1 sp2])
                         (sshapes/add-style style (sshapes/poly 0 (+ outer-radius (last sp2)) outer-radius 25)))
      ]
    (into [] (concat (groups/group (sshapes/add-style style (sshapes/poly 0 0 inner-radius 35)))
                     (layouts/clock-rotate 8 one-spoke)       ) ) ) )


(defn polyflower-group "number of polygons rotated and superimosed"
  ( [sides-per-poly no-polies radius style]
      (layouts/clock-rotate no-polies (groups/group (sshapes/add-style style (sshapes/poly 0 0 radius sides-per-poly)))))
  
  ( [sides-per-poly no-polies radius] (polyflower-group sides-per-poly no-polies radius {})))



(defn face-group "[head, eyes, nose and mouth] each argument is a pair to describe a poly [no-sides color]"
  ( [[ head-sides head-color] [ eye-sides eye-color] [ nose-sides nose-color] [ mouth-sides mouth-color]] 
      (let [left-eye (sshapes/stretch 1.3 1 (sshapes/add-style { :color eye-color } (sshapes/poly -0.3 -0.1 0.1 eye-sides)))
            right-eye (sshapes/h-reflect left-eye)
            ]
                
        (groups/group (sshapes/add-style {:color head-color } (sshapes/poly 0 0 0.8 head-sides))
               (sshapes/stretch 1.3 0.4 (sshapes/add-style {:color mouth-color } (sshapes/poly 0 1.3 0.2 mouth-sides)))
               (sshapes/translate 0 0.1
                                  (sshapes/stretch
                                   0.6 1.1
                                   (sshapes/rotate
                                    (/ maths/PI 2) (sshapes/add-style {:color nose-color } (sshapes/poly 0 0 0.2 nose-sides)))))
               left-eye
               right-eye) ) ) )


(defn petal-group "Using bezier curves" [style dx dy]
  (let [ep [0 0]] [ (sshapes/bez-curve style [ ep [(- dx) (- dy)] [(- (*  -2 dx) dx) (- dy)] ep])]  ))

(defn petal-pair-group "reflected petals" [style dx dy]
  (let [petal (petal-group style dx dy)] (layouts/stack petal (groups/h-reflect petal))))

;; Made with Turtle

(defn zig-zag [[x y]] (basic-turtle [x y] 0.1 (/ maths/PI 2)
                                      (/ maths/PI 3) "++F-FF+FF-FF+FF-FF+FF-FF+FF-FF+F" {}
                                      {:color (p-color 150 210 200) :stroke-weight 4}))

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



