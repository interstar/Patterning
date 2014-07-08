(ns patterning.layouts
  (:require [patterning.maths :as maths]
            [patterning.groups :as groups])  )


;; Layouts
;; Note layouts combine and multiply groups to make larger groups

(defn superimpose-layout "simplest layout, two groups located on top of each other "
  [group1 group2] (into [] (concat group1 group2))   )

(defn stack "superimpose a number of groups"
  [& groups] (reduce superimpose-layout groups))

(defn nested-stack "superimpose smaller copies of a shape"
  [styles group reducer]
  (let [gen-next (fn [[style x]] (groups/over-style style (groups/scale x group)))]    
    (into [] (stack (mapcat gen-next (map vector styles (iterate reducer 1 )))))  ))


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
        
        h1-iterator (take (if (even? n2) n2 (+ 1 n2)) (iterate inc-x init-x1))
        v1-iterator (take number (iterate inc-y init-y1))
        h2-iterator (take n2 (iterate inc-x init-x2))
        v2-iterator (take (+ 1 number) (iterate inc-y init-y2))
        h-iterator (interleave h1-iterator h2-iterator)
        v-iterator (interleave v1-iterator v2-iterator)
        ]
    (concat (cart [h1-iterator v1-iterator]) (cart [h2-iterator v2-iterator]))))


(defn diamond-layout-positions "Diamond grid, actually created like a half-drop"
  [number]
  (let [
        offset (/ 2 number)
        n2 (int  (/ number 1))
        inc-x (fn [x] (+ (* 1 offset) x))
        inc-y (fn [y] (+ offset y))
        in-x (float (/ offset 2))
        in-y (float (/ offset 2))
        
        init-x1 (- in-x (+ 1 (/ offset 2)))
        ;;        init-x2 (- in-x (- 1 offset))
        init-x2 (+ init-x1 (/ offset 2) )
        init-y1 (- in-y 1)
        init-y2 (- in-y (+ 1 ( / offset 2)))
        
        h1-iterator (take (+ 1 (if (even? n2) n2 (+ 1 n2))) (iterate inc-x init-x1))
        v1-iterator (take number (iterate inc-y init-y1))
        h2-iterator (take n2 (iterate inc-x init-x2))
        v2-iterator (take (+ 1 number) (iterate inc-y init-y2))
        h-iterator (interleave h1-iterator h2-iterator)
        v-iterator (interleave v1-iterator v2-iterator)
        ]
    (concat (cart [h1-iterator v1-iterator]) (cart [h2-iterator v2-iterator]))))


(defn place-groups-at-positions "Takes a list of groups and a list of positions and puts one of the groups at each position"
  [groups positions]
  (concat ( mapcat (fn [[ [x y] group]] (groups/translate x y group)) (map vector positions groups) ) )) 

(defn scale-group-stream [n groups] (map (partial groups/scale (/ 1 n)) groups))

(defn grid-layout "Takes an n and a group-stream and returns items from the group-stream in an n X n grid "
  [n groups] (place-groups-at-positions (scale-group-stream n groups) (grid-layout-positions n))  )

(defn half-drop-grid-layout "Like grid but with half-drop"
  [n groups] (place-groups-at-positions (scale-group-stream n groups) (half-drop-grid-layout-positions n)))

(defn diamond-layout "Like half-drop"
  [n groups] (place-groups-at-positions (scale-group-stream n groups) (diamond-layout-positions n)))

(defn q1-rot-group [group] (groups/rotate (float (/ maths/PI 2)) group ) )
(defn q2-rot-group [group] (groups/rotate maths/PI group))
(defn q3-rot-group [group] (groups/rotate (-  (float (/ maths/PI 2))) group))


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
  (let [ together (interleave groups1 groups2 ) ]
    (if (= 0 (mod n 2))
      (drop-every (+ 1 n) together)
      together ) ) )



(defn checked-layout "does checks using grid layout"
  [number groups1 groups2]
  (grid-layout number (check-seq number groups1 groups2)))


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
  [n i groups1 groups2] (one-x-layout n i (fn [[group [x y]]] (groups/translate y x group)) groups1 groups2  ))


(defn one-col-layout "uses one-x-layout with rows"
  [n i groups1 groups2] (one-x-layout n i (fn [[group [x y]]] (groups/translate x y group)) groups1 groups2 ) )

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
  (let [nw (groups/translate (- 0.5) (- 0.5) (groups/scale (float (/ 1 2)) group))
        ne (groups/h-reflect nw)
        sw (groups/v-reflect nw)
        se (groups/h-reflect sw) ]
    (concat nw ne sw se)))

(defn h-mirror "Reflect horizontally and stretch"  [group]
  (let [left  (groups/translate -0.5 0 (groups/stretch 0.5 1 group))
        right (groups/h-reflect left)]
    (stack left right)))

(defn v-mirror "Reflect vertically and stretch" [group]
  (let [top    (groups/translate 0 -0.5 (groups/stretch 1 0.5 group))
        bottom (groups/v-reflect top)]
    (stack top bottom)))

(defn clock-rotate "Circular layout. Returns n copies in a rotation"
  [n group]
  (let [angs (maths/clock-angles n)]
    (concat (mapcat (fn [a] (groups/rotate a group)) angs )) 
   ))


(defn four-round "Four squares rotated" [group]
  (let [scaled (groups/scale (float (/ 1 2)) group)
        p2 (float (/ maths/PI 2))
        nw (groups/translate (- 0.5) (- 0.5) scaled )
        ne (groups/translate 0.5 (- 0.5) (q1-rot-group scaled)) 
        se (groups/translate (- 0.5) 0.5 (q3-rot-group scaled))
        sw (groups/translate 0.5 0.5 (q2-rot-group scaled) )
        ]
    (concat nw ne se sw )  )  )

(defn frame "Frames consist of corners and edges. " [grid-size corners edges]
  (let [
        gs2 (- grid-size 2)
        [nw b c d] (into [] (take 4 corners))
        ne ( groups/h-reflect b)
        se ( groups/h-reflect (groups/v-reflect c ))
        sw ( groups/v-reflect d)
        edge (first edges)
        col (concat [ (q1-rot-group edge)]
                    (repeat gs2 (groups/empty-group))
                    [ (q3-rot-group edge)]
            )     ]
    (grid-layout grid-size (concat [nw] (repeat gs2 edge) [sw]
                                   (mapcat identity (repeat gs2 col))
                                   [ne] (repeat gs2 (q2-rot-group edge)) [se] ))
    )  )

(defn framed "Puts a frame around the other group" [grid-size corners edges inner]
  (let [gs2 (- grid-size 2)
        shrink (float (/ gs2 grid-size))]
    (stack (groups/scale shrink inner)
            (frame grid-size corners edges) ) ))


;; Flower of Life layout ... these are recursive developments of circles
(defn flower-of-life-positions [r depth [cx cy]]
  (if (= depth 0) [[cx cy]]      
      (let [round-points (map (fn [a] (maths/rotate-point a [(+ cx 0) (+ cy r)])) (maths/clock-angles 6) )
            rec-points (mapcat (partial flower-of-life-positions r (- depth 1)) round-points)]        
        (set (conj rec-points [cx cy]))
        ))  )
