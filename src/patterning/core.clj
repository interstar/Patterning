(ns patterning.core
  (:require [quil.core :refer :all])
  (:gen-class))

;; Point geometry

(defn rec-to-pol [[x y]] [(sqrt (+ (* x x) (* y y))) (atan2 x y)])
(defn pol-to-rec [[r a]] [(* r (cos a)) (* r (sin a))])
(defn add-points [[x1 y1] [x2 y2]] [(+ x1 x2) (+ y1 y2)])

;; Shapes
;; Shape is a list of points

(defn scale-shape [val shape]
  (into [] (map (fn [[x y]] [(* val x) (* val y)]) shape )) )

(defn translate-shape [dx dy shape]
  (into [] (map (fn [[x y]] [(+ dx x) (+ dy y)]) shape ))  )

(defn h-reflect-shape [shape]
  (into [] (map (fn [[x y]] [(- x) y]) shape )) )

(defn v-reflect-shape [shape]
  (into [] (map (fn [[x y]] [x (- y)]) shape )) )

(defn close-shape "Closes an open shape" [points] (conj points (first points)))

(defn points-to-polars [points] (into [] (map rec-to-pol points)))
(defn polars-to-points [polars] (into [] (map pol-to-rec polars)))

(defn rotate-shape [da shape]
  (let [polars (points-to-polars shape)
        rotated (map (fn [[rad a]] [rad (+ da a)]) polars )
        ] (polars-to-points rotated) ) )

