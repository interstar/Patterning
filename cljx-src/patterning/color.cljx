(ns patterning.color  
  (:require [patterning.strings :as strings]))

;; Now we'll use a custom vector as a color
(defn p-color
  ([r g b a] [r g b a])
  ([r g b] (p-color r g b 255))
  ([x] (p-color x x x 255))  )

;; Color helpers
(defn color-seq "handy for creating sequences of color changes"
  [colors] (into [] (map (fn [c] {:stroke c}) colors )))


(defn lightened-color [c]
  (let [light (fn [c] (* 1.2 c))]
    (p-color (light (get c 0))  (light (get c 1))  (light (get c 2))  (get c 3)) ))

(defn color-to-fill [style] (conj  {:fill (get style :stroke)} style))

(defn lighten [style] (conj {:stroke (lightened-color (get style :stroke))} style))

(defn mod-styles [f styles] (into [] (map f styles)))


(defn edge-col [c] (comp #(conj % {:stroke c}) color-to-fill) )

(defn setup-colors [colors c] (map (edge-col c) (color-seq colors)  ))


(defn color-to-web [[r g b a]] (if (= a 255)
                                 (str (strings/gen-format "rgb(%d,%d,%d)" r g b))
                                 (str (strings/gen-format "rgba(%d,%d,%d,%d)" r g b a) )))
