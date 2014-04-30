(ns patterning.core
  (:require [quil.core :refer :all])
  (:require [patterning.geometry :refer :all])
  (:require [patterning.view :refer :all])
  (:gen-class))



(defn setup []
  (no-loop)

  (let [
        my-green (color 150 220 150 )
        my-purple (color 150 100 200)
        my-blue (color 160 160 250 )
        my-red (color 200 100 100)
        my-yellow (color 220 200 150)
        my-orange (color 255 128 64)
        my-cyan (color 150 250 250)
        my-cream (color 220 210 180)
        my-pink (color 250 100 180)
        
        square (group  {:style {:colour my-pink :stroke-weight 3} :points [[-1 -1] [-1 1] [1 1] [1 -1] [-1 -1]] } )
        basic (superimpose-layout  (group                  
                                    (weight-sshape 2 (colour-sshape my-red (poly 0 0 0.5 3) ))
                                     (colour-sshape my-yellow (poly 0.3 0.6 0.2 7) ) )
                                     (clock-rotate 6 (group  (add-style {:colour my-purple :stroke-weight 2 } (poly (- 0.3) (- 0.5) 0.3 4) )))
                                    )
        cross ( rotate-group (- (rand (/ PI 2)) (/ PI 4)) ( cross-group my-red 0 0))
        blue-cross (rotate-group (- (rand (/ PI 2)) (/ PI 4)) (cross-group (color 100 100 200) 0 0)) 
        clock (clock-rotate 12 (group  (weight-sshape 2 (colour-sshape my-yellow (poly (rand 1) (rand 1)  0.12 4)))
                                       (weight-sshape 2 (colour-sshape my-green (drunk-line 9 0.2)))))
        flake (spoke-flake-group {:colour my-orange :stroke-weight 1 })
        face (scale-group 0.8 (face-group [20 my-cream] [5 my-blue] [3 my-purple]  [8 my-red]))

        red-ball (group (add-style {:colour my-red :stroke-weight 2} (poly 0 -0.82 0.05 3)))
        simple-clock (clock-rotate 8 (group (add-style {:colour my-pink :stroke-weight 1 } (poly 0.5 0 0.2 8))))
        half-bird (sshape {:colour my-purple :stroke-weight 2} [[0 0] [0.4 (- 0.2)] [0.8 (- 0.3)]])
        bird (group half-bird (h-reflect-sshape half-bird ))
        
        test-shape (group
                   (add-style {:colour my-red  :stroke-weight 2} (poly 0 0 0.8 3))                     
                   (sshape {:colour my-green :stroke-weight 3} [[0 0] [0 (- 1)]])
                   (rotate-sshape (/ PI 2)  (stretch-sshape 1 0.5 (h-sin-sshape {:colour my-pink :stroke-weight 1})))
;;                    (zig-zag-sshape 4 {:colour my-blue} )

;;                     (sshape {:colour (color 100 100 200) :fill my-green} [[-1 -1] [1 -1] [1 1] [-1 -1]] )
;;                     (random-rect {:colour (color 50 100 100)})
;;                    (random-rect {:colour (color 150 100 0) :fill (color 200 220 150 180)})
                     )
        test2 (superimpose-layout square test-shape)

        final-pattern ( half-drop-grid-layout 12
                                              (repeat (superimpose-layout
                                                       (translate-group 0 0.5 simple-clock)
                                                       (checked-layout 3
                                                                        (repeat (clock-rotate 3  (group  (zig-zag-sshape 4 {:colour my-blue}))))
                                                                        (repeat bird)    ))))
        
        final-pattern3 ( superimpose-layout 
                        (checked-layout 4 (cycle [blue-cross (four-round test2)]) (repeat flake))
                        (alt-rows-grid-layout 4 (repeat  (polyflower-group 3 4 0.75 {:colour my-green :fill (color 100 255 100 100)}))
                                              (repeat (add-style {:colour my-red} ( drunk-line 10 0.3)) ) ))
        
        final-pattern2  (four-round  (alt-rows-grid-layout 2 (repeat  test-shape)
                                                          (repeat (checked-layout 3
                                                                                  (cycle [flake
                                                                                          (polyflower-group 3 5 0.7 {:colour my-pink})
                                                                                          face ])
                                                                                  (random-turn-groups (cycle  [(four-mirror  blue-cross) clock] )) )))) 
   
        txpt (make-txpt [-1 -1 1 1] [0 0 (width) (height)])
        ]

        (stroke-weight 1)
        (color 0)
        (no-fill)
        (background 255)
        (draw-group txpt final-pattern)
        (write-svg txpt 800 800 final-pattern)
        (smooth)
        (smooth)) 
  )  

(defn draw [])


(defn -main [& args]
  (sketch
   :title "Patterning"
   :setup setup
   :draw draw
   :size [700 700]
   :on-close #(System/exit 0)))
