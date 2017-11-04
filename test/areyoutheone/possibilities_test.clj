(ns areyoutheone.possibilities-test
  (:require [clojure.test :refer :all]
            [areyoutheone.possibilities :refer :all]))

(deftest apply-tb-result-test
  (testing "apply-tb-result function"
    (let [rem-poss (get-all-possibilities 4)]
      (is (= (apply-tb-result rem-poss [0 0] true)
             (sort '([0 1 2 3] [0 1 3 2] [0 2 1 3] [0 2 3 1] [0 3 1 2] [0 3 2 1]))))
      (is (= (apply-tb-result rem-poss [0 0] false)
             (sort '([1 0 2 3] [1 0 3 2] [1 2 0 3] [1 2 3 0] [1 3 0 2] [1 3 2 0]
                     [2 0 1 3] [2 0 3 1] [2 1 0 3] [2 1 3 0] [2 3 0 1] [2 3 1 0]
                     [3 0 1 2] [3 0 2 1] [3 1 0 2] [3 1 2 0] [3 2 0 1] [3 2 1 0])))))
    ))

(deftest apply-guess-result-test
  (testing "apply-guess-result function"
    (let [rem-poss (get-all-possibilities 4)
          guess [0 1 2 3]]
      (is (= (apply-guess-result rem-poss guess 0)
             (sort '([1 0 3 2] [1 2 3 0] [1 3 0 2]
                     [2 0 3 1] [2 3 0 1] [2 3 1 0]
                     [3 0 1 2] [3 2 0 1] [3 2 1 0]))))
      (is (= (apply-guess-result rem-poss guess 1)
             (sort '([0 2 3 1] [0 3 1 2]
                     [1 2 0 3] [1 3 2 0]
                     [2 0 1 3] [2 1 3 0]
                     [3 0 2 1] [3 1 0 2]))))
      (is (= (apply-guess-result rem-poss guess 2)
             (sort '([0 1 3 2] [0 3 2 1] [0 2 1 3] [3 1 2 0] [2 1 0 3] [1 0 2 3]))))
      (is (= (apply-guess-result rem-poss guess 3) '()))
      (is (= (apply-guess-result rem-poss guess 4) '([0 1 2 3]))))
    ))
