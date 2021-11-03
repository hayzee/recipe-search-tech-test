(ns riverford.search-test
  (:require [clojure.test :refer :all])
  (:require [riverford.search :refer :all]))


(def test-dir-path "resources/testfiles")
(def test-idx (atom {}))

(do
  (#'riverford.indexer/store-index! test-idx test-dir-path)
  nil                                                       ; suppress output
  )

(def test-file-1 "resources/testfiles/testfile1.txt")
(def test-file-2 "resources/testfiles/testfile2.txt")

(def test-index-entry (first (:file-index @test-idx)))


(deftest term-freq-test
  (is (int? (#'riverford.search/term-freq test-index-entry "file"))
      "Valid terms known to be in an index-entry should yield an integer value")
  (is (= nil (#'riverford.search/term-freq test-index-entry "sausage"))
      "Valid terms known to NOT be present in an index-entry should yield nil"))


(deftest find-term-test
  (is (= 2 (count (#'riverford.search/find-term @test-idx "file")))
      "Valid search terms should return correct number of entries (2)")
  (is (= 1 (count (#'riverford.search/find-term @test-idx "really")))
      "Valid search terms should return correct number of entries (1)")
  (is (= 0 (count (#'riverford.search/find-term @test-idx "sausage")))
      "Search terms not present should return nothing")
  (is (= #{:file :token-count :term-freqs} (set (keys (first (#'riverford.search/find-term @test-idx "really")))))
      "Valid searches should return a set {:token-count :file :term-freqs} maps"))


(deftest find-terms-test
  (is (= 2 (count (#'riverford.search/find-terms @test-idx ["file"])))
      "Valid search terms should return correct number of entries (2)")
  (is (= 1 (count (#'riverford.search/find-terms @test-idx ["really"])))
      "Valid search terms should return correct number of entries (1)")
  (is (= 1 (count (#'riverford.search/find-terms @test-idx ["really" "file"])))
      "Multi term searches should yield only the number of files in which all terms exist"))


(deftest find-terms-test
  (testing "Checking found-terms returns correct values ..."
    (let [found-terms (:term-freqs (first (#'riverford.search/find-terms @test-idx ["really" "file"])))]
      (is (int? (get found-terms "really")))
      (is (int? (get found-terms "file")))
      (is (int? (get found-terms "nice")))
      (is (nil? (get found-terms "sausage"))))))


(deftest augment-index-entry
  (is (= {:token-count 24,
          :term-freqs {"file" 2, "sometimes" 1},
          :term-idfs {"file" 0.0, "sometimes" 0.6931471805599453},
          :term-tfidfs {"file" 0.0, "sometimes" 0.6931471805599453},
          :tfidf-score 0.0}
         (dissoc (#'riverford.search/augment-index-entry @test-idx test-index-entry ["file" "sometimes"]) :file))
      "Augmentation should eliminate unmatched terms-freqs and add entries for :term-idfs, :term-tfidfs and :tfidf-score"))


(deftest perform-search-test
  (is (empty? (perform-search @test-idx "egg noodle"))
      "Searches for any unmatched terms should return an empty collection")
  (is (not (empty? (perform-search @test-idx "file")))
      "Searches for any unmatched terms should return an empty collection")
  (is (not (empty? (perform-search @test-idx "file nice")))
      "Searches for any unmatched terms should return an empty collection"))
