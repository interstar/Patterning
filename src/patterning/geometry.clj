(ns patterning.geometry
 (:require [quil.core :refer :all])
 (:require [clojure.math.numeric-tower :as math])

  )
  

;; Point geometry

(defn f-eq "floating point equality" [a b]  (<= (Math/abs (- a b)) 0.00001))
(defn p-eq "point equality" [[x1 y1] [x2 y2]] (and (f-eq x1 x2) (f-eq y1 y2)))

(defn line-to-segments [points]
  (if (empty? points) [] 
      (loop [p (first points) ps (rest points) acc []]
        (if (empty? ps) acc
            (recur (first ps) (rest ps) (conj acc [p (first ps)]) )  )        
        )))

(defn add-points [[x1 y1] [x2 y2]] [(+ x1 x2) (+ y1 y2)])
(defn diff [[x1 y1] [x2 y2]] (let [dx (- x2 x1) dy (- y2 y1)]  [dx dy]) )
(defn magnitude [[dx dy]] (math/sqrt (+ (* dx dx) (* dy dy) )))
(defn distance [p1 p2] ( (comp magnitude diff) p1 p2) )
(defn unit [[dx dy]] (let [m (magnitude [dx dy])] [(float (/ dx m)) (float (/ dy m))]) )

(defn rev [[dx dy]] [(- dx) (- dy)])

(defn rec-to-pol [[x y]] [(math/sqrt (+ (* x x) (* y y))) (atan2 x y)])
(defn pol-to-rec [[r a]] [(* r (cos a)) (* r (sin a))])


(defn rotate-point [a [x y]]
  (let [cos-a (cos a) sin-a (sin a)]  
    [(- (* x cos-a) (* y sin-a))
     (+ (* x sin-a) (* y cos-a))]))

(defn wobble-point "add some noise to a point, qx and qy are the x and y ranges of noise"
  [[qx qy]  [x y]]
  (let [wob (fn [n qn] (+ n (- (rand qn) (/ qn 2))))]
     [(wob x qx) (wob y qy)]  ) )




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

(defn points-to-polars [points] (into [] (map rec-to-pol points)))
(defn polars-to-points [polars] (into [] (map pol-to-rec polars)))

(defn rotate-shape [da shape] (map (partial rotate-point da) shape))

(defn wobble-shape [noise shape] (map (partial wobble-point noise) shape))


;; SShape (styled shape)
;; SShape, is a shape with a style attached ({:points points :style style}
;; style is a dictionary of style hints eg. colour

(defn empty-sshape [] {:style {} :points []})

(defn sshape [style points] {:style style :points points})

(defn add-style [new-style {:keys [style points]} ] {:points points :style (conj style new-style)})

(defn colour-sshape "Give new colour to a sshape " [colour sshape] (add-style {:colour colour} sshape))
(defn weight-sshape "Give new strokeWeight to a sshape" [weight sshape] (add-style {:stroke-weight weight} sshape))
(defn fill-sshape "Give a fill-colour to a sshape" [fill sshape] (add-style {:fill fill} sshape))
(defn hide-sshape "Keep this sshape in the pattern but disable it from rendering" [sshape] (add-style {:hidden true} sshape))
(defn show-sshape "Remove the hidden label from a sshape, so it's shown" [sshape] (dissoc sshape :hidden))
(defn sshape-hidden? "Test if a sshape is hidden" [{:keys [style points]}] (contains? style :hidden) )

(defn scale-sshape [val {:keys [style points]}]   {:style style :points (scale-shape val points) } )
(defn translate-sshape [dx dy {:keys [style points]}] {:style style :points (translate-shape dx dy points)})
(defn h-reflect-sshape [{:keys [style points]}] {:style style :points (h-reflect-shape points)})
(defn v-reflect-sshape [{:keys [style points]}] {:style style :points (v-reflect-shape points)})
(defn stretch-sshape [sx sy {:keys [style points]}] {:style style :points (stretch-shape sx sy points)})

(defn rotate-sshape [da {:keys [style points]}] {:style style :points (rotate-shape da points)} )
(defn wobble-sshape [noise {:keys [style points]}] {:style style :points (wobble-shape noise points)} )

(defn reverse-order-sshape [{:keys [style points]}] {:style style :points (reverse points)})

;;; Some actual sshapes
(defn angles [number]  (take number (iterate (fn [a] (+ a (float (/ (* 2 PI) number)))) (- PI))) )

(defn poly "polygon sshape"
  ([cx cy radius no-sides style]
     (let [ make-point (fn [a] (add-points [cx cy] (pol-to-rec [radius a])))]
       {:style style :points (close-shape (into [] (map make-point (angles no-sides))))}
       ))
  ([cx cy radius no-sides] (poly cx cy radius no-sides {}))
  )

(defn horizontal-sshape "horizontal line" [y] (sshape {} [[(- 1) y] [1 y] [(- 1 ) y] [1 y]]) )
(defn vertical-sshape "vertical line" [x] (sshape {} [[x (- 1)] [x 1] [x (- 1)] [x 1]]))

(defn rand-angle [seed]  (lazy-seq (cons seed (rand-angle (+ seed (- (rand (/ PI 2))) (/ PI 4) )))))

(defn drunk-line "drunkard's walk line" [steps stepsize]
  (let [ offs (map (fn [a] [stepsize a]) (take steps (rand-angle 0))) ]
    (sshape {}
      (loop [pps offs current [0 0] acc []]
        (if (empty? pps) acc
            (let [p (add-points current (pol-to-rec (first pps))) ]
              (recur (rest pps) p (conj acc p))  ))
      ) ) 
    ) )



(defn rect-sshape [x y w h] (let [x2 (+ x w) y2 (+ y h)]  ( sshape {} [[x y] [x2 y] [x2 y2] [x y2] [x y]]) ))

(defn random-rect [style]  (let [ rr (fn [l] (rand l ))
                                  m1 (fn [x] (- x 1))]
                             (add-style style (rect-sshape (m1 (rr 1)) (m1 (rr 1)) (rr 1) (rr 1)  ) )))


(defn h-sin-sshape [style] (sshape style (into [] (map (fn [a] [a (sin (* PI a))]  ) (range (- 1) 1 0.05)) )))

(defn h-glue-shape "repeats a shape horizontally" [shape1 shape2]
  (let [e1 (last shape1)
        add (fn [[x y]] [(+ 1 (first e1) x) y])]
    (concat shape1 (into [] (map add shape2)))))

(defn zig-zag-sshape [freq style]
  (let [tri (into [] (map (fn [x] [x (abs x)]) (range (- 1) 1 0.1) ))
        down (translate-shape (- (- 1 (float (/ 1 freq))) ) (- 0.25) (stretch-shape (float (/ 1 freq)) 0.5 tri))
        all (reduce h-glue-shape (repeat freq down))
        ]
    (sshape style all)))


(defn diamond-sshape [style] (sshape style (close-shape [[-1 0] [0 -1] [1 0] [0 1]])) )

(defn ogee-sshape [resolution stretch style ]
  (let [ogee (fn [x] (/ x (math/sqrt (+ 1 (* x x)))))
        points (into [] (map (fn [x] [x (ogee (* stretch x))]) (range (- 1) 1.0001 resolution) ) )]
    (sshape style (rotate-shape (/ PI 2) points))   ))

;; Groups
;; A Group is a vector of sshapes. All patterns are basically groups.
;; Groups can represent ordinary patterns that require several sshapes
;; (because they have disjoint geometric forms, or multiple colours
;; etc.
;; Groups are also the flattened results of combining multiple groups
;; together, eg. when running them through a layout. 


;;; Making groups
(defn group "a vector of sshapes" [& sshapes] (into [] sshapes) )
(defn empty-group [] (empty-sshape))

;;; Simple transforms
(defn scale-group ([val group] (into [] (map (partial scale-sshape val) group )))   )

(defn translate-group  [dx dy group] (into [] (map (partial translate-sshape dx dy) group))  )

(defn translate-group-to [x y group] (translate-group (- x) (- y) group) )  

(defn h-reflect-group [group] (into [] (map h-reflect-sshape group) ) )
(defn v-reflect-group [group] (into [] (map v-reflect-sshape group) ) )

(defn stretch-group [sx sy group] (into [] (map (partial stretch-sshape sx sy) group)))
(defn rotate-group [da group] (into [] (map (partial rotate-sshape da) group)))

(defn wobble-group [noise group] (into [] (map (partial wobble-sshape noise) group)))

(defn over-style-group "Changes the style of a group" [style group]
  (into [] (map (partial add-style style) group)))


(defn extract-points [{:keys [style points]}] points)

(defn flatten-group "Flatten all sshapes into a single sshape"
  ([group] (flatten-group {} group))
  ([style group]
     (let [all-points (mapcat extract-points group) ]
       (sshape style all-points) )  ) )



;; Growth transformations

(defn l-norm [p1 p2] (let [[dx dy] (diff [p1 p2])] (unit [(- dy) dx]) ) )
(defn r-norm [p1 p2] (rev (l-norm p1 p2)))

(defn add-dots-to-sshape "Adds dots each side of a line. " [args make-spot dist {:keys [style points]}]  
  (let [segs (line-to-segments points)
        l-dot (fn [[x1 y1] [x2 y2]] (make-spot   ))]
    ()))

