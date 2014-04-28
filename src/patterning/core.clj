(ns patterning.core
  (:require [quil.core :refer :all])
  (:require [patterning.geometry :refer :all]) 
  (:gen-class))




(defn scale-pair "returns [scaler scaled1 scaled2] " [number group1 group2]
  (let [scaler (/ 1 number)] 
    [scaler (scale-group scaler group1) (scale-group scaler group2) ]))

(defn check-seq "returns the appropriate lazy seq of groups for constructing a checked-layout"
  [n groups1 groups2]
  (let [ together (map (partial scale-group (/ 1 n)) (interleave groups1 groups2) )  ]
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
  (let [scale-fn (fn [group] (scale-group (/ 1 n) group))
        the-seq (concat (take (* n i) groups1) (take n groups2) (take (* n (- n i)) groups1) )
        scaled-seq (into [] (map scale-fn the-seq))
        layout-positions (map vector scaled-seq (grid-layout-positions n))        
        ]
     (concat (mapcat f layout-positions))
    )
  )

(defn one-row-layout "uses one-x-layout with rows"
  [n i groups1 groups2] (one-x-layout n i (fn [[group [x y]]] (translate-group y x group)) groups1 groups2  ))

(defn one-col-layout "uses one-x-layout with rows"
  [n i groups1 groups2] (one-x-layout n i (fn [[group [x y]]] (translate-group x y group)) groups1 groups2 ) )



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
                                     (fill-sshape my-red (weight-sshape 2 (colour-sshape my-red (poly 0 0 0.5 3) )))
                                     (fill-sshape my-yellow (colour-sshape my-yellow (poly 0.3 0.6 0.2 7) )) )
                                     (clock-rotate 6 (group  (add-style {:colour my-purple :stroke-weight 2 } (poly (- 0.3) (- 0.5) 0.3 4) )))
                                    )
        cross ( rotate-group (- (rand (/ PI 2)) (/ PI 4)) ( cross-group my-red 0 0))
        blue-cross (rotate-group (- (rand (/ PI 2)) (/ PI 4)) (cross-group (color 100 100 200) 0 0)) 
        clock (clock-rotate 12 (group  (fill-sshape my-yellow (weight-sshape 2 (colour-sshape my-yellow (poly (rand 1) (rand 1)  0.12 4))))
                                       (fill-sshape my-blue (weight-sshape 2 (colour-sshape my-blue (drunk-line 9 0.2))))))
        flake (spoke-flake-group {:colour my-cream :fill my-cyan })
        face (scale-group 0.8 (face-group [20 my-cream] [5 my-blue] [3 my-purple]  [8 my-red]))

        red-ball (group (add-style {:colour my-purple :fill my-red} (poly 0 -0.82 0.05 3)))
        simple-clock (clock-rotate 8 (group (add-style {:colour my-green :fill my-red } (poly 0.5 0 0.2 8))))

        
        test-shape (group
;;                     (add-style {:colour my-red :fill my-yellow :stroke-weight 2} (poly 0 0 0.8 3))
;;                     (add-style {:colour my-blue :fill my-blue :stroke-weight 2} (poly 0 (- 0.5) 0.2 8))
;;                     (sshape {:colour my-green :stroke-weight 3} [[0 0] [0 (- 1)]])
                    (sshape {:colour (color 200 250 200) :fill (color 160 240 160)} [[-1 -1] [-1 1] [1 1]] )
                    (sshape {:colour (color 100 100 200) :fill (color 50 50 150) } [[-1 -1] [1 -1] [1 1]])
                    (random-rect {:fill (color 0)})
                    (random-rect {:fill (color 0)})
                    (random-rect {:fill (color 0)})
                    (random-rect {:fill (color 0)})
                    
                    )
        txpt (make-txpt [-1 -1 1 1] [0 0 (width) (height)])
        ]

        (stroke-weight 1)
        (color 255)
        (no-fill)
        (background 0)
        (draw-group txpt
                    (checked-layout 6
                                     (cycle [(four-mirror test-shape) simple-clock]  )
                                     (random-turn-groups (cycle  [(four-mirror  test-shape) clock] )) ) 
                    
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
