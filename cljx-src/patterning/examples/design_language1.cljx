(ns patterning.examples.design_language1
  (:require [patterning.maths :as maths] 
            [patterning.sshapes :refer [add-style ->SShape set-weight set-color h-reflect]]
            [patterning.library.std :refer [poly square diamond drunk-line cross ogee]]
            [patterning.groups :refer [group rotate stretch scale] ]
            [patterning.groups :as groups]
            [patterning.layouts :refer [clock-rotate nested-stack stack diamond-layout v-mirror random-turn-groups
                                        superimpose-layout half-drop-grid-layout framed random-grid-layout four-round
                                        alt-rows-grid-layout checked-layout four-mirror grid-layout ]]
            [patterning.library.complex_elements :refer [petal-pair-group  spoke-flake-group
                                                         face-group polyflower-group]]

            [patterning.color :refer [p-color setup-colors mod-styles color-to-fill color-seq]])  
  )

;; ELEMENTS I'M USING TO MAKE PATTERNS. IF YOU WANT TO START
;; WITH YOUR OWN "DESIGN LANGUAGE" YOU CAN KILL ALL THESE 



;; The palette

(def my-green (p-color 100 250 130 ))
(def my-purple (p-color 150 100 200))
(def my-blue (p-color 100 100 255 ))
(def my-red (p-color 255 150 150))
(def my-yellow (p-color 240 250 200))
(def my-orange (p-color 255 128 64))
(def my-cyan (p-color 150 250 250))
(def my-cream (p-color 252 251 227))
(def my-burgundy (p-color 160 0 23))
(def my-pink (p-color 250 100 180))
(def my-black (p-color 0))        


(def simple-clock (clock-rotate 8 (poly 0.5 0 0.2 8
                                        {:stroke my-orange
                                         :fill my-cream
                                         :stroke-weight 1 })))
        
(def simple-diamond  (diamond {:stroke my-red :stroke-weight 2}))

(def flower-style {:stroke my-orange :stroke-weight 3  :bezier true})        
(def flower (clock-rotate 5 (stack (petal-pair-group flower-style 0.5 0.7) )))

(def less-complex-diamond (nested-stack
                           (setup-colors [my-blue my-pink ] (p-color 0))
                           simple-diamond (fn [x] (- x 0.2)) ))





        
(def complex-diamond (nested-stack (setup-colors  [my-green my-pink my-cream] (p-color 0))
                                          simple-diamond (fn [x] (- x 0.25)) ))
 
(def pink-tile (stack complex-diamond (group (->SShape {:stroke my-blue :stroke-weight 5} [[0 0] [0 1] ]))))
        
(def edge (rotate maths/half-PI (stretch 0.7 1 pink-tile)))

(def corner (rotate maths/q-PI edge))

(def red-ball 
  (poly 0 -0.82 0.3 8 {:stroke my-red :fill my-red :stroke-weight 2} ))

(def tri (poly 0 0 0.7 3 {:stroke (p-color 240 200 170)}))
(def star (stack tri (groups/rotate maths/PI tri)))
(def emp (groups/empty-group))
(def star-band (grid-layout 7 (cycle [emp emp emp star emp emp emp])))
   
(defn complex-ogee
  ([] (complex-ogee (take 5 (cycle [my-purple my-blue my-green])) ) )
  ([colours] 
     (nested-stack (mod-styles color-to-fill (color-seq colours))
                   (ogee 0.1 3 {:stroke-weight 2})
                   (fn [x] (- x 0.2)))))


(defn square2 [] (stack (group (->SShape {:fill my-cream :stroke my-blue :stroke-weight 3}
                                         [[-1 -1] [-1 1] [1 1] [1 -1] [-1 -1]]))
                        less-complex-diamond simple-clock  ))

(defn dl []
  (let [
        

        

        
        basic (superimpose-layout  (stack
                                    (poly 0 0 0.5 3 {:stroke my-red :stroke-weight 2}) 
                                    (poly 0.3 0.6 0.2 7 {:stroke my-yellow}) )
                                   (clock-rotate
                                    6 (poly (- 0.3) (- 0.5) 0.3 4
                                            {:stroke my-purple :stroke-weight 2 } )  )  )
        
        a-cross ( rotate (- (rand (/ maths/PI 2)) (/ maths/PI 4)) (cross my-green 0 0))
        blue-cross (rotate (- (rand (/ maths/PI 2)) (/ maths/PI 4)) (cross (p-color 100 100 200) 0 0)) 
        clock (clock-rotate 12 (stack 
                                (poly (rand 1) (rand 1)  0.12 4 {:stroke my-yellow :stroke-weight 2 :fill my-green})               
                                (drunk-line 9 0.2 {:stroke my-red :fill my-blue :stroke-weight 3 })))
        flake (spoke-flake-group {:stroke my-orange :stroke-weight 1 })
        face (groups/scale 0.8 (face-group [20 my-burgundy] [5 my-blue] [3 my-purple]  [8 my-red]))
        
      

        half-bird (->SShape {:stroke my-purple :stroke-weight 2} [[0 0] [0.4 (- 0.2)] [0.8 (- 0.3)]])
        bird (groups/group half-bird (h-reflect half-bird ))

        
        test-shape (stack
                    (clock-rotate 3 (groups/group  (->SShape {:stroke my-green :stroke-weight 3} [[0 0] [(-  0.25) (- 1)]]) ))
                    )       

        
        complex-square (nested-stack [{:stroke my-red} {:stroke my-blue} {:stroke my-pink} {:stroke my-cream}]
                                     square (fn [x] (- x 0.2)))




        complex-ogee2 (nested-stack ( color-seq (take 5 (cycle [my-purple my-red my-pink])))
                                    (ogee 0.1 3 {:stroke-weight 2})
                                    (fn [x] (- x 0.2)))

        my-style {:stroke (p-color 0) :stroke-weight 1}

        half (groups/group (->SShape {:fill my-black :stroke my-black} [[-1 -1] [1 1] [1 -1]]) )

        

        
        final-pattern-framed (framed 6 (repeat corner) (repeat edge)
                              (random-grid-layout 4 (repeat pink-tile )))
        
        final-pattern9 (diamond-layout 4 (cycle [ complex-diamond  complex-ogee] ))
        
        
        final-pattern8 (diamond-layout 7 (cycle [ (groups/scale 0.9  (clock-rotate 3  (v-mirror complex-diamond)))
                                                  complex-ogee a-cross flake clock ]) )




        
        final-pattern6 (groups/scale 1  (diamond-layout 4 (cycle [complex-ogee complex-ogee2])))
        
        final-pattern5 (diamond-layout 6 (cycle [ complex-diamond complex-square]))
        
        final-pattern4 (groups/scale 1  (superimpose-layout
                                         (half-drop-grid-layout 7 (repeat square)) 
                                        (half-drop-grid-layout 7
                                                               (random-turn-groups (repeat test-shape) )))
                                    )
        



        
        final-pattern2  (four-round
                         (alt-rows-grid-layout
                          2 (repeat  test-shape)
                          (repeat (checked-layout 3 (cycle [flake
                                                            (polyflower-group 3 5 0.7 {:stroke my-pink}) face ])
                                                  (random-turn-groups (cycle  [(four-mirror  blue-cross) clock] ))
                                                  )))) 
        
        
                  ] ())  )