;; SShape (styled shape)
;; SShape, is a shape with a style attached ({:points points :style style}
;; style is a dictionary of style hints eg. colour

(defn empty-sshape [] {:style {} :points []})

(defn sshape [style points] {:style style :points points})

(defn add-style [new-style {:keys [style points]} ] {:points points :style (conj style new-style)})

(defn colour-sshape "Give new colour to a sshape " [colour sshape] (add-style {:colour colour} sshape))
(defn weight-sshape "Give new strokeWeight to a sshape" [weight sshape] (add-style {:stroke-weight weight} sshape))


(defn scale-sshape [val {:keys [style points]}]   {:style style :points (scale-shape val points) } )

(defn translate-sshape [dx dy {:keys [style points]}] {:style style :points (translate-shape dx dy points)})

(defn h-reflect-sshape [{:keys [style points]}] {:style style :points (h-reflect-shape points)})
(defn v-reflect-sshape [{:keys [style points]}] {:style style :points (v-reflect-shape points)})

(defn rotate-sshape [da {:keys [style points]}] {:style style :points (rotate-shape da points)} )

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

;; Groups
(defn group "a vector of sshapes"
  [& sshapes]
  (into [] sshapes) ) 


(defn scale-group
  ([val group] (into [] (map (partial scale-sshape val) group )))   )

(defn translate-group
  [dx dy group] (into [] (map (partial translate-sshape dx dy) group))  )

(defn translate-group-to
  [x y group]
  (translate-group (- x) (- y) group) )  

(defn h-reflect-group [group] (into [] (map h-reflect-sshape group) ) )
(defn v-reflect-group [group] (into [] (map v-reflect-sshape group) ) )

(defn rotate-group [da group] (into [] (map (partial rotate-sshape da) group)))

(defn cross-group "A cross, can only be made as a group (because sshapes are continuous lines) which is why we only define it now"
  [colour x y]
  (group (colour-sshape colour (horizontal-sshape y)) (colour-sshape colour (vertical-sshape  x)))  )


;; Layouts
(defn superimpose-pattern "simplest layout, two groups lacated on top of each other "
  [group1 group2]
  (into [] (concat group1 group2))   )


(defn cart "Cartesian Product of two collections" [colls]
  (if (empty? colls)
    '(())
    (for [x (first colls)
          more (cart (rest colls))]
      (cons x more))))

(defn grid-pattern-positions "calculates the positions for a gid pattern"
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
  [group positions]  
  (concat ( mapcat (fn [[x y]] (translate-group x y group)) positions) )) 


(defn grid-pattern "Takes a group and a number of repetitions n and returns that group repeated in an n X n grid "
  [number group]
  (let [scaler (/ 1 number)
       scaled (scale-group scaler group) ]
    (apply-positions scaled (grid-pattern-positions number)) )
  )

(defn drop-every [n xs]
  (lazy-seq (if (seq xs) (concat (take (dec n) xs) (drop-every n (drop n xs))))))

(defn check-seq "returns the appropriate lazy seq of patterns for constructing a checker-pattern"
  [number group1 group2]
  (let [scaler (/ 1 number)
        scaled1 (scale-group scaler group1)
        scaled2 (scale-group scaler group2)]
    (if (= 0 (mod number 2))
      (drop-every (+ 1 number) (cycle [scaled1 scaled2]))
      (cycle [scaled1 scaled2]) ) ) )


(defn checker-pattern "takes number n and two groups and lays out alternating copies of the groups on an n X n checkerboard"
  [number group1 group2]
  (let [c-seq (check-seq number group1 group2)
        pattern-positions (map vector c-seq (grid-pattern-positions number))]
    (concat (mapcat (fn [[group [x y]]] (translate-group x y group)) pattern-positions ))
    )
  )


(defn one-row "takes a total number of rows, an index i and two groups. Makes an n X n square where row i is group 2 and everything else is group1"
  [n i group1 group2]
  (let [scaler (/ 1 n)
        scaled1 (scale-group scaler group1)
        scaled2 (scale-group scaler group2)
        the-seq ( ())
        ]
    ())
  )

(defn four-mirror "Four-way mirroring. Returns the group repeated four times reflected vertically and horizontall" [group]
  (let [
        nw (translate-group (- 0.5) (- 0.5) (scale-group (float (/ 1 2)) group))
        ne (h-reflect-group nw)
        sw (v-reflect-group nw)
        se (h-reflect-group sw) ]
    (concat nw ne sw se)))

(defn clock-rotate "Circular layout. Returns n copies in a rotation"
  [n group]
  (let [angs (angles n)]
    (concat (mapcat (fn [a] (rotate-group a group)) angs )) 
   ))

;; Viewing pipeline

(defn tx
  "transform a scalar from one space to another. o1 is origin min, o2 is origin max, t1 is target min, t2 is target max"
  [o1 o2 t1 t2 x]
  (+ (* (float (/ (- x o1) (- o2 o1))) (- t2 t1)) t1) )


(defn make-txpt
  "first argument is viewport (left, top, right, bottom), second is window (left, top, right, bottom)
   returns a mapping function from viewport to window"
  [[vx1 vy1 vx2 vy2] [wx1 wy1 wx2 wy2]]
  (fn [[x y]] [(tx vx1 vx2 wx1 wx2 x) (tx vy1 vy2 wy1 wy2 y)] )   )

(defn project-points
  ([txpt points] (map txpt points))
  ([viewport window points] (map (make-txpt viewport window) points) )  )


(defn points-to-segments "turns a vector of points to a vector of segments"
  [points]
  (if (< (count points) 2)
    []
    (loop [p (first points) ps (rest points) acc [] ]  
      (let [nxt [p (first ps)]]
        (if (< (count ps) 2)
          (conj acc nxt)
          (recur (first  ps) (rest ps) (conj acc nxt))
          )
        )
      )
    )
  )


(defn transformed-sshape [txpt {:keys [style points]}]
  {:style style :points (points-to-segments (project-points txpt points)) } )



;; Interactive Bit. Only this bit should be Quil / Processing dependent. 

(defn draw-seg "draws a segment"
  [[[x1 y1][x2 y2]]]
  (line x1 y1 x2 y2)  )

(defn draw-sshape "a sshape has a list of points, here's where we project it to window and turn it into a segment list "
  [txpt {:keys [style points] :as sshape}]
  (let [tsshape (transformed-sshape txpt sshape)]
    (push-style)
    (if (contains? style :colour) (stroke (get style :colour)) )
    (if (contains? style :stroke-weight) (stroke-weight (get style :stroke-weight)))
    (dorun (map draw-seg (get tsshape :points) ) )
    (pop-style)
    )
  )


(defn draw-group 
  [txpt group]
  (dorun (map (partial draw-sshape txpt) group))  )


(defn setup []
  (frame-rate 1)      
  (background 0)) 

(defn draw []
  (stroke-weight 1)
  (color 255)
  (fill 0)
  (background 0)

  (let [txpt (make-txpt [-1 -1 1 1] [0 0 (width) (height)])
        my-green (color 100 200 100)
        my-purple (color 150 100 200)
        my-blue (color 100 100 200 150)
        my-red (color 200 100 100)
        my-yellow (color 250 250 150 150)
        square (group  {:style {:colour my-yellow} :points [[-1 -1] [-1 1] [1 1] [1 -1] [-1 -1]] } )
        basic (group                  
               (weight-sshape 2 (colour-sshape my-red (poly 0 0 0.7 3) ))
               (colour-sshape my-yellow (poly 0.3 0.6 0.5 7) )                 
               (clock-rotate 6 (group  (colour-sshape my-purple (poly -0.3 0.5 0.1 12) )))
        )
        cross (rotate-group (- (rand (/ PI 2)) (/ PI 4)) (cross-group (color 100 200 100) 0 0))
        clock (clock-rotate 12 (group  (colour-sshape my-yellow (poly (rand 1) (rand 1)  0.06 4))
                                       (colour-sshape my-blue (drunk-line 9 0.2))))

        ]

    
    (draw-group txpt (checker-pattern 4
                                      (superimpose-pattern square (checker-pattern 3 clock cross))
                                      (four-mirror (rotate-group (/ PI (+ 1 (rand 10)))  basic))
                                      )
                ) )
   (smooth)
   (smooth) 
  )


(defn -main [& args]
  (sketch
   :title "Patterning"
   :setup setup
   :draw draw
   :size [700 700]
   :on-close #(System/exit 0)))
