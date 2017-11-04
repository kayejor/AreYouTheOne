(ns areyoutheone.scenario
  (:require [areyoutheone.comps :refer :all]
            [areyoutheone.possibilities :refer :all]))

(defn create-name-associations [a-names b-names]
  (let [a-vec (map keyword (clojure.string/split a-names #","))
        b-vec (map keyword (clojure.string/split b-names #","))
        name-map (apply merge (map #(hash-map %2 %1 %3 %1) (range (count a-vec)) a-vec b-vec))]
    [a-vec b-vec name-map]))

(defn create-guess [matchups name-map]
  (map second 
       (sort-by first 
                (map 
                  #(map (fn [person] (name-map (keyword person))) (clojure.string/split % #":")) 
                  matchups))))

(defn tb-hash-map [info name-map]
  ;info comes in the form [nameA nameB true/false]
  {:matchup (map #(-> % keyword name-map) (butlast info))
   :correct? (= (last info) "true")})

(defn guess-hash-map [info name-map]
  ;info comes in the form [name1A:name1B name2A:name2B ... num-beams] all as strings
  {:guess (create-guess (butlast info) name-map) :beams (Integer/parseInt (last info))})

(defn parse-results [results name-map]
  (loop [tb-results '() guess-results '() rem-res results]
    (if (empty? rem-res)
      [tb-results guess-results]
      (let [cur (first rem-res)
            split (clojure.string/split cur #",")]
        (if (= (count split) 3)
          (recur (conj tb-results (tb-hash-map split name-map)) guess-results (rest rem-res))
          (recur tb-results (conj guess-results (guess-hash-map split name-map)) (rest rem-res)))))))

(defn filter-poss [tb-results guess-results]
  (let [num-people (-> guess-results first :guess count)
        rem-poss (get-all-possibilities num-people)
        rem-poss (reduce #(apply-tb-result %1 (:matchup %2) (:correct? %2)) rem-poss tb-results)
        rem-poss (reduce #(apply-guess-result %1 (:guess %2) (:beams %2)) rem-poss guess-results)]
    rem-poss))

(defn get-best-tb [tb-results guess-results]
  (let [rem-poss (filter-poss tb-results guess-results)]
    (get-smart-tb-guess rem-poss)))

(defn get-best-guess [tb-results guess-results]
  (let [rem-poss (filter-poss tb-results guess-results)]
    (smart-guess rem-poss)))

(defn convert-tb-names [tb-guess a-vec b-vec]
  (map #(name (nth %2 %1)) tb-guess [a-vec b-vec]))

(defn convert-guess-names [guess a-vec b-vec]
  (map #(vector %1 (nth b-vec %2)) a-vec guess))

(defn get-best-truth-booth [result-file]
  (let [lines (clojure.string/split (slurp result-file) #"\n")
        [a-vec b-vec name-map] (create-name-associations (first lines) (second lines))
        [tb-results guess-results] (parse-results (nnext lines) name-map)
        tb-guess (get-best-tb tb-results guess-results)]
    (convert-tb-names tb-guess a-vec b-vec)))

(defn get-best-matchup [result-file]
  (let [lines (clojure.string/split (slurp result-file) #"\n")
        [a-vec b-vec name-map] (create-name-associations (first lines) (second lines))
        [tb-results guess-results] (parse-results (nnext lines) name-map)
        guess (get-best-guess tb-results guess-results)]
    (convert-guess-names guess a-vec b-vec)))
