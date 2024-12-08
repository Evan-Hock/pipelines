(ns pipelines.core-test
  (:require [clojure.test :refer [deftest testing is]])
  (:require [pipelines.core :refer [|>]]))


(deftest single-argument-tests
  (testing "Does nothing if no pipeline is specified"
    (is (= 0 (|> 0)))
    (is (= "string" (|> "string")))
    (is (= :keyword (|> :keyword)))
    (is (= '(l i s t) (|> '(l i s t))))))


(deftest simple-pipeline-tests
  (testing "Pipelines without lists work as expected"
    (is (= 1 (|> 0 inc)))
    (is (= (range 2 3) (|> (range 3) next next)))
    (is (= 10 (|> 0 inc inc inc inc inc inc inc inc inc inc)))))


(deftest list-form-pipeline-tests
  (testing "Pipelines with lists works as expected, threading last"
    (is (= 5 (|> 2 (* 2) (+ 1))))
    (is 
     (= (range 20 101 20)
        (|> (range)
            (map #(* 10 %))
            (filter #(= 0 (rem % 20)))
            (drop 1)
            (take 5))))
    (is 2 (|> 1 (inc)))))


(deftest underscore-pipeline-tests
  (testing "Pipelines with underscores substitute the arguments correctly"
    (is (= 9 (|> 20 (quot _ 2) (- _ 1))))
    (is (= 2401 (|> 7 (* _ _) (* _ _))))))