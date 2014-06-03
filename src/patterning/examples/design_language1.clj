(ns patterning.examples.design_language1
  (:require [patterning.maths :as maths])
  (:require [patterning.sshapes :as sshapes])  
  (:require [patterning.groups :refer :all])
  (:require [patterning.layouts :refer :all])
  (:require [patterning.complex_elements :refer :all])
  (:require [patterning.view :refer :all])
  (:require [patterning.color :refer :all])  
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


(def simple-clock (clock-rotate 8 (group (sshapes/add-style {:color my-orange :fill my-cream :stroke-weight 1 } (sshapes/poly 0.5 0 0.2 8)))))
        
(def simple-diamond (group  (sshapes/diamond {:color my-red :stroke-weight 2})))

(def flower-style {:color my-yellow :stroke-weight 3 :fill my-orange :bezier true})        
(def flower (clock-rotate 5 (stack (petal-pair-group flower-style 0.5 0.7) )))

(def less-complex-diamond (nested-stack
                           (setup-colors [my-blue my-pink ] (p-color 0))
                           simple-diamond (fn [x] (- x 0.2)) ))





        
(def complex-diamond (nested-stack (setup-colors  [my-green my-pink my-cream] (p-color 0))
                                          simple-diamond (fn [x] (- x 0.25)) ))
 
(def pink-tile (stack complex-diamond (group ( sshapes/make {:color my-blue :stroke-weight 5} [[0 0] [0 1] ]))))
        
(def edge (rotate-group maths/half-PI (stretch-group 0.7 1 pink-tile)))

(def corner (rotate-group maths/q-PI edge))


(defn dl []
  (let [
        
        ;; ELEMENTS I'M USING TO MAKE PATTERNS. IF YOU WANT TO START
        ;; WITH YOUR OWN "DESIGN LANGUAGE" YOU CAN KILL ALL THESE UP
        ;; TO THE "final-pattern" DEFINITION

        

        
        basic (superimpose-layout  (group                  
                                    (sshapes/weight-it 2 (sshapes/color-it my-red (sshapes/poly 0 0 0.5 3) ))
                                     (sshapes/color-it my-yellow (sshapes/poly 0.3 0.6 0.2 7) ) )
                                   (clock-rotate 6
                                                 (group (sshapes/add-style
                                                         {:color my-purple :stroke-weight 2 } (sshapes/poly (- 0.3) (- 0.5) 0.3 4) )))
                                    )
        cross ( rotate-group (- (rand (/ maths/PI 2)) (/ maths/PI 4)) ( cross-group my-green 0 0))
        blue-cross (rotate-group (- (rand (/ maths/PI 2)) (/ maths/PI 4)) (cross-group (p-color 100 100 200) 0 0)) 
        clock (clock-rotate 12 (group
                                (sshapes/add-style {:color my-yellow :stroke-weight 2 :fill my-green}
                                                   (sshapes/poly (rand 1) (rand 1)  0.12 4))
                                       (sshapes/add-style {:color my-red :fill my-blue :stroke-weight 3 } (sshapes/drunk-line 9 0.2))))
        flake (spoke-flake-group {:color my-orange :stroke-weight 1 })
        face (scale-group 0.8 (face-group [20 my-burgundy] [5 my-blue] [3 my-purple]  [8 my-red]))
        
        red-ball (group (sshapes/add-style {:color my-red :stroke-weight 2} (sshapes/poly 0 -0.82 0.05 3)))

        half-bird (sshapes/make {:color my-purple :stroke-weight 2} [[0 0] [0.4 (- 0.2)] [0.8 (- 0.3)]])
        bird (group half-bird (sshapes/h-reflect half-bird ))

        
        test-shape (stack
                    (group
                     ;(sshapes/add-style {:color my-cream :fill my-burgundy :stroke-weight 3} (sshapes/poly 0 0 0.8 3))  
                           (sshapes/zig-zag 4 {:color my-blue :stroke-weight 3} )                   
                           ;;       (sshapes/rotate (/ PI 2)  (stretch-sshape 1 0.5 (h-sin-sshape {:color my-pink :stroke-weight 1})))
                           ;;       (sshapes/make {:color (color 100 100 200) :fill my-green} [[-1 -1] [1 -1] [1 1] [-1 -1]] )
                           ;;                     (random-rect {:color (color 50 100 100)})
                           ;;                    (random-rect {:color (color 150 100 0) :fill (color 200 220 150 180)})
                           )
                    (clock-rotate 3 (group  (sshapes/make {:color my-green :stroke-weight 3} [[0 0] [(-  0.25) (- 1)]]) ))
                    )       

        
        complex-square (nested-stack [{:color my-red} {:color my-blue} {:color my-pink} {:color my-cream}]
                                     (group sshapes/square) (fn [x] (- x 0.2)))

        complex-ogee (nested-stack (mod-styles color-to-fill (color-seq (take 5 (cycle [my-purple my-blue my-green]))))
                                   (ogee-group 0.1 3 {:stroke-weight 2})
                                   (fn [x] (- x 0.2)))


        complex-ogee2 (nested-stack ( color-seq (take 5 (cycle [my-purple my-red my-pink])))
                                    (ogee-group 0.1 3 {:stroke-weight 2})
                                    (fn [x] (- x 0.2)))

        my-style {:color (p-color 0) :stroke-weight 1}

        half (group (sshapes/make {:fill my-black :color my-black} [[-1 -1] [1 1] [1 -1]]) )

        

        
        final-pattern-framed (framed 6 (repeat corner) (repeat edge)
                              (random-grid-layout 4 (repeat pink-tile )))
        
        final-pattern9 (diamond-layout 4 (cycle [ complex-diamond  complex-ogee] ))
        
        
        final-pattern8 (diamond-layout 7 (cycle [ (scale-group 0.9  (clock-rotate 3  (v-mirror complex-diamond)))
                                                  complex-ogee cross flake clock ]) )




        
        final-pattern6 (scale-group 1  (diamond-layout 4 (cycle [complex-ogee complex-ogee2])))
        
        final-pattern5 (diamond-layout 6 (cycle [ complex-diamond complex-square]))
        
        final-pattern4 (scale-group 1  (superimpose-layout
                                        (half-drop-grid-layout 7 (repeat (group sshapes/square))) 
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
