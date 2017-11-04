(ns areyoutheone.scenario-test
  (:require [clojure.test :refer :all]
            [areyoutheone.scenario :refer :all]))

(def name-map {:A 0 :B 1 :C 2 :D 3 :E 0 :F 1 :G 2 :H 3})

(deftest create-name-assoc-test
  (testing "create-name-associations function"
    (is (= (create-name-associations "A,B,C,D" "E,F,G,H")
           [[:A :B :C :D] [:E :F :G :H] {:A 0 :B 1 :C 2 :D 3 :E 0 :F 1 :G 2 :H 3}]))))

(deftest create-guess-test
  (testing "create-guess function"
    (is (= (create-guess '("A:E" "B:F" "C:H" "D:G") name-map)
           [0 1 3 2]))))

(deftest parse-results-test
  (testing "parse-results function"
    (let [results '("A:E,B:F,C:H,D:G,2" "A,H,false")]
      (is (= (parse-results results name-map)
             [[{:matchup [0 3] :correct? false}] [{:guess [0 1 3 2] :beams 2}]])))))

(deftest filter-poss-test
  (testing "filter-poss function"
    (let [tb-results [{:matchup [0 3] :correct? false}]
          guess-results [{:guess [0 1 3 2] :beams 2}]]
      (is (= (filter-poss tb-results guess-results)
             (sort [[0 1 2 3] [0 2 3 1] [0 3 1 2] [2 1 3 0] [1 0 3 2]]))))))
