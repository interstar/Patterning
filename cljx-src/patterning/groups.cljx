(ns patterning.groups
  (:require [patterning.maths :as maths] 
            [patterning.sshapes :as sshapes]))
 

;; Groups
;; A Group is a vector of sshapes. All patterns are basically groups.
;; Groups can represent ordinary patterns that require several sshapes
;; (because they have disjoint geometric forms, or multiple colors
;; etc.
;; Groups are also the flattened results of combining multiple groups
;; together, eg. when running them through a layout. 


;;; Making groups
(defn group "a vector of sshapes" [& sshapes] (into [] sshapes) )


(defn empty-group [] [(sshapes/empty-sshape)])


;;; Simple transforms
(defn scale ([val group] (into [] (map (partial sshapes/scale val) group )))   )

(defn translate  [dx dy group] (into [] (map (partial sshapes/translate dx dy) group))  )

(defn translate-to [x y group] (translate (- x) (- y) group) )  

(defn h-reflect [group] (into [] (map sshapes/h-reflect group) ) )
(defn v-reflect [group] (into [] (map sshapes/v-reflect group) ) )

(defn stretch [sx sy group] (into [] (map (partial sshapes/stretch sx sy) group)))
(defn rotate [da group] (into [] (map (partial sshapes/rotate da) group)))

(defn wobble [noise group] (into [] (map (partial sshapes/wobble noise) group)))

(defn over-style "Changes the style of a group" [style group]
  (into [] (map (partial sshapes/add-style style) group)))

(defn extract-points [{:keys [style points]}] points)

(defn flatten-group "Flatten all sshapes into a single sshape"
  ([group] (flatten-group {} group))
  ([style group]
     (let [all-points (mapcat extract-points group) ]
       (sshapes/->SShape style all-points) )  ) )

(defn reframe-scaler "Takes a sshape and returns a scaler to reduce it to usual viewport coords [-1 -1][1 1] "
  [sshape] (/ 2.0 (max (sshapes/width sshape) (sshapes/height sshape))))

(defn reframe [group]
  (let [sshape (flatten-group {} group)
        scaled (scale (reframe-scaler sshape) group)
        f-scaled (flatten-group {} scaled)
        dx (- (- 1) (sshapes/leftmost f-scaled))
        dy (- (- 1) (sshapes/top f-scaled))
        ]
    (let []
      (translate dx dy scaled))))

(defn filter-group [p? group] (map (partial sshapes/ss-filter p?) group))

(defn filter-sshapes-in-group "this removes entire sshapes from the group that have points that don't match the criteria"
  [p? group]
  (let [all-ok? (fn [{:keys [style points]}] (every? p? points) )]
    (filter all-ok? group) ) )


(defn clip-sshape "takes a predicate and a sshape, splits the sshape at any point which doesn't meet the predicate, return group"
  [p? {:keys [style points]}]
  (let []
    (if (empty? points) {:style style :points points}
        (loop [p (first points) ps (rest points) acc-sshape-ps [] acc-group []]
          (cond
           (and (empty? ps) (not (p? p))) (conj acc-group {:style style :points acc-sshape-ps})           
           (empty? ps) (conj acc-group {:style style :points (conj acc-sshape-ps p)})
           (p? p) (recur (first ps) (rest ps) (conj acc-sshape-ps p) acc-group)
           :else (recur (first ps) (rest ps) [] (conj acc-group {:style style :points acc-sshape-ps}))
           )          
          )
        )
    ))

(defn clip "clips all sshapes in a group"
  [p? group] (mapcat (partial clip-sshape p?) group) )


