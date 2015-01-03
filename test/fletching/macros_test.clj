(ns fletching.macros-test
  (:require [clojure.test :refer :all]
            [fletching.macros :refer :all]))

(deftest basic-tests
  (testing ">>"
    (are [x y] (= x y)
         (>>) nil
         (>> vector) []
         (>> 1 vector) [1]
         (>> 1 vector 2) [2 1]
         (>> 1 vector 2 3 4) [2 3 4 1]))
  (testing "<<"
    (are [x y] (= x y)
         (<<) nil
         (<< vector) []
         (<< vector 1) [1]
         (<< vector 1 2) [2 1]
         (<< vector 1 2 3 4) [4 1 2 3]))
  (testing "?>"
    (are [x y] (= x y)
         (?>) nil
         (?> vector) nil
         (?> vector ?) []
         (?> 1 vector ?) [1]
         (?> 1 vector 2 ? 3 ? 4) [2 1 3 1 4]))
  (testing "<?"
    (are [x y] (= x y)
         (<?) nil
         (<? vector) nil
         (<? ? vector) []
         (<? vector ? 1) [1]
         (<? vector 1 ? 2 ? 3 4) [1 4 2 4 3]))
  (testing "><"
    (are [x y] (= x y)
         (><) nil
         (>< vector) []
         (>< vector 1) [1]
         (>< vector 1 2) [1 2]
         (>< vector 1 2 3 4) [1 2 3 4]))
  (testing "<fn"
    (are [x y] (= x y)
         ((<fn) vector) []
         ((<fn inc) 1) 2
         ((<fn do) 1) 1
         ((<fn / 5 3) 7) (/ 7 5 3)))
  (testing "<&fn"
    (are [x y] (= x y)
         :fail (try
                 ((<&fn) 1 2)
                 (catch Exception ex :fail))
         ((<&fn first) 1 2 3) 1
         ((<&fn nth 2) 4 5 6 7) 6)))

(deftest composition-tests
  (testing ">>"
    (is (= (-> [:a :b :c]
               (>> take 2 ,))
           [:a :b])))
  (testing "<<"
    (is (= (->> [:a :b :c]
                (<< nth , 1))
           (->> [:a :b :c]
                (<< -> , (nth 1)))
           :b)))
  (testing "?>"
    (is (= (-> 2
               (?> vector 1 ? 3))
           [1 2 3])))
  (testing "<?"
    (is (= (->> 2
                (<? vector 1 ? 3))
           [1 2 3])))
  (testing "><"
    (is (= (-> inc
               (>< 1))
           (->> inc
                (<< >< , 1))
           2))))

(deftest README-tests
  (testing ">>"
    (is (= (-> [[1 2 3] [3 4 5]]
               (nth 1)
               (->> (map -))
               (nth 1))
           (-> [[1 2 3] [3 4 5]]
               (nth 1)
               (>> map -)
               (nth 1))
           -4)))
  (testing "<<"
    (is (= (-> (->> [-1 -2 -3 -4 -5]
                    (map -)
                    (filter even?))
               last
               range
               rest
               (->> (reduce *)))
           (as-> [-1 -2 -3 -4 -5] x
                 (map - x)
                 (filter even? x)
                 (-> x last range rest)
                 (reduce * x))
           (->> [-1 -2 -3 -4 -5]
                (map -)
                (filter even?)
                (<< -> last range rest)
                (reduce *))
           6))
    (is (= (->> [1 2 3 4 5]
                (map -)
                (<< nth 2)
                (/ 6))
           -2)))
  (testing "?> and ?>"
    (is (= (->> [1 2 3 4 5]
                (take 3)
                (reduce *)
                (<? str ? " is " (if (even? ?) "even" "odd"))
                (<? do [? ?]))
           ["6 is even" "6 is even"])))
  (testing "><"
    (is (= (-> [+ - * /]
               (nth 2)
               (>< 3 4)
               inc)
           (->> [+ - * /]
                (drop 2)
                first
                (<< >< 3 4)
                inc)
           13)))
  (testing "<fn and <&fn"
    (is (= (let [f (<fn ->> (map -)
                        ,   (filter even?)
                        ,   (reduce *))]
             (f [2 4 5 6]))
           (let [g (<&fn ->> (map -)
                         ,   (filter even?)
                         ,   (reduce *))]
             (g 2 4 5 6))
           -48))))