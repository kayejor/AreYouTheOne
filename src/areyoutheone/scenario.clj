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

(defn print-formatted-row [row]
  (->> row (map #(if (float? %) (format "%10.1f" %) (format "%10s" %))) 
           (clojure.string/join "|") 
           println))

(defn convert-matrix-to-probs [matrix total]
  (map (fn [row] (map #(float (/ (* % 100) total)) row)) matrix))

(defn print-freq-table [a-vec b-vec rem-poss]
  (let [header-vec (cons "" b-vec)
        freq-matrix (create-freq-matrix rem-poss)
        prob-matrix (convert-matrix-to-probs freq-matrix (count rem-poss))
        prob-matrix-with-labels (map #(cons %2 %1) prob-matrix a-vec)]
    (print-formatted-row header-vec)
    (doseq [row prob-matrix-with-labels] (print-formatted-row row))))

(defn print-beam-freq-table [rem-poss guess]
  (let [freq-array (create-beam-freq-array guess rem-poss)
        prob-array (map #(double (/ (* 100 %) (count rem-poss))) freq-array)]
    (println "Probability distribution of number of beams for this matchup")
    (println (clojure.string/join "|" (map #(format "%5d" %) (range (inc (count guess))))))
    (println (clojure.string/join "|" (map #(format "%5.1f" %) prob-array)))))

(defn convert-tb-names [tb-guess a-vec b-vec]
  (map #(name (nth %2 %1)) tb-guess [a-vec b-vec]))

(defn convert-guess-names [guess a-vec b-vec]
  (map #(vector (name %1) (name (nth b-vec %2))) a-vec guess))

(defn print-best-truth-booth [result-file]
  (let [lines (clojure.string/split (slurp result-file) #"\n")
        [a-vec b-vec name-map] (create-name-associations (first lines) (second lines))
        [tb-results guess-results] (parse-results (nnext lines) name-map)
        rem-poss (filter-poss tb-results guess-results)
        tb-guess (get-smart-tb-guess rem-poss)]
    (print-freq-table a-vec b-vec rem-poss)
    (println (convert-tb-names tb-guess a-vec b-vec))))

(defn print-best-matchup [result-file]
  (let [lines (clojure.string/split (slurp result-file) #"\n")
        [a-vec b-vec name-map] (create-name-associations (first lines) (second lines))
        [tb-results guess-results] (parse-results (nnext lines) name-map)
        rem-poss (filter-poss tb-results guess-results)
        guess (smart-guess rem-poss)]
    (print-beam-freq-table rem-poss guess)
    (println (convert-guess-names guess a-vec b-vec))))
