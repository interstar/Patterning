(ns patterning.core
  (:require [quil.core :refer :all])
  (:gen-class))

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



(defn drop-every [n xs] (lazy-seq (if (seq xs) (concat (take (dec n) xs) (drop-every n (drop n xs))))))

(defn scale-pair "returns [scaler scaled1 scaled2] " [number group1 group2]
  (let [scaler (/ 1 number)] 
    [scaler (scale-group scaler group1) (scale-group scaler group2) ]))

(defn check-seq "returns the appropriate lazy seq of groups for constructing a checker-layout"
  [number group1 group2]
  (let [ [scaler scaled1 scaled2] (scale-pair number group1 group2) ]
    (if (= 0 (mod number 2))
      (drop-every (+ 1 number) (cycle [scaled1 scaled2]))
      (cycle [scaled1 scaled2]) ) ) )


(defn checker-layout "takes number n and two groups and lays out alternating copies of the groups on an n X n checkerboard"
  [number group1 group2]
  (let [c-seq (check-seq number group1 group2)
        layout-positions (map vector c-seq (grid-layout-positions number))]
    (concat (mapcat (fn [[group [x y]]] (translate-group x y group)) layout-positions )) ) )


(defn one-x-layout
  "takes a total number of rows, an index i and two groups.
   Makes an n X n square where row or col i is group 2 and everything else is group1"
  [n i f group1 group2]
  (let [[scaler scaled1 scaled2] (scale-pair n group1 group2)        
        the-seq (concat (repeat (* n i) scaled1) (repeat n scaled2) (repeat (* n (- n i)) scaled1) )
        layout-positions (map vector the-seq (grid-layout-positions n)) ]       
    (concat (mapcat f layout-positions))
    )
  )

(defn one-row-layout "uses one-x-layout with rows"
  [n i group1 group2] (one-x-layout n i (fn [[group [x y]]] (translate-group y x group)) group1 group2  ))

(defn one-col-layout "uses one-x-layout with rows"
  [n i group1 group2] (one-x-layout n i (fn [[group [x y]]] (translate-group x y group)) group1 group2 ) )



(defn four-mirror "Four-way mirroring. Returns the group repeated four times reflected vertically and horizontall" [group]
  (let [nw (translate-group (- 0.5) (- 0.5) (scale-group (float (/ 1 2)) group))
        ne (h-reflect-group nw)
        sw (v-reflect-group nw)
        se (h-reflect-group sw) ]
    (concat nw ne sw se)))

(defn clock-rotate "Circular layout. Returns n copies in a rotation"
  [n group]
  (let [angs (angles n)]
    (concat (mapcat (fn [a] (rotate-group a group)) angs )) 
   ))


(defn q1-rot-group [group] (rotate-group (float (/ PI 2)) group ) )
(defn q2-rot-group [group] (rotate-group PI group))
(defn q3-rot-group [group] (rotate-group (-  (float (/ PI 2))) group))

(defn four-round "Four squares rotated" [group]
  (let [scaled (scale-group (float (/ 1 2)) group)
        p2 (float (/ PI 2))
        nw (translate-group (- 0.5) (- 0.5) scaled )
        ne (translate-group 0.5 (- 0.5) (q1-rot-group scaled)) 
        se (translate-group (- 0.5) 0.5 (q3-rot-group scaled))
        sw (translate-group 0.5 0.5 (q2-rot-group scaled) )
        ]
    (concat nw ne se sw )  )  )


(defn random-grid-layout "Takes a group and returns a grid with random quarter rotations"
  [number group]
  (let [scaler (/ 1 number)
        scaled (scale-group scaler group)
        random-turn (fn [group]
                      (case (rand-int 4)
                        0 group
                        1 (q1-rot-group group)
                        2 (q2-rot-group group)
                        3 (q3-rot-group group)  ) )
        groups (into [] (map random-turn (repeat (* number number) scaled)))
        ]
    (place-groups-at-positions groups (grid-layout-positions number)) )
  )


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
      (let [left-eye (stretch-sshape 1.3 1 (add-style { :colour eye-colour :fill eye-colour} (poly -0.3 -0.1 0.1 eye-sides)))
            right-eye (h-reflect-sshape left-eye)
            ]
                
        (group (add-style {:colour head-colour :fill head-colour} (poly 0 0 0.8 head-sides))
               (stretch-sshape 1.3 0.4 (add-style {:colour mouth-colour :fill mouth-colour} (poly 0 1.3 0.2 mouth-sides)))
               (translate-sshape 0 0.1
                                 (stretch-sshape 0.6 1.1 (rotate-sshape (/ PI 2)
                                 (add-style {:colour nose-colour :fill nose-colour} (poly 0 0 0.2 nose-sides)))))
               left-eye
               right-eye) ) ) )


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


