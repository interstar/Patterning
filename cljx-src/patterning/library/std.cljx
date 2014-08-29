(ns patterning.library.std
  (:require [patterning.maths :as maths]
            [patterning.sshapes :refer [square]]
            [patterning.groups :refer [group]]
            [patterning.layouts :refer [stack]]))


(defn background [color pattern]
  (stack (group (square {:fill color}))
         pattern))
