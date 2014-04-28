(ns patterning.geometry
 (:require [quil.core :refer :all])
  )

;; Point geometry

(defn rec-to-pol [[x y]] [(sqrt (+ (* x x) (* y y))) (atan2 x y)])
(defn pol-to-rec [[r a]] [(* r (cos a)) (* r (sin a))])
(defn add-points [[x1 y1] [x2 y2]] [(+ x1 x2) (+ y1 y2)])

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



;; Groups
(defn group "a vector of sshapes"
  [& sshapes]
  (into [] sshapes) ) 

(defn empty-group [] (empty-sshape))

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

(defn cross-group "A cross, can only be made as a group (because sshapes are continuous lines) which is why we only define it now"
  [colour x y] (group (colour-sshape colour (horizontal-sshape y)) (colour-sshape colour (vertical-sshape  x)))  )

;; Layouts
;; Note layouts combine and multiply groups to make larger groups

(defn superimpose-layout "simplest layout, two groups located on top of each other "
  [group1 group2] (into [] (concat group1 group2))   )


(defn cart "Cartesian Product of two collections" [colls]
  (if (empty? colls)
    '(())
    (for [x (first colls) more (cart (rest colls))]
      (cons x more))))

(defn grid-layout-positions "calculates the positions for a grid layout"
  [number]
  (let [
      offset (/ 2 number)
      inc (fn [x] (+ offset x))
      ino (float (/ offset 2))
      init (- ino 1)
      h-iterator (take number (iterate inc init))
      v-iterator (take number (iterate inc init))
      ]
  (cart [h-iterator v-iterator])  ) )


(defn apply-positions "Takes a group and a list of positions and puts a copy of the group at each position"
  [group positions] (concat ( mapcat (fn [[x y]] (translate-group x y group)) positions) )) 



(defn place-groups-at-positions "Takes a list of groups and a list of positions and puts one of the groups at each position"
  [groups positions]
  (concat ( mapcat (fn [[ [x y] group]] (translate-group x y group)) (map vector positions groups) ) )) 

(defn grid-layout "Takes an n and a group-stream and returns items from the group-stream in an n X n grid "
  [n groups]
  (let [scaler (/ 1 n)
        scaled-groups (into [] (map (partial scale-group scaler) groups))
        ]    
    (place-groups-at-positions scaled-groups (grid-layout-positions n)) )
  )


(defn q1-rot-group [group] (rotate-group (float (/ PI 2)) group ) )
(defn q2-rot-group [group] (rotate-group PI group))
(defn q3-rot-group [group] (rotate-group (-  (float (/ PI 2))) group))


(defn random-grid-layout "Takes a group and returns a grid with random quarter rotations"
  [number groups]
  (let [scaler (/ 1 number)
        random-turn (fn [group]
                      (case (rand-int 4)
                        0 group
                        1 (q1-rot-group group)
                        2 (q2-rot-group group)
                        3 (q3-rot-group group)  ) )
        scaled-groups (into [] (map random-turn (map (partial scale-group scaler ) groups)))
        ]
    (place-groups-at-positions scaled-groups (grid-layout-positions number)) )
  )

(defn random-turn-groups [groups]
  (let [random-turn (fn [group]
                      (case (rand-int 4)
                        0 group
                        1 (q1-rot-group group)
                        2 (q2-rot-group group)
                        3 (q3-rot-group group)  ) ) ]
    (map random-turn groups) ))

(defn drop-every [n xs] (lazy-seq (if (seq xs) (concat (take (dec n) xs) (drop-every n (drop n xs))))))
