(ns areyoutheone.comps-test
  (:require [clojure.test :refer :all]
            [areyoutheone.comps :refer :all]
            [areyoutheone.possibilities :refer [get-all-possibilities]]))

(deftest get-best-indices-test
  (testing "get-best-indices function"
    (let [freqs [[8 5 4 3] [0 9 7 12] [1 2 90 16] [31 14 15 6]]]
      (is (= (get-best-indices freqs 12) [3 3]))
      (is (= (get-best-indices freqs 100) [3 0]))
      (is (= (get-best-indices freqs 16) [0 0]))
      (is (= (get-best-indices freqs 25) [1 3])))))

(deftest create-freq-matrix-test
  (testing "create-freq-matrix function"
    (let [rem-poss (get-all-possibilities 4)]
      (is (= (create-freq-matrix rem-poss) [[6 6 6 6] [6 6 6 6] [6 6 6 6] [6 6 6 6]]))
      (is (= (create-freq-matrix (next rem-poss)) [[5 6 6 6] [6 5 6 6] [6 6 5 6] [6 6 6 5]]))
      (is (= (create-freq-matrix (take 5 rem-poss)) [[5 0 0 0] [0 2 2 1] [0 2 1 2] [0 1 2 2]])))
    ))

;[0 1 2 3] [0 1 3 2] [0 2 1 3] [0 2 3 1] [0 3 1 2]
(deftest create-beam-freq-array-test
  (testing "create-beam-freq-array function"
    (let [rem-poss (get-all-possibilities 4)
          cand [0 1 2 3]]
      (is (= (create-beam-freq-array cand rem-poss) [9 8 6 0 1]))
      (is (= (create-beam-freq-array cand (take 5 rem-poss)) [0 2 2 0 1])))))
