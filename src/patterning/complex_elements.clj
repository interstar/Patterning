(ns patterning.complex_elements
  (:require [quil.core :refer :all])
  (:require [patterning.geometry :refer :all])
  (:require [patterning.layouts :refer :all])
  (:require [patterning.color :refer :all])
  )



;; Complex patterns made as groups (these have several disjoint sshapes)

(defn cross-group "A cross, can only be made as a group (because sshapes are continuous lines) which is why we only define it now"
  [color x y] (group (color-sshape color (horizontal-sshape y)) (color-sshape color (vertical-sshape  x)))  )


(defn ogee-group "An ogee shape" [resolution stretch style]
  (let [o-group (into [] (four-mirror (group ( ogee-sshape resolution stretch style))))        
        o0 (get (get o-group 0) :points)
        o1 (get (get o-group 1) :points)
        o2 (get (get o-group 2) :points)
        o3 (get (get o-group 3) :points)
        top (tie-together o0 o1)
        bottom (tie-together o2 o3) ]
    (group (sshape style ( tie-together top bottom))) )  )

(defn spoke-flake-group "The thing from my 'Bouncing' Processing sketch"
  [style]
  (let [outer-radius 0.05
        inner-radius (* 2.01 outer-radius)
        arm-radius (* 4.2 inner-radius)

        inner-circle (add-style style (poly 0 0 inner-radius 30))
        sp1 [0 inner-radius]
        sp2 [0 (+ inner-radius arm-radius)]
      
        one-spoke (group (sshape style [sp1 sp2 sp1 sp2])
                         (add-style style (poly 0 (+ outer-radius (last sp2)) outer-radius 25)))
      ]
    (into [] (concat (group (add-style style (poly 0 0 inner-radius 35)))
                     (clock-rotate 8 one-spoke)       ) ) ) )


(defn polyflower-group "number of polygons rotated and superimosed"
  ( [sides-per-poly no-polies radius style]
      (clock-rotate no-polies (group (add-style style (poly 0 0 radius sides-per-poly)))))
  
  ( [sides-per-poly no-polies radius] (polyflower-group sides-per-poly no-polies radius {})))



(defn face-group "[head, eyes, nose and mouth] each argument is a pair to describe a poly [no-sides color]"
  ( [[ head-sides head-color] [ eye-sides eye-color] [ nose-sides nose-color] [ mouth-sides mouth-color]] 
      (let [left-eye (stretch-sshape 1.3 1 (add-style { :color eye-color } (poly -0.3 -0.1 0.1 eye-sides)))
            right-eye (h-reflect-sshape left-eye)
            ]
                
        (group (add-style {:color head-color } (poly 0 0 0.8 head-sides))
               (stretch-sshape 1.3 0.4 (add-style {:color mouth-color } (poly 0 1.3 0.2 mouth-sides)))
               (translate-sshape 0 0.1
                                 (stretch-sshape 0.6 1.1 (rotate-sshape (/ PI 2)
                                 (add-style {:color nose-color } (poly 0 0 0.2 nose-sides)))))
               left-eye
               right-eye) ) ) )


(defn petal-group "Using bezier curves" [style dx dy]
  (let [ep [0 0]] [ (bez-curve style [ ep [(- dx) (- dy)] [(- (*  -2 dx) dx) (- dy)] ep])]  ))

(defn petal-pair-group "reflected petals" [style dx dy]
  (let [petal (petal-group style dx dy)] (stack petal (h-reflect-group petal))))


;; L-System for organic shapes

(defn applicable [[from to] c] (= from (str c)))
(defn apply-rule-to-char [rule c] (if (applicable rule c) (get rule 1) c ))

(defn apply-rules-to-char [rules c]
  (let [rule (first (filter #(applicable % c) rules))]
    (if (nil? rule) c (apply-rule-to-char rule c) )  ))

(defn apply-rules [rules string] (apply str (map #(apply-rules-to-char rules %) string) ))

(defn multi-apply-rules [steps rules string]
  (last (take (+ 1 steps) (iterate #(apply-rules rules %) string))))

(defn l-system [rules] #(multi-apply-rules %1 rules %2))

(defn l-string-turtle-to-group-r "A more sophisticated turtle that renders l-system string but has a stack and returns a group"
  [[ox oy] d angle da string]
  (let [for-x (fn [x a] (+ x (* d (Math/cos a))) )
        for-y (fn [y a] (+ y (* d (Math/sin a))) )
        ]
    
    (loop [x ox y oy a angle s string points [] acc [] ]
      (if (empty? s) [s (into [] (concat acc [(sshape {} (conj points [x y]))]))]          
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
            \[ (let [[cont sub-groups] (l-string-turtle-to-group-r [x y] d a da (rest s) ) ]
                 (recur x y a cont points (concat acc sub-groups)))
            \] [(rest s) (into [] (concat [(sshape {} (conj points [x y]))] acc ))]
            
            ;; catch 
            (recur x y a (rest s) points acc))
          ) ) ))

(defn l-string-to-group "turns a string from the l-system into a number of lines"
  ([start-pos d init-angle d-angle string style]
     (let [res (l-string-turtle-to-group-r start-pos d init-angle d-angle string)]
       (over-style-group style (second res))))
  ([start-pos d init-angle d-angle string] (l-string-to-group start-pos d init-angle d-angle string {}))   )


;; Growth transformations (unfinished)

(defn l-norm [p1 p2] (let [[dx dy] (diff [p1 p2])] (unit [(- dy) dx]) ) )
(defn r-norm [p1 p2] (rev (l-norm p1 p2)))

(defn add-dots-to-sshape "Adds dots each side of a line. " [args make-spot dist {:keys [style points]}]  
  (let [segs (line-to-segments points)
        l-dot (fn [[x1 y1] [x2 y2]] (make-spot   ))]
    ()))

