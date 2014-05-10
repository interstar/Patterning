(ns patterning.color
  (:require [quil.core :refer :all])
  (:require [patterning.geometry :refer :all])  )




;; Color helpers
(defn color-seq "handy for creating sequences of color changes"
  [colors] (into [] (map (fn [c] {:color c}) colors )))


(defn lightened-color [c]
  (let [light (fn [c] (* 1.2 c))]
    (color (light (red c)) (light (green c)) (light (blue c))) ))

(defn color-to-fill [style] (conj  {:fill (get style :color)} style))

(defn lighten [style] (conj {:color (lightened-color (get style :color))} style))

(defn mod-styles [f styles] (into [] (map f styles)))


(defn edge-col [c] (comp #(conj % {:color c}) color-to-fill) )
(defn setup-colors [colors c] (map (edge-col c) (color-seq colors)  ))


(defn color-to-web [color]
  (str (format  "#%x%x%x" (int (red color)) (int (green color)) (int  (blue color))) ))
