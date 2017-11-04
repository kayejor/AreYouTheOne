(ns areyoutheone.comps
  (:require [clojure.math.combinatorics :as combo]))

(def default-comps-number 30000000)

(defn get-num-same [a b]
  (count (filter identity (map = a b))))

(defn get-min-val-with-idx [v]
  (apply min-key second (map-indexed vector v)))

(defn get-best-indices [freqs num-poss]
  (let [scores (map (fn [v] (map #(Math/abs (- (quot num-poss 2) %)) v)) freqs)]
    (vec (butlast (flatten (apply min-key #(second (second %)) 
                                  (map-indexed vector (map get-min-val-with-idx scores))))))))

(defn zero-vec [n]
  (vec (repeat n (vec (repeat n 0)))))

(defn create-freq-matrix [rem-poss]
  (reduce 
    (fn [freqs poss] (reduce-kv #(update-in %1 [%2 %3] inc) freqs poss)) 
    (zero-vec (count (first rem-poss))) rem-poss))
 
(defn get-smart-tb-guess [rem-poss]
  "Gets the truth booth guess as a vector [x y]"
  (get-best-indices (create-freq-matrix rem-poss) (count rem-poss)))

(defn create-beam-freq-array [cand rem-poss]
  (reduce
    (fn [freqs poss] (update freqs (get-num-same cand poss) inc))
    (vec (repeat (inc (count cand)) 0))
    rem-poss))

(defn get-score [cand rem-poss]
  (apply max (create-beam-freq-array cand rem-poss)))

(defn smart-guess [rem-poss]
  "Gets the match guess as a vector"
  (let [num-to-calc (inc (quot 30000000 (count rem-poss)))
        candidates (take num-to-calc (shuffle rem-poss))
        cands-with-scores (pmap #(hash-map :guess % :score (get-score % rem-poss)) candidates)]
    (:guess (apply min-key :score cands-with-scores))))
 
