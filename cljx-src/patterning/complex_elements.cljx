(ns patterning.complex_elements
  (:require [patterning.maths :as maths] 
            [patterning.sshapes :as sshapes]
            [patterning.groups :as groups]
            [patterning.layouts :as layouts]
            [patterning.color :as color]))




;; Complex patterns made as groups (these have several disjoint sshapes)

(defn cross-group "A cross, can only be made as a group (because sshapes are continuous lines) which is why we only define it now"
  [color x y] (groups/group (sshapes/set-color color (sshapes/horizontal-line y)) (sshapes/set-color color (sshapes/vertical-line  x)))  )


(defn ogee-group "An ogee shape" [resolution stretch style]
  (let [o-group (into [] (layouts/four-mirror (groups/group ( sshapes/ogee resolution stretch style))))        
        o0 (get (get o-group 0) :points)
        o1 (get (get o-group 1) :points)
        o2 (get (get o-group 2) :points)
        o3 (get (get o-group 3) :points)
        top (sshapes/tie-together o0 o1)
        bottom (sshapes/tie-together o2 o3) ]
    (groups/group (sshapes/->SShape style ( sshapes/tie-together top bottom))) )  )

(defn spoke-flake-group "The thing from my 'Bouncing' Processing sketch"
  [style]
  (let [outer-radius 0.05
        inner-radius (* 2.01 outer-radius)
        arm-radius (* 4.2 inner-radius)

        inner-circle (sshapes/add-style style (sshapes/poly 0 0 inner-radius 30))
        sp1 [0 inner-radius]
        sp2 [0 (+ inner-radius arm-radius)]
      
        one-spoke (groups/group (sshapes/->SShape style [sp1 sp2 sp1 sp2])
                         (sshapes/add-style style (sshapes/poly 0 (+ outer-radius (last sp2)) outer-radius 25)))
      ]
    (into [] (concat (groups/group (sshapes/add-style style (sshapes/poly 0 0 inner-radius 35)))
                     (layouts/clock-rotate 8 one-spoke)       ) ) ) )


(defn polyflower-group "number of polygons rotated and superimosed"
  ( [sides-per-poly no-polies radius style]
      (layouts/clock-rotate no-polies (groups/group (sshapes/add-style style (sshapes/poly 0 0 radius sides-per-poly)))))
  
  ( [sides-per-poly no-polies radius] (polyflower-group sides-per-poly no-polies radius {})))



(defn face-group "[head, eyes, nose and mouth] each argument is a pair to describe a poly [no-sides color]"
  ( [[ head-sides head-color] [ eye-sides eye-color] [ nose-sides nose-color] [ mouth-sides mouth-color]] 
      (let [left-eye (sshapes/stretch 1.3 1 (sshapes/add-style { :color eye-color } (sshapes/poly -0.3 -0.1 0.1 eye-sides)))
            right-eye (sshapes/h-reflect left-eye)
            ]
                
        (groups/group (sshapes/add-style {:color head-color } (sshapes/poly 0 0 0.8 head-sides))
               (sshapes/stretch 1.3 0.4 (sshapes/add-style {:color mouth-color } (sshapes/poly 0 1.3 0.2 mouth-sides)))
               (sshapes/translate 0 0.1
                                  (sshapes/stretch
                                   0.6 1.1
                                   (sshapes/rotate
                                    (/ maths/PI 2) (sshapes/add-style {:color nose-color } (sshapes/poly 0 0 0.2 nose-sides)))))
               left-eye
               right-eye) ) ) )


(defn petal-group "Using bezier curves" [style dx dy]
  (let [ep [0 0]] [ (sshapes/bez-curve style [ ep [(- dx) (- dy)] [(- (*  -2 dx) dx) (- dy)] ep])]  ))

(defn petal-pair-group "reflected petals" [style dx dy]
  (let [petal (petal-group style dx dy)] (layouts/stack petal (groups/h-reflect petal))))


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


;; Growth transformations (unfinished)

(defn l-norm [p1 p2] (let [[dx dy] (maths/diff [p1 p2])] (maths/unit [(- dy) dx]) ) )
(defn r-norm [p1 p2] (maths/rev (l-norm p1 p2)))

(defn add-dots-to-sshape "Adds dots each side of a line. " [args make-spot dist {:keys [style points]}]  
  (let [segs (maths/line-to-segments points)
        l-dot (fn [[x1 y1] [x2 y2]] (make-spot   ))]
    ()))

