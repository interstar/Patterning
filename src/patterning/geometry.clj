(ns patterning.geometry
 (:require [quil.core :refer :all])
 (:require [clojure.math.numeric-tower :as math])

  )
  

;; Point geometry

(defn rec-to-pol [[x y]] [(math/sqrt (+ (* x x) (* y y))) (atan2 x y)])
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
  (let [ offset (/ 2 number)
         inc (fn [x] (+ offset x))
         ino (float (/ offset 2))
         init (- ino 1)
         h-iterator (take number (iterate inc init))
         v-iterator (take number (iterate inc init)) ]
  (cart [h-iterator v-iterator])  ) )

(defn half-drop-grid-layout-positions "Like a grid but with a half-drop every other column"
  [number]
  (let [ offset (/ 2 number)
        n2 (int  (/ number 2))
        inc-x (fn [x] (+ (* 2 offset) x))
        inc-y (fn [y] (+ offset y))
        in-x (float (/ offset 2))
        in-y (float (/ offset 2))
        
        init-x1 (- in-x 1)
        init-x2 (- in-x (- 1 offset))
        init-y1 (- in-y 1)
        init-y2 (- in-y (+ 1 ( / offset 2)))
        
        h1-iterator (take n2 (iterate inc-x init-x1))
        v1-iterator (take number (iterate inc-y init-y1))
        h2-iterator (take n2 (iterate inc-x init-x2))
        v2-iterator (take (+ 1 number) (iterate inc-y init-y2))
        ]
    (concat (cart [h1-iterator v1-iterator]) (cart [h2-iterator v2-iterator]))))

(defn place-groups-at-positions "Takes a list of groups and a list of positions and puts one of the groups at each position"
  [groups positions]
  (concat ( mapcat (fn [[ [x y] group]] (translate-group x y group)) (map vector positions groups) ) )) 

(defn scale-group-stream [n groups] (map (partial scale-group (/ 1 n)) groups))

(defn grid-layout "Takes an n and a group-stream and returns items from the group-stream in an n X n grid "
  [n groups] (place-groups-at-positions (scale-group-stream n groups) (grid-layout-positions n))  )

(defn half-drop-grid-layout "Like grid but with half-drop"
  [n groups] (place-groups-at-positions (scale-group-stream n groups) (half-drop-grid-layout-positions n)))

(defn q1-rot-group [group] (rotate-group (float (/ PI 2)) group ) )
(defn q2-rot-group [group] (rotate-group PI group))
(defn q3-rot-group [group] (rotate-group (-  (float (/ PI 2))) group))


(defn random-turn-groups [groups]
  (let [random-turn (fn [group]
                      (case (rand-int 4)
                        0 group
                        1 (q1-rot-group group)
                        2 (q2-rot-group group)
                        3 (q3-rot-group group)  ) ) ]
    (map random-turn groups) ))

(defn random-grid-layout  "Takes a group and returns a grid with random quarter rotations"
  [n groups] (grid-layout n (random-turn-groups groups)))


(defn drop-every [n xs] (lazy-seq (if (seq xs) (concat (take (dec n) xs) (drop-every n (drop n xs))))))


(defn check-seq "returns the appropriate lazy seq of groups for constructing a checked-layout"
  [n groups1 groups2]
  (let [ together (scale-group-stream n (interleave groups1 groups2) ) ]
    (if (= 0 (mod n 2))
      (drop-every (+ 1 n) together)
      together ) ) )


(defn checked-layout "takes number n and two group-streams and lays out alternating copies of the groups on an n X n checkerboard"
  [number groups1 groups2]
  (let [c-seq (check-seq number groups1 groups2)
        layout (map vector c-seq (grid-layout-positions number)  )]
    (concat (mapcat (fn [[group [x y]]] (translate-group x y group)) layout )) ) )



(defn one-x-layout
  "Takes a total number of rows, an index i and two group-streams.
   Makes an n X n square where row or col i is from group-stream2 and everything else is group-stream1"
  [n i f groups1 groups2]
  (let [the-seq (concat (take (* n i) groups1) (take n groups2) (take (* n (- n i)) groups1) )
        layout-positions (map vector (scale-group-stream n the-seq) (grid-layout-positions n))        
        ]
     (concat (mapcat f layout-positions))
    )
  )

(defn one-row-layout "uses one-x-layout with rows"
  [n i groups1 groups2] (one-x-layout n i (fn [[group [x y]]] (translate-group y x group)) groups1 groups2  ))

(defn one-col-layout "uses one-x-layout with rows"
  [n i groups1 groups2] (one-x-layout n i (fn [[group [x y]]] (translate-group x y group)) groups1 groups2 ) )


(defn alt-cols "Fills a group-stream with cols from alternative group-streams"
  [n groups1 groups2]
  (cycle (concat (take n groups1) (take n groups2)))  )

(defn alt-rows "Fills a group-stream with rows from alternative group-streams"
  [n groups1 groups2]
  (interleave groups1 groups2))

(defn alt-cols-grid-layout "Every other column from two streams" [n groups1 groups2]
  (grid-layout n (alt-cols n groups1 groups2)))

(defn alt-rows-grid-layout "Every other row from two streams" [n groups1 groups2]
  (grid-layout n (alt-rows n groups1 groups2)))

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


(defn four-round "Four squares rotated" [group]
  (let [scaled (scale-group (float (/ 1 2)) group)
        p2 (float (/ PI 2))
        nw (translate-group (- 0.5) (- 0.5) scaled )
        ne (translate-group 0.5 (- 0.5) (q1-rot-group scaled)) 
        se (translate-group (- 0.5) 0.5 (q3-rot-group scaled))
        sw (translate-group 0.5 0.5 (q2-rot-group scaled) )
        ]
    (concat nw ne se sw )  )  )



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
