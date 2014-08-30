(ns patterning.sshapes
  (:require [patterning.maths :as maths])
  (#+clj :require #+cljs :require-macros 
         [patterning.macros :refer [optional-styled-primitive]])
 )


;; Shapes
;; Shape is a list of points

(defn scale-shape [val shape]
  (into [] (map (fn [[x y]] [(* val x) (* val y)]) shape )) )

(defn translate-shape [dx dy shape]
  (into [] (map (fn [[x y]] [(+ dx x) (+ dy y)]) shape ))  )

(defn stretch-shape [sx sy shape]
  (into [] (map (fn [[x y]] [(* sx x) (* sy y)]) shape ))  )

(defn h-reflect-shape [shape]
  (into [] (map (fn [[x y]] [(- x) y]) shape )) )

(defn v-reflect-shape [shape]
  (into [] (map (fn [[x y]] [x (- y)]) shape )) )

(defn close-shape "Closes an open shape" [points] (conj points (first points)))

(defn points-to-polars [points] (into [] (map maths/rec-to-pol points)))
(defn polars-to-points [polars] (into [] (map maths/pol-to-rec polars)))

(defn rotate-shape [da shape] (map (partial maths/rotate-point da) shape))


(defn wobble-shape [noise shape] (map (partial maths/wobble-point noise) shape))

(defn ends "the start and end point of a shape" [shape] [(first shape) (last shape)] )

(defn tie-together "Merge two open shapes together, by choosing the ends that are closest" [shape1 shape2]
  (let [[e1 e2] (ends shape1)
        [e3 e4] (ends shape2)
        o1 (maths/distance e1 e3)
        o2 (maths/distance e1 e4)
        o3 (maths/distance e2 e3)
        o4 (maths/distance e2 e4)
        dists [ o1 o2 o3 o4 ]
        m (apply min dists)]
    (cond 
      (= m o1) (concat (reverse shape1) shape2)
      (= m o2) (concat (reverse shape1) (reverse shape2))
      (= m o3) (concat shape1 shape2)
      (= m o4) (concat shape1 (reverse shape2)))  ) )

(defn h-glue-shape "repeats a shape horizontally" [shape1 shape2]
  (let [e1 (last shape1)
        add (fn [[x y]] [(+ 1 (first e1) x) y])]
    (concat shape1 (into [] (map add shape2)))))

(defn filter-shape [f? ps] (filter f? ps))



;; SShape (styled shape)
;; SShape, is a shape with a style attached ({:points points :style style}
;; style is a dictionary of style hints eg. color

(defrecord SShape [style points])

(defn empty-sshape [] (->SShape {} []))


;; Adding styles to sshapes
(defn add-style [new-style {:keys [style points]} ] {:points points :style (conj style new-style)})
(defn set-color "Give new color to a sshape " [c sshape] (add-style {:color c} sshape))
(defn set-weight "Give new strokeWeight to a sshape" [weight sshape] (add-style {:stroke-weight weight} sshape))
(defn set-fill "Give a fill-color to a sshape" [fill sshape] (add-style {:fill fill} sshape))
(defn hide "Keep this sshape in the pattern but disable it from rendering" [sshape] (add-style {:hidden true} sshape))
(defn unhide "Remove the hidden label from a sshape, so it's shown" [sshape] (dissoc sshape :hidden))
(defn hidden? "Test if a sshape is hidden" [{:keys [style points]}] (contains? style :hidden) )

(defn bez-curve [style points] (add-style {:bezier true} (->SShape style points )))

(defn scale [val sshape] (->SShape (get sshape :style) (scale-shape val (get sshape :points)))  )
(defn translate [dx dy {:keys [style points]}] (->SShape style (translate-shape dx dy points)))
(defn h-reflect [{:keys [style points]}] (->SShape style (h-reflect-shape points)))
(defn v-reflect [{:keys [style points]}] (->SShape style (v-reflect-shape points)))
(defn stretch [sx sy sshape] (->SShape (get sshape :style) (stretch-shape sx sy (get sshape :points))))
(defn rotate [da sshape] (->SShape (get sshape :style) (rotate-shape da (get sshape :points))) )

(defn wobble [noise {:keys [style points]}] (->SShape style (wobble-shape noise points)) )

(defn reverse-order [{:keys [style points]}] (->SShape style (reverse points)))

(defn flat-point-list [{:keys [style points] :as sshape}] (flatten points))

(defn xs [{:keys [style points]}] (map first points))
(defn ys [{:keys [style points]}] (map second points))

(defn top [sshape] (apply min (ys sshape)))
(defn bottom [sshape] (apply max (ys sshape)))
(defn leftmost [sshape] (apply min (xs sshape)))
(defn rightmost [sshape] (apply max (xs sshape)))

(defn width [sshape] (- (rightmost sshape) (leftmost sshape)))
(defn height [sshape] (- (bottom sshape) (top sshape)))

(defn ss-filter [p? {:keys [style, points]}] (->SShape style (filter-shape p? points)))



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

(def ogee (optional-styled-primitive [resolution stretch]
                                     (let [ogee (fn [x] (/ x (maths/sqrt (+ 1 (* x x)))))
                                           points (into []
                                                        (map (fn [x] [x (ogee (* stretch x))])
                                                             (range (- 1) 1.0001 resolution) ) )]
                                       (rotate-shape (/ maths/PI 2) points)   ) ))


