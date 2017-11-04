(ns areyoutheone.possibilities
  (:require [clojure.math.combinatorics :as combo]
            [areyoutheone.comps :refer [get-num-same]]))

(defn get-all-possibilities [num-people]
  (combo/permutations (range num-people)))

(defn apply-tb-result [rem-poss [guess-i guess-j] correct?]
  (filter #(= correct? (= (nth % guess-i) guess-j)) rem-poss))

(defn apply-guess-result [rem-poss guess beams]
  (filter #(= beams (get-num-same % guess)) rem-poss))
