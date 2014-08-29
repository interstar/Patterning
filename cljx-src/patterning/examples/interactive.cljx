(ns patterning.examples.interactive
  (:require [patterning.maths :as maths] 
            [patterning.sshapes :refer [->SShape poly ]]
            [patterning.groups :refer [group clip rotate scale translate h-reflect reframe over-style]]
            [patterning.layouts :refer [clock-rotate stack flower-of-life-positions nested-stack 
                                        place-groups-at-positions framed grid-layout checked-layout four-mirror]]
            [patterning.library.turtle :refer [basic-turtle]]
            [patterning.library.l_systems :refer [l-system]]
            [patterning.color :refer [p-color setup-colors]]
            [patterning.examples.design_language1 :as design-language]
            [patterning.examples.tutorial :as tutorial]
            [patterning.library.complex_elements :as complex_elements]
            [patterning.library.symbols :as symbols]
            [patterning.examples.framedplant :as framedplant]
            )  )

; "Interactive" examples ... these are paramaterised shapes designed
; to be called from Javascript 

(defn framed_fol [frame-size rotated] (stack tutorial/m2 (scale 0.7 (symbols/folexample)) )
  (framed frame-size (repeat (clock-rotate rotated tutorial/dline))
           (repeat tutorial/triangles) (symbols/folexample))
  )


(def l-sys  (l-system [["F" "F[+F]F[-F][GF]"] ["G" "H"] ["H" "IZ"] ]))
        
(defn draw-it [da]
  (basic-turtle [0 0] 0.1 (/ maths/PI -2) (/ maths/PI da) (l-sys 4 "F")
                {\Z (fn [x y a] (let [] ( group (poly x y 0.05 8 {:fill design-language/my-red}))))}
                {:color design-language/my-green :stroke-weight 2}  ))

(defn _spray [da] (translate -0.6 0 (h-reflect (reframe (draw-it da)))))
(def spray (memoize _spray))

(def vase (memoize complex_elements/vase))

(defn _diamonds [shrink]
  (scale 0.9
         (nested-stack
          (setup-colors [design-language/my-blue
                         design-language/my-yellow
                         design-language/my-red ]
                        (p-color 0 0 255))
          design-language/simple-diamond (fn [x] (- x shrink)) )))

(def diamonds (memoize _diamonds))

(def flower (over-style {:color (p-color 240 200 180)
                         :fill (p-color 200 150 100)} design-language/flower ))

 
(defn test1 [frame-size grid-size shrink ]
  (framed frame-size (repeat flower) (repeat (diamonds shrink))
          (grid-layout grid-size (cycle [flower (diamonds shrink)]))))


(def tri (over-style {:color (p-color 100 100 255)}  tutorial/triangles) )
(defn test2 [grid-size rotates ]
  (stack (checked-layout grid-size
                         (repeat tri )
                         (repeat (diamonds 0.3)))
         (four-mirror (over-style {:fill (p-color 100 200 150 180)} (clock-rotate rotates tutorial/dline))))
  )

