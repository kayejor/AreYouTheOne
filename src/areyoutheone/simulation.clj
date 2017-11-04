(ns areyoutheone.simulation
  (:require [areyoutheone.comps :refer :all]
            [areyoutheone.possibilities :refer :all]))

(def num-rand-rounds 1)

(defn get-rand-tb [num-people]
  [(rand-int num-people) (rand-int num-people)])

(defn apply-tb [rem-poss [guess-i guess-j] solution]
  (apply-tb-result rem-poss [guess-i guess-j] (= guess-j (nth solution guess-i))))

(defn get-rand-guess [num-people]
  (vec (shuffle (range num-people))))

(defn apply-guess [rem-poss guess solution]
  (let [beams (get-num-same guess solution)]
    (apply-guess-result rem-poss guess beams)))

(defn win-game [round]
  (println "Game won in" (inc round) "rounds")
  (inc round))

(defn run-game [solution]
  (let [num-people (count solution)]
    (loop [round 0 rem-poss (get-all-possibilities (count solution))]
      (let [rand-round? (< round num-rand-rounds)]
        (when (= round num-rand-rounds) (println "Start strat, remaining poss:" (count rem-poss)))
        (println "Round:" (inc round))
        (let [tb-guess (if rand-round? (get-rand-tb num-people) (get-smart-tb-guess rem-poss))
              rem-poss (apply-tb rem-poss tb-guess solution)]
          (println "Truth booth guess:" tb-guess)
          (println (if (= (peek tb-guess) (nth solution (first tb-guess))) "Correct!" "Wrong"))
          (when (not rand-round?) (println "Remaining poss:" (count rem-poss)))
          (let [guess (if rand-round? (get-rand-guess num-people) (smart-guess rem-poss))
                beams (get-num-same guess solution)
                rem-poss (apply-guess rem-poss guess solution)]
            (println "Guess" guess)
            (println "Beams" beams)
            (when (not rand-round?) (println "Remaining poss:" (count rem-poss)))
            (if (= beams (count solution))
              (win-game round)
              (recur (inc round) rem-poss))))))))

(defn run-simulation [num-people num-runs random-solution?]
  (loop [i 0 scores []]
    (if (= i num-runs)
      (println "Scores:" scores "Won games:" (count (filter #(<= % 10) scores)))
      (let [nums (range num-people)
            solution (if random-solution? (vec (shuffle nums)) (vec nums))
            num-turns (run-game solution)]
        (recur (inc i) (conj scores num-turns))))))
