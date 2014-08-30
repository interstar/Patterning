(ns patterning.examples.attic
  (:require [quil.core :refer :all])
  (:require [patterning.geometry :refer :all])
  (:require [patterning.layouts :refer :all])
  (:require [patterning.complex_elements :refer :all])
  (:require [patterning.view :refer :all])
  (:require [patterning.color :refer :all])  
  )

(defn )

(defn dl []
  (let [
        
        ;; ELEMENTS I'M USING TO MAKE PATTERNS. IF YOU WANT TO START
        ;; WITH YOUR OWN "DESIGN LANGUAGE" YOU CAN KILL ALL THESE UP
        ;; TO THE "final-pattern" DEFINITION

        
        square (group  {:style {:fill my-burgundy :stroke my-cream :stroke-weight 3} :points [[-1 -1] [-1 1] [1 1] [1 -1] [-1 -1]] } )
        
        basic (superimpose-layout  (group                  
                                    (weight-sshape 2 (color-sshape my-red (poly 0 0 0.5 3) ))
                                     (color-sshape my-yellow (poly 0.3 0.6 0.2 7) ) )
                                     (clock-rotate 6 (group  (add-style {:stroke my-purple :stroke-weight 2 } (poly (- 0.3) (- 0.5) 0.3 4) )))
                                    )
        cross ( rotate-group (- (rand (/ PI 2)) (/ PI 4)) ( cross-group my-green 0 0))
        blue-cross (rotate-group (- (rand (/ PI 2)) (/ PI 4)) (cross-group (color 100 100 200) 0 0)) 
        clock (clock-rotate 12 (group  (add-style {:stroke my-yellow :stroke-weight 2 :fill my-green} (poly (rand 1) (rand 1)  0.12 4))
                                       (add-style {:stroke my-red :fill my-blue :stroke-weight 3 } (drunk-line 9 0.2))))
        flake (spoke-flake-group {:stroke my-orange :stroke-weight 1 })
        face (scale-group 0.8 (face-group [20 my-burgundy] [5 my-blue] [3 my-purple]  [8 my-red]))
        
        red-ball (group (add-style {:stroke my-red :stroke-weight 2} (poly 0 -0.82 0.05 3)))

        half-bird (sshape {:stroke my-purple :stroke-weight 2} [[0 0] [0.4 (- 0.2)] [0.8 (- 0.3)]])
        bird (group half-bird (h-reflect-sshape half-bird ))

        
        test-shape (stack
                    (group
                     ;(add-style {:stroke my-cream :fill my-burgundy :stroke-weight 3} (poly 0 0 0.8 3))  
                           (zig-zag-sshape 4 {:stroke my-blue :stroke-weight 3} )                   
                           ;;                   (rotate-sshape (/ PI 2)  (stretch-sshape 1 0.5 (h-sin-sshape {:stroke my-pink :stroke-weight 1})))
                           ;;                     (sshape {:stroke (color 100 100 200) :fill my-green} [[-1 -1] [1 -1] [1 1] [-1 -1]] )
                           ;;                     (random-rect {:stroke (color 50 100 100)})
                           ;;                    (random-rect {:stroke (color 150 100 0) :fill (color 200 220 150 180)})
                           )
                    (clock-rotate 3 (group  (sshape {:stroke my-green :stroke-weight 3} [[0 0] [(-  0.25) (- 1)]]) ))
                    )
        test2 (superimpose-layout square test-shape)

        

        
        complex-diamond (nested-stack (setup-colors  [my-green my-pink my-cream] (color 0))
                                      simple-diamond (fn [x] (- x 0.25)) )
        

        
        complex-square (nested-stack [{:stroke my-red} {:stroke my-blue} {:stroke my-pink} {:stroke my-cream}]
                                     square (fn [x] (- x 0.2)))

        complex-ogee (nested-stack (mod-styles color-to-fill (color-seq (take 5 (cycle [my-purple my-blue my-green]))))
                                   (ogee-group 0.1 3 {:stroke-weight 2})
                                   (fn [x] (- x 0.2)))


        complex-ogee2 (nested-stack ( color-seq (take 5 (cycle [my-purple my-red my-pink])))
                                    (ogee-group 0.1 3 {:stroke-weight 2})
                                    (fn [x] (- x 0.2)))

        my-style {:stroke (color 0) :stroke-weight 1}

        half (group (sshape {:fill my-black :stroke my-black} [[-1 -1] [1 1] [1 -1]]) )

        pink-tile (stack complex-diamond (group ( sshape {:stroke my-blue :stroke-weight 5} [[0 0] [0 1] ])))
        
        edge (rotate-group (half-PI) (stretch-group 0.7 1 pink-tile))

        corner (rotate-group (q-PI) edge)

 
        
 

        system-1 (l-system [["F" "F+G++G-F--FF-G+"]
                            ["G" "-F+GG++G+F--F-G"]])


        
        sys-g1 (basic-turtle [0 0] 0.1 0 (/ PI 3) (system-1 3 "F") {} {:stroke my-purple  :stroke-weight 2} )


        inner (stack (reframe-group sys-g1)
                     (alt-rows-grid-layout 4 (repeat (checked-layout 2 (repeat nothing-like-fucking-bez) (repeat [])))
                                           (cycle [sprey sprey (h-reflect-group sprey) (h-reflect-group sprey)])))
        

        
        ;; THESE ARE SOME OLDER PATTERNS, REPLACE THE FINAL PATTERN
        ;; DEFINITION WITH ONE OF THESE TO SEE IT
        
        final-pattern-final-paper (framed 9 (repeat (scale-group 0.75 ( rotate-group (/ PI 4) square2)))
                              (cycle [square2 (scale-group 0.8 square2)]) inner)

        
        final-pattern-framed-plant  (framed 9 (repeat nothing-like-fucking-bez) (repeat square2) sprey )
        
        final-pattern-trees
          (framed 6 (repeat corner) (repeat sprey) (checked-layout 4 (repeat pink-tile) (repeat nothing-like-fucking-bez )) ) 
        
        final-pattern-framed (framed 6 (repeat corner) (repeat edge)
                              (random-grid-layout 4 (repeat pink-tile )))
        
        final-pattern9 (diamond-layout 4 (cycle [ complex-diamond  complex-ogee] ))
        
        
        final-pattern8 (diamond-layout 7 (cycle [ (scale-group 0.9  (clock-rotate 3  (v-mirror complex-diamond)))
                                                  complex-ogee cross flake clock ]) )



        final-pattern7 (checked-layout 4
                                       (repeat (stack  (clock-rotate 3 test2) (diamond-layout 4 (repeat complex-diamond))) )
                                       (repeat (scale-group 0.8 (four-round (checked-layout 2  (repeat complex-ogee2) (repeat complex-ogee))))))
        
        final-pattern6 (scale-group 1  (diamond-layout 4 (cycle [complex-ogee complex-ogee2])))
        
        final-pattern5 (diamond-layout 6 (cycle [ complex-diamond complex-square]))
        
        final-pattern4 (scale-group 1  (superimpose-layout
                                        (half-drop-grid-layout 7 (repeat square)) 
                                        (half-drop-grid-layout 7
                                                               (random-turn-groups (repeat test-shape) )))
                                    )
        
        final-pattern3 ( superimpose-layout 
                         (checked-layout 4 (cycle [blue-cross (four-round test2)]) (repeat flake))
                         (alt-rows-grid-layout 4 (repeat  (polyflower-group 3 4 0.75 {:stroke my-green :fill (color 100 255 100 100)}))
                                               (repeat (add-style {:stroke my-red} ( drunk-line 10 0.3)) ) ))


        
        final-pattern2  (four-round
                         (alt-rows-grid-layout
                          2 (repeat  test-shape)
                          (repeat (checked-layout 3 (cycle [flake
                                                            (polyflower-group 3 5 0.7 {:stroke my-pink}) face ])
                                                  (random-turn-groups (cycle  [(four-mirror  blue-cross) clock] ))
                                                  )))) 
        
        
                  ]))
