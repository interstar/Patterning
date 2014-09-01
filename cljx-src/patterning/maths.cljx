(ns patterning.maths)

;; My maths library (to factor out all the maths functions that will
;; need to be different in Clojure / ClojureScript cljx

(def PI #+clj(Math/PI) #+cljs js/Math.PI )
(def half-PI (float (/ PI 2)))
(def q-PI (float (/ PI 4)))

(defn sqrt [x] #+clj (Math/sqrt x) #+cljs (js/Math.sqrt x) )
(defn abs [x] (Math/abs x))
(defn atan2 [x y] (Math/atan2 x y))
(defn cos [a] (Math/cos a))
(defn sin [a] (Math/sin a))


(defn clock-angles [number]  (take number (iterate (fn [a] (+ a (float (/ (* 2 PI) number)))) (- PI))) )

(defn tx
  "transform a scalar from one space to another. o1 is origin min, o2 is origin max, t1 is target min, t2 is target max"
  [o1 o2 t1 t2 x]
  (+ (* (float (/ (- x o1) (- o2 o1))) (- t2 t1)) t1) )


(defn mol= "more or less equal" [x y] (< (Math/abs (- x y)) 0.0000001) )
(defn molp= "more or less equal points" [[x1 y1] [x2 y2]] (and (mol= x1 x2) (mol= y1 y2)))



;;(defn mol=s "more or less equal vectors" [v1 v2] (and (mol= (first v1) (first v2)) (molv= (rest v1) (rest v2))))

;; Point geometry

(defn f-eq "floating point equality" [a b]  (<= (abs (- a b)) 0.00001))
(defn p-eq "point equality" [[x1 y1] [x2 y2]] (and (f-eq x1 x2) (f-eq y1 y2)))

(defn add-points [[x1 y1] [x2 y2]] [(+ x1 x2) (+ y1 y2)])
(defn diff [[x1 y1] [x2 y2]] (let [dx (- x2 x1) dy (- y2 y1)]  [dx dy]) )
(defn magnitude [[dx dy]] (sqrt (+ (* dx dx) (* dy dy) )))
(defn distance [p1 p2] ( (comp magnitude diff) p1 p2) )
(defn unit [[dx dy]] (let [m (magnitude [dx dy])] [(float (/ dx m)) (float (/ dy m))]) )

(defn rev [[dx dy]] [(- dx) (- dy)])

(defn rec-to-pol [[x y]] [(sqrt (+ (* x x) (* y y))) (atan2  x y)])
(defn pol-to-rec [[r a]] [(* r (cos a)) (* r (sin a))])

(defn line-to-segments [points]
  (if (empty? points) [] 
      (loop [p (first points) ps (rest points) acc []]
        (if (empty? ps) acc
            (recur (first ps) (rest ps) (conj acc [p (first ps)]) )  )        
        )))

(defn rotate-point [a [x y]]
  (let [cos-a (cos a) sin-a (sin a)]  
    [(- (* x cos-a) (* y sin-a))
     (+ (* x sin-a) (* y cos-a))]))


(defn wobble-point "add some noise to a point, qx and qy are the x and y ranges of noise"
  [[qx qy]  [x y]]
  (let [wob (fn [n qn] (+ n (- (rand qn) (/ qn 2))))]
     [(wob x qx) (wob y qy)]  ) )

