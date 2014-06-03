(ns patterning.sshapes
 (:require [patterning.maths :as maths])
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

(defn filter-shape [f? ps] (filter f? ps))



;; SShape (styled shape)
;; SShape, is a shape with a style attached ({:points points :style style}
;; style is a dictionary of style hints eg. color

(defn empty-sshape [] {:style {} :points []})

(defn make [style points] {:style style :points points})


;; Adding styles to sshapes
(defn add-style [new-style {:keys [style points]} ] {:points points :style (conj style new-style)})
(defn color-it "Give new color to a sshape " [c sshape] (add-style {:color c} sshape))
(defn weight-it "Give new strokeWeight to a sshape" [weight sshape] (add-style {:stroke-weight weight} sshape))
(defn fill-it "Give a fill-color to a sshape" [fill sshape] (add-style {:fill fill} sshape))
(defn hide-it "Keep this sshape in the pattern but disable it from rendering" [sshape] (add-style {:hidden true} sshape))
(defn show-it "Remove the hidden label from a sshape, so it's shown" [sshape] (dissoc sshape :hidden))
(defn hidden? "Test if a sshape is hidden" [{:keys [style points]}] (contains? style :hidden) )

(defn bez-curve [style points] (add-style {:bezier true} (make style points )))

(defn scale [val {:keys [style points]}]   {:style style :points (scale-shape val points) } )
(defn translate [dx dy {:keys [style points]}] {:style style :points (translate-shape dx dy points)})
(defn h-reflect [{:keys [style points]}] {:style style :points (h-reflect-shape points)})
(defn v-reflect [{:keys [style points]}] {:style style :points (v-reflect-shape points)})
(defn stretch [sx sy {:keys [style points]}] {:style style :points (stretch-shape sx sy points)})

(defn rotate [da {:keys [style points]}] {:style style :points (rotate-shape da points)} )
(defn wobble [noise {:keys [style points]}] {:style style :points (wobble-shape noise points)} )


(defn reverse-order [{:keys [style points]}] {:style style :points (reverse points)})

(defn flat-point-list [{:keys [style points] :as sshape}] (flatten points))

(defn xs [{:keys [style points]}] (map first points))
(defn ys [{:keys [style points]}] (map second points))

(defn top [sshape] (apply min (ys sshape)))
(defn bottom [sshape] (apply max (ys sshape)))
(defn ss-left [sshape] (apply min (xs sshape)))
(defn ss-right [sshape] (apply max (xs sshape)))

(defn width [sshape] (- (ss-right sshape) (ss-left sshape)))
(defn height [sshape] (- (bottom sshape) (top sshape)))

(defn ss-filter [p? {:keys [style, points]}] {:style style :points (filter-shape p? points)})


;;; Some actual sshapes


(defn poly "polygon sshape"
  ([cx cy radius no-sides style]
     (let [ make-point (fn [a] (maths/add-points [cx cy] (maths/pol-to-rec [radius a])))]
       {:style style :points (close-shape (into [] (map make-point (maths/clock-angles no-sides))))}
       ))
  ([cx cy radius no-sides] (poly cx cy radius no-sides {}))
  )

(defn horizontal-line "horizontal line" [y] (make {} [[(- 1) y] [1 y] [(- 1 ) y] [1 y]]) )
(defn vertical-line "vertical line" [x] (make {} [[x (- 1)] [x 1] [x (- 1)] [x 1]]))

(defn rand-angle [seed]  (lazy-seq (cons seed (rand-angle (+ seed (- (rand (/ maths/PI 2))) (/ maths/PI 4) )))))

(defn drunk-line "drunkard's walk line" [steps stepsize]
  (let [ offs (map (fn [a] [stepsize a]) (take steps (rand-angle 0))) ]
    (make {}
      (loop [pps offs current [0 0] acc []]
        (if (empty? pps) acc
            (let [p (maths/add-points current (maths/pol-to-rec (first pps))) ]
              (recur (rest pps) p (conj acc p))  ))
      ) ) 
    ) )



(defn rect [x y w h] (let [x2 (+ x w) y2 (+ y h)]  (make {} [[x y] [x2 y] [x2 y2] [x y2] [x y]]) ))

(defn random-rect [style]  (let [ rr (fn [l] (rand l ))
                                  m1 (fn [x] (- x 1))]
                             (add-style style (rect (m1 (rr 1)) (m1 (rr 1)) (rr 1) (rr 1)  ) )))

(defn square [] {:style {} :points [[-1 -1] [-1 1] [1 1] [1 -1] [-1 -1]] } )

(defn h-sin [style] (make style (into [] (map (fn [a] [a (maths/sin (* maths/PI a))]  ) (range (- 1) 1 0.05)) )))

(defn h-glue-shape "repeats a shape horizontally" [shape1 shape2]
  (let [e1 (last shape1)
        add (fn [[x y]] [(+ 1 (first e1) x) y])]
    (concat shape1 (into [] (map add shape2)))))

(defn zig-zag [freq style]
  (let [tri (into [] (map (fn [x] [x (maths/abs x)]) (range (- 1) 1 0.1) ))
        down (translate (- (- 1 (float (/ 1 freq))) ) (- 0.25) (stretch (float (/ 1 freq)) 0.5 tri))
        all (reduce h-glue-shape (repeat freq down))
        ]
    (make style all)))


(defn diamond [style] (make style (close-shape [[-1 0] [0 -1] [1 0] [0 1]])) )


(defn ogee [resolution stretch style ]
  (let [ogee (fn [x] (/ x (maths/sqrt (+ 1 (* x x)))))
        points (into [] (map (fn [x] [x (ogee (* stretch x))]) (range (- 1) 1.0001 resolution) ) )]
    (make style (rotate-shape (/ maths/PI 2) points))   ))
