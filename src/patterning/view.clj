(ns patterning.view
  (:require [quil.core :refer :all])
  (:require [patterning.geometry :refer :all])
  (:require [patterning.color :refer :all])
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


;; Interactive Bit. Only this bit should be Quil / Processing dependent. 

;; we are now using our p-color to represent colors, this converts it
;; into Processing format
(defn mk-color [[r g b a]] (color r g b a))

(defn draw-sshape "using vertices"
  [txpt {:keys [style points] :as sshape}]
  (if (sshape-hidden? sshape) ()
      (let [tsshape (transformed-sshape txpt sshape)]    
        (push-style)
        (if (contains? style :color) (stroke  (mk-color (get style :color))))
        (if (contains? style :fill) (fill (mk-color (get style :fill))))
        (if (contains? style :stroke-weight) (stroke-weight (get style :stroke-weight)))
        (if (contains? style :bezier)
          (let [ls (flat-point-list tsshape) ]             
            (apply bezier ls) 
            )
          
          (do (begin-shape)
            (dorun (map (fn [[x y]] (vertex x y)) (get tsshape :points)))
            (end-shape ))
           )
        (pop-style)
        ))
  )

(defn draw-group 
  [txpt group]
  (dorun (map (partial draw-sshape txpt) group))  )

