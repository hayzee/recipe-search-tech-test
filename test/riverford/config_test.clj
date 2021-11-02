(ns riverford.config-test
  (:require [clojure.test :refer :all])
  (:require [riverford.config :refer :all]))

(deftest config-test
  (is (= String (type (config :dir-path))) ":dir-path should be configured as a string")
  (is (= nil (type (config :bananas))) "keywords not configured should return nil"))

(deftest load-edn-test
  (is (= clojure.lang.PersistentArrayMap (type (#'riverford.config/load-edn "resources/config.edn"))) "config.edn should contain a map"))
