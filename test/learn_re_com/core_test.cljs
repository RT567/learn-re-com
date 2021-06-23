(ns learn-re-com.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [learn-re-com.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
