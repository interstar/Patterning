(ns patterning.complex_elements
  (:require [quil.core :refer :all])
  (:require [patterning.geometry :refer :all])
  (:require [patterning.layouts :refer :all])
  )



;; Complex patterns made as groups (these have several disjoint sshapes)

(defn cross-group "A cross, can only be made as a group (because sshapes are continuous lines) which is why we only define it now"
  [colour x y] (group (colour-sshape colour (horizontal-sshape y)) (colour-sshape colour (vertical-sshape  x)))  )



(defn ogee-group "An ogee shape" [resolution stretch style]
  (let [o-group (into [] (four-mirror (group ( ogee-sshape resolution stretch style))))
        o0 (get o-group 0)
        o1 (reverse-order-sshape  (get o-group 1))
        o2 (get o-group 2)
        o3 (get o-group 3)
        ref-group [o0 o1 o3 o2]]
    (group (flatten-group style ref-group)) )  )

(defn spoke-flake-group "The thing from my 'Bouncing' Processing sketch"
  [style]
  (let [outer-radius 0.05
        inner-radius (* 2.01 outer-radius)
        arm-radius (* 4.2 inner-radius)

        inner-circle (add-style style (poly 0 0 inner-radius 30))
        sp1 [0 inner-radius]
        sp2 [0 (+ inner-radius arm-radius)]
      
        one-spoke (group (sshape style [sp1 sp2 sp1 sp2])
                         (add-style style (poly 0 (+ outer-radius (last sp2)) outer-radius 25)))
      ]
    (into [] (concat (group (add-style style (poly 0 0 inner-radius 35)))
                     (clock-rotate 8 one-spoke)       ) ) ) )


(defn polyflower-group "number of polygons rotated and superimosed"
  ( [sides-per-poly no-polies radius style]
      (clock-rotate no-polies (group (add-style style (poly 0 0 radius sides-per-poly)))))
  
  ( [sides-per-poly no-polies radius] (polyflower-group sides-per-poly no-polies radius {})))



(defn face-group "[head, eyes, nose and mouth] each argument is a pair to describe a poly [no-sides colour]"
  ( [[ head-sides head-colour] [ eye-sides eye-colour] [ nose-sides nose-colour] [ mouth-sides mouth-colour]] 
      (let [left-eye (stretch-sshape 1.3 1 (add-style { :colour eye-colour } (poly -0.3 -0.1 0.1 eye-sides)))
            right-eye (h-reflect-sshape left-eye)
            ]
                
        (group (add-style {:colour head-colour } (poly 0 0 0.8 head-sides))
               (stretch-sshape 1.3 0.4 (add-style {:colour mouth-colour } (poly 0 1.3 0.2 mouth-sides)))
               (translate-sshape 0 0.1
                                 (stretch-sshape 0.6 1.1 (rotate-sshape (/ PI 2)
                                 (add-style {:colour nose-colour } (poly 0 0 0.2 nose-sides)))))
               left-eye
               right-eye) ) ) )
