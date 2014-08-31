(ns patterning.library.std
  (:require [patterning.maths :as maths]
            [patterning.sshapes :refer [rotate-shape close-shape ->SShape set-color tie-together ]]
            [patterning.groups :refer [group]]
            [patterning.layouts :refer [stack four-mirror]])
  (#+clj :require #+cljs :require-macros 
         [patterning.macros :refer [optional-styled-primitive]])
  )


;;; Some basic sshapes

(def rect (optional-styled-primitive [x y w h]
                                     (let [x2 (+ x w) y2 (+ y h)]
                                       [[x y] [x2 y] [x2 y2] [x y2] [x y]] ) ))

(def square (optional-styled-primitive []
                                       ([[-1 -1] [-1 1] [1 1] [1 -1] [-1 -1]] ) ))

(def poly (optional-styled-primitive [cx cy radius no-sides]
             (let [ make-point (fn [a] (maths/add-points [cx cy] (maths/pol-to-rec [radius a])))]
                (close-shape (into [] (map make-point (maths/clock-angles no-sides))))  )
           ))

(defn random-rect [style]
  (let [ rr (fn [l] (rand l))
        m1 (fn [x] (- x 1))]
    (rect (m1 (rr 1)) (m1 (rr 1)) (rr 1) (rr 1) style ) ))

(def horizontal-line (optional-styled-primitive [y] [[-1 y] [1 y] [-1 y] [1 y]] ))
(def vertical-line (optional-styled-primitive [x] [[x -1] [x 1] [x -1] [x 1]]))


(defn rand-angle [seed]  (lazy-seq (cons seed (rand-angle (+ seed (- (rand (/ maths/PI 2))) (/ maths/PI 4) )))))

(def drunk-line
  (optional-styled-primitive [steps stepsize]
                             (let [ offs (map (fn [a] [stepsize a]) (take steps (rand-angle 0))) ]
                               (loop [pps offs current [0 0] acc []]
                                 (if (empty? pps) acc
                                     (let [p (maths/add-points current (maths/pol-to-rec (first pps))) ]
                                       (recur (rest pps) p (conj acc p))  )) )  )  ))




(def h-sin (optional-styled-primitive [] (into [] (map (fn [a] [a (maths/sin (* maths/PI a))]  ) (range (- 1) 1 0.05)) ) ))

(def diamond (optional-styled-primitive [] (close-shape [[-1 0] [0 -1] [1 0] [0 1]] )))

(def quarter-ogee (optional-styled-primitive [resolution stretch]
                                     (let [ogee (fn [x] (/ x (maths/sqrt (+ 1 (* x x)))))
                                           points (into []
                                                        (map (fn [x] [x (ogee (* stretch x))])
                                                             (range (- 1) 1.0001 resolution) ) )]
                                       (rotate-shape (/ maths/PI 2) points)   ) ))



;; Complex patterns made as groups (these have several disjoint sshapes)

(defn cross "A cross, can only be made as a group (because sshapes are continuous lines) which is why we only define it now"
  [color x y] (group (set-color color (horizontal-line y)) (set-color color (vertical-line  x)))  )


(defn ogee "An ogee shape" [resolution stretch style]
  (let [o-group (into [] (four-mirror (group (quarter-ogee resolution stretch style))))        
        o0 (get (get o-group 0) :points)
        o1 (get (get o-group 1) :points)
        o2 (get (get o-group 2) :points)
        o3 (get (get o-group 3) :points)
        top (tie-together o0 o1)
        bottom (tie-together o2 o3) ]
    (group (->SShape style ( tie-together top bottom))) )  )





;; Others
(defn background [color pattern]
  (stack (group (square {:fill color}))
         pattern))
