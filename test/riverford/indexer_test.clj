(ns riverford.indexer-test
  (:require [clojure.test :refer :all])
  (:require [riverford.indexer :refer :all]))


(deftest remove-non-alphanum-test
  (is (= "bac0n" (#'riverford.indexer/remove-non-alphanum "bac0n")))
  (is (= "Sausages" (#'riverford.indexer/remove-non-alphanum "Sausages")))
  (is (= "Sausages" (#'riverford.indexer/remove-non-alphanum "Saus)ages")))
  (is (= "Sausages" (#'riverford.indexer/remove-non-alphanum "@Sausa!ges£")))
  (is (= "Unaltered" (#'riverford.indexer/remove-non-alphanum "Unaltered"))))


