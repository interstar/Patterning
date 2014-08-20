(ns patterning.library.l_systems
  (:require [patterning.maths :as maths] ))

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