(defn transformed-sshape [txpt {:keys [style points]}]
  {:style style :points (project-points txpt points) } )


;; Interactive Bit. Only this bit should be Quil / Processing dependent. 

(defn draw-sshape "using vertices"
  [txpt {:keys [style points] :as sshape}]
  (if (sshape-hidden? sshape) ()
      (let [tsshape (transformed-sshape txpt sshape)]    
        (push-style)
        (if (contains? style :colour) (stroke (get style :colour)))
        (if (contains? style :fill) (fill (get style :fill)))
        (if (contains? style :stroke-weight) (stroke-weight (get style :stroke-weight)))
        (begin-shape)
        (dorun (map (fn [[x y]] (vertex x y)) (get tsshape :points)))
        (end-shape )
        (pop-style)
        ))
  )

(defn draw-group 
  [txpt group]
  (dorun (map (partial draw-sshape txpt) group))  )


(defn setup []
  (no-loop)

  (let [
        my-green (color 100 200 100 100)
        my-purple (color 150 100 200)
        my-blue (color 100 100 200 150)
        my-red (color 200 100 100)
        my-yellow (color 250 250 150 200)
        my-cyan (color 150 250 250)
        my-cream (color 220 210 180)
        
        square (group  {:style {:colour my-yellow} :points [[-1 -1] [-1 1] [1 1] [1 -1] [-1 -1]] } )
        basic (superimpose-layout  (group                  
                                     (fill-sshape my-red (hide-sshape (weight-sshape 2 (colour-sshape my-red (poly 0 0 0.7 3) ))))
                                     (fill-sshape my-yellow (colour-sshape my-yellow (poly 0.3 0.6 0.5 7) )) )
                                    (clock-rotate 6 (group  ( colour-sshape my-purple (poly (- 0.3) (- 0.5) 0.3 4) )))
                                    )
        cross (rotate-group (- (rand (/ PI 2)) (/ PI 4)) (cross-group (color 100 200 100) 0 0))
        blue-cross (rotate-group (- (rand (/ PI 2)) (/ PI 4)) (cross-group (color 100 100 200) 0 0))
        clock (clock-rotate 12 (group  (fill-sshape my-blue (weight-sshape 2 (colour-sshape my-blue (poly (rand 1) (rand 1)  0.12 4))))
                                       (fill-sshape my-yellow (weight-sshape 2 (colour-sshape my-yellow (drunk-line 9 0.2))))))
        flake (spoke-flake-group {:colour my-cyan :stroke-weight 2})
        face (scale-group 0.8 (face-group [20 my-cream] [5 my-blue] [3 my-purple]  [8 my-red]))

        red-ball (group (add-style {:colour my-purple :fill my-red} (poly 0 -0.82 0.05 3)))
        simple-clock (clock-rotate 8 (group (add-style {:colour my-green :fill my-cyan } (poly 0.5 0 0.2 8))))

        
        test-shape (group
;;                     (add-style {:colour my-red :fill my-yellow :stroke-weight 2} (poly 0 0 0.8 3))
;;                     (add-style {:colour my-blue :fill my-blue :stroke-weight 2} (poly 0 (- 0.5) 0.2 8))
;;                     (sshape {:colour my-green :stroke-weight 3} [[0 0] [0 (- 1)]])
                    (sshape {:color (color 255) :fill (color 255)} [[-1 -1] [-1 1] [1 1]] )
                    )
        txpt (make-txpt [-1 -1 1 1] [0 0 (width) (height)])
        ]

        (stroke-weight 1)
        (color 255)
        (no-fill)
        (background 0)
        (draw-group txpt
                    (superimpose-layout 
                     (random-grid-layout 8 (repeat 64 test-shape))
                     basic ) 
                    
                    )
        (smooth)
        (smooth)) 
  )  

(defn draw [])


(defn -main [& args]
  (sketch
   :title "Patterning"
   :setup setup
   :draw draw
   :size [700 700]
   :on-close #(System/exit 0)))
