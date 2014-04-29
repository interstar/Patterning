(ns patterning.view
  (:require [quil.core :refer :all])
  (:require [patterning.geometry :refer :all]) 
  )

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


;; SVG generation

(defn colour-to-web [colour]
  (str (format  "#%x%x%x" (int (red colour)) (int (green colour)) (int  (blue colour))) ))

(defn sshape-to-SVG-path [txpt sshape]
  (let [stringify (fn [[x y]] (str " L " x " " y))
        {:keys [style points] :as tss} (transformed-sshape txpt sshape)
        p (first points)
        s1 (str "M " (first p) " " (last p) )
        strung (cons s1 (mapcat stringify (rest points)))
        col (colour-to-web (get style :colour))]
    
    (str (format "\n<path style='-webkit-tap-highlight-color: rgba(0, 0, 0, 0);' fill='none' stroke='%s' " col)
         "d='"
         (apply str  (cons s1 (map stringify (rest points))))
         "'></path>"
         )  ) )

(defn xml-tpl
  "svg 'template' which also flips the coordinate system via http://www.braveclojure.com/organization/"
  [txpt width height group]
  (str "<svg height=\"" height "\" width=\"" width "\">"
;;       "<g transform=\"translate(0," height ")\">"
;;       "<g transform=\"scale(-1,1)\">"
       (clojure.string/join  (apply str (mapcat (partial sshape-to-SVG-path txpt) group)))
;;       "</g></g>"
       "</svg>"))

(defn write-svg "writes svg"
  [txpt width height group]
  (spit "out.svg" (xml-tpl txpt width height group))
  )


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

