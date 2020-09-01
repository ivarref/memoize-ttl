(ns ivarref.memoize-ttl-test
  (:require [clojure.test :refer :all]
            [ivarref.memoize-ttl.impl :as impl]))

(deftest find-args-test
  (testing "nil values works"
    (is (= [nil] (impl/find-args 0 {[:a] {:expires 3600 :val nil}} [:a]))))
  (testing "regular values works"
    (is (= [:a] (impl/find-args 0 {[:a] {:expires 3600 :val :a}} [:a]))))
  (testing "expired values returns nil"
    (is (= nil (impl/find-args 3600 {[:a] {:expires 3600 :val :a}} [:a]))))
  (testing "unknown key returns nil"
    (is (= nil (impl/find-args 3600 {[:a] {:expires 3600 :val :a}} [:b])))))

(deftest memo-ttl
  (testing "returning non-map / incorrect map throws exception"
    (is (thrown? AssertionError (impl/memoize-ttl! 0 (constantly 123) (atom {}) nil)))
    (is (thrown? AssertionError (impl/memoize-ttl! 0 (constantly {}) (atom {}) nil)))
    (is (thrown? AssertionError (impl/memoize-ttl! 0 (constantly {:val 123 :ttl 0}) (atom {}) nil)))
    (is (thrown? AssertionError (impl/memoize-ttl! 0 (constantly {:val 123 :ttl "asdf"}) (atom {}) nil)))
    (is (thrown? AssertionError (impl/memoize-ttl! 0 (constantly {:value 123 :ttl 1}) (atom {}) nil))))
  (testing "basic happy case"
    (is (= 123 (impl/memoize-ttl! 0 (constantly {:val 123 :ttl 1}) (atom {}) nil))))
  (testing "nil return value"
    (let [mem (atom {})]
      (is (= nil (impl/memoize-ttl! 0 (constantly {:val nil :ttl 1}) mem nil)))
      (is (= {nil {:expires 1000 :val nil}} @mem))))
  (testing "regular return value"
    (let [mem (atom {})]
      (is (= :a (impl/memoize-ttl! 0 (constantly {:val :a :ttl 1}) mem nil)))))
  (testing "basic caching works"
    (let [mem (atom {})
          cnt (atom 0)
          f (fn [] {:val (swap! cnt inc) :ttl 1})]
      (is (= 1 (impl/memoize-ttl! 0 f mem nil)))
      (is (= 1 (impl/memoize-ttl! 999 f mem nil)))
      (is (= 2 (impl/memoize-ttl! 1000 f mem nil)))
      (is (= 2 (impl/memoize-ttl! 1999 f mem nil)))
      (is (= 3 (impl/memoize-ttl! 2000 f mem nil)))))
  (testing "different args works"
    (let [mem (atom {})
          cnt (atom {:a 0 :b 0})
          f (fn [x] {:val (get (swap! cnt update x inc) x) :ttl 1})]
      (is (= 1 (impl/memoize-ttl! 0 f mem (list :a))))
      (is (= 2 (impl/memoize-ttl! 1000 f mem (list :a))))
      (is (= 3 (impl/memoize-ttl! 2000 f mem (list :a))))
      (is (= 1 (impl/memoize-ttl! 2001 f mem (list :b))))
      (is (= 3 (impl/memoize-ttl! 2500 f mem (list :a))))
      (is (= 2 (impl/memoize-ttl! 3001 f mem (list :b))))
      (is (= {'(:b) {:expires 4001 :val 2}
              '(:a) {:expires 3000 :val 3}}
             @mem)))))
