(ns patterning.view
  (:require [patterning.sshapes :refer []])
  (:require [patterning.groups :refer []])
  (:require [patterning.color :refer [color-to-web p-color]])
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


(defn sshape-to-SVG-path [txpt sshape]
  (let [linify (fn [[x y]] (str " L " x " " y))
        bezify (fn [[x y]] (str x " " y " " ))

        {:keys [style points] :as tss} (transformed-sshape txpt sshape)
        p (first points)
        s1 (str "M " (first p) " " (last p) )
        
        strung (if (contains? style :bezier)
                 (cons s1 (cons "C" (mapcat bezify (rest points) )))
                 (cons s1 (mapcat linify (rest points)))
                 )
        col (if (contains? style :color) (color-to-web (get style :color)) (color-to-web (p-color 0)) )
        fill (str "fill=\""  (if (contains? style :fill)
                               (color-to-web (get style :fill))
                               "none")
                  "\" ")
        ]
    
    (str (format "\n<path style='-webkit-tap-highlight-color: rgba(0, 0, 0, 0);' stroke='%s' %s " col fill)
         "d='"
         (apply str strung)
         "'></path>"
         )  ) )

(defn xml-tpl
  "svg 'template' which also flips the coordinate system via http://www.braveclojure.com/organization/"
  [txpt width height group]
  (str "<svg height=\"" height "\" width=\"" width "\">"

       (clojure.string/join  (apply str (mapcat (partial sshape-to-SVG-path txpt) group)))

       "</svg>"))

(defn write-svg "writes svg" [width height group]
  (spit "out.svg" (xml-tpl (make-txpt [-1 -1 1 1] [0 0 width height]) width height group))   )

