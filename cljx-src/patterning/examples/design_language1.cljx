(ns patterning.examples.design_language1
  (:require [patterning.maths :as maths] 
            [patterning.sshapes :refer [add-style poly ->SShape square diamond set-weight set-color drunk-line h-reflect]]  
            [patterning.groups :refer [group rotate stretch scale] ]
            [patterning.groups :as groups]
            [patterning.layouts :refer [clock-rotate nested-stack stack diamond-layout v-mirror random-turn-groups
                                        superimpose-layout half-drop-grid-layout framed random-grid-layout four-round
                                        alt-rows-grid-layout checked-layout four-mirror ]]
            [patterning.complex_elements :refer [petal-pair-group cross-group spoke-flake-group face-group ogee-group polyflower-group]]

            [patterning.color :refer [p-color setup-colors mod-styles color-to-fill color-seq]])  
  )


;; The palette
(def x 4)
(def my-green (p-color 20 150 20 ))
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


(def simple-clock (clock-rotate 8 (group (add-style {:color my-orange :fill my-cream :stroke-weight 1 } (poly 0.5 0 0.2 8)))))
        
(def simple-diamond (group (diamond {:color my-red :stroke-weight 2})))

(def flower-style {:color my-orange :stroke-weight 3  :bezier true})        
(def flower (clock-rotate 5 (stack (petal-pair-group flower-style 0.5 0.7) )))

(def less-complex-diamond (nested-stack
                           (setup-colors [my-blue my-pink ] (p-color 0))
                           simple-diamond (fn [x] (- x 0.2)) ))





        
(def complex-diamond (nested-stack (setup-colors  [my-green my-pink my-cream] (p-color 0))
                                          simple-diamond (fn [x] (- x 0.25)) ))
 
(def pink-tile (stack complex-diamond (group (->SShape {:color my-blue :stroke-weight 5} [[0 0] [0 1] ]))))
        
(def edge (rotate maths/half-PI (stretch 0.7 1 pink-tile)))

(def corner (rotate maths/q-PI edge))


(defn dl []
  (let [
        
        ;; ELEMENTS I'M USING TO MAKE PATTERNS. IF YOU WANT TO START
        ;; WITH YOUR OWN "DESIGN LANGUAGE" YOU CAN KILL ALL THESE UP
        ;; TO THE "final-pattern" DEFINITION

        

        
        basic (superimpose-layout  (group                  
                                    (set-weight 2 (set-color my-red (poly 0 0 0.5 3) ))
                                     (set-color my-yellow (poly 0.3 0.6 0.2 7) ) )
                                   (clock-rotate 6
                                                 (group (add-style
                                                         {:color my-purple :stroke-weight 2 } (poly (- 0.3) (- 0.5) 0.3 4) )))
                                    )
        cross ( rotate (- (rand (/ maths/PI 2)) (/ maths/PI 4)) (cross-group my-green 0 0))
        blue-cross (rotate (- (rand (/ maths/PI 2)) (/ maths/PI 4)) (cross-group (p-color 100 100 200) 0 0)) 
        clock (clock-rotate 12 (group
                                (add-style {:color my-yellow :stroke-weight 2 :fill my-green}
                                                   (poly (rand 1) (rand 1)  0.12 4))
                                       (add-style {:color my-red :fill my-blue :stroke-weight 3 } (drunk-line 9 0.2))))
        flake (spoke-flake-group {:color my-orange :stroke-weight 1 })
        face (groups/scale 0.8 (face-group [20 my-burgundy] [5 my-blue] [3 my-purple]  [8 my-red]))
        
        red-ball (groups/group (add-style {:color my-red :stroke-weight 2} (poly 0 -0.82 0.05 3)))

        half-bird (->SShape {:color my-purple :stroke-weight 2} [[0 0] [0.4 (- 0.2)] [0.8 (- 0.3)]])
        bird (groups/group half-bird (h-reflect half-bird ))

        
        test-shape (stack
                    (clock-rotate 3 (groups/group  (->SShape {:color my-green :stroke-weight 3} [[0 0] [(-  0.25) (- 1)]]) ))
                    )       

        
        complex-square (nested-stack [{:color my-red} {:color my-blue} {:color my-pink} {:color my-cream}]
                                     (groups/group square) (fn [x] (- x 0.2)))

        complex-ogee (nested-stack (mod-styles color-to-fill (color-seq (take 5 (cycle [my-purple my-blue my-green]))))
                                   (ogee-group 0.1 3 {:stroke-weight 2})
                                   (fn [x] (- x 0.2)))


        complex-ogee2 (nested-stack ( color-seq (take 5 (cycle [my-purple my-red my-pink])))
                                    (ogee-group 0.1 3 {:stroke-weight 2})
                                    (fn [x] (- x 0.2)))

        my-style {:color (p-color 0) :stroke-weight 1}

        half (groups/group (->SShape {:fill my-black :color my-black} [[-1 -1] [1 1] [1 -1]]) )

        

        
        final-pattern-framed (framed 6 (repeat corner) (repeat edge)
                              (random-grid-layout 4 (repeat pink-tile )))
        
        final-pattern9 (diamond-layout 4 (cycle [ complex-diamond  complex-ogee] ))
        
        
        final-pattern8 (diamond-layout 7 (cycle [ (groups/scale 0.9  (clock-rotate 3  (v-mirror complex-diamond)))
                                                  complex-ogee cross flake clock ]) )




        
        final-pattern6 (groups/scale 1  (diamond-layout 4 (cycle [complex-ogee complex-ogee2])))
        
        final-pattern5 (diamond-layout 6 (cycle [ complex-diamond complex-square]))
        
        final-pattern4 (groups/scale 1  (superimpose-layout
                                        (half-drop-grid-layout 7 (repeat (groups/group square))) 
                                        (half-drop-grid-layout 7
                                                               (random-turn-groups (repeat test-shape) )))
                                    )
        



        
        final-pattern2  (four-round
                         (alt-rows-grid-layout
                          2 (repeat  test-shape)
                          (repeat (checked-layout 3 (cycle [flake
                                                            (polyflower-group 3 5 0.7 {:color my-pink}) face ])
                                                  (random-turn-groups (cycle  [(four-mirror  blue-cross) clock] ))
                                                  )))) 
        
        
                  ] ())  )