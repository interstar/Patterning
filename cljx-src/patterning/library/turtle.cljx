(ns patterning.library.turtle
  (:require [patterning.maths :as maths] 
            [patterning.sshapes :as sshapes]
            [patterning.groups :as groups] ))

;; Turtle
(defn l-string-turtle-to-group-r "A more sophisticated turtle that renders l-system string but has a stack and returns a group"

  ([[ox oy] d angle da string leaf-map style]
      (let [for-x (fn [x a] (+ x (* d (Math/cos a))) )
            for-y (fn [y a] (+ y (* d (Math/sin a))) ) ]
        
        (loop [x ox y oy a angle s string points [] acc [] ]
          (if (empty? s) [s (into [] (concat acc [(sshapes/->SShape style (conj points [x y]))]))]

              ;; We check first for custom leaves. If (first s)
              ;; maps to a custom leaf function we call that and add
              ;; to our accumulator before recursing
              
              (if (contains? leaf-map (first s))
                (let [leaf ((get leaf-map (first s)) x y a)]
                  (recur x y a (rest s) points (concat acc leaf) ) )

                ;; otherwise we have standard interpretations for
                ;; the (first s) character
                (case (first s)
                    ;; note we give ourselves 5 drawable edges here, 
                    \F (recur (for-x x a) (for-y y a) a (rest s) (conj points [x y]) acc)
                    \G (recur (for-x x a) (for-y y a) a (rest s) (conj points [x y]) acc)
                    \H (recur (for-x x a) (for-y y a) a (rest s) (conj points [x y]) acc)
                    \I (recur (for-x x a) (for-y y a) a (rest s) (conj points [x y]) acc)
                    \J (recur (for-x x a) (for-y y a) a (rest s) (conj points [x y]) acc)            

                    ;; our two turning options
                    \+ (recur x y (+ a da) (rest s) points acc)
                    \- (recur x y (- a da) (rest s) points acc)

                    ;; recursion
                    \[ (let [[cont sub-groups] (l-string-turtle-to-group-r [x y] d a da (rest s) leaf-map style ) ]
                         (recur x y a cont points (concat acc sub-groups)))
                    \] [(rest s) (into [] (concat [(sshapes/->SShape style (conj points [x y]))] acc ))]
                    
                    ;; catch 
                    (recur x y a (rest s) points acc)) )
              ) ) ))   )

(defn basic-turtle "turns a string from the l-system into a number of lines"
  ([start-pos d init-angle d-angle string leaf-map style]
     (let [res (l-string-turtle-to-group-r start-pos d init-angle d-angle string leaf-map style)]
       (second res)))  )


