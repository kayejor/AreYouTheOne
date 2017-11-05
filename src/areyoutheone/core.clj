(ns areyoutheone.core
  (:gen-class)
  (:require [areyoutheone.comps :refer :all]
            [areyoutheone.possibilities :refer :all]
            [areyoutheone.simulation :refer :all]
            [areyoutheone.scenario :refer :all]
            [clojure.java.io :as io]))

(defn contains-val? [coll value]
  (when (seq coll) (or (= value (first coll)) (recur (next coll) value))))

(defn process-input-and-run-simulation [args]
  (let [num-people (Integer/parseInt (first args))
        num-runs (Integer/parseInt (second args))
        random? (contains-val? args "--rand")]
    (run-simulation num-people num-runs random?)))

(defn process-input-and-run-scenario [args]
  (let [filename (first args)
        truth? (contains-val? args "--truth")
        matchup? (contains-val? args "--matchup")]
    (when truth? (println (get-best-truth-booth (io/file filename))))
    (when matchup? (println (get-best-matchup (io/file filename))))))

(defn -main
  [& args]
  (let [run-type (first args)]
    (if (= run-type "-sim") (process-input-and-run-simulation (rest args))
      (if (= run-type "-scenario") (process-input-and-run-scenario (rest args))
        (println "Improper Input"))))
  (shutdown-agents))
