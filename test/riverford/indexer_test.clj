(ns riverford.indexer-test
  (:require [clojure.test :refer :all])
  (:require [riverford.indexer :refer :all :exclude [idx]]))


(def test-dir-path "resources/testfiles")
(def test-idx (atom {}))


(do
  (#'riverford.indexer/store-index! test-idx test-dir-path)
  nil   ; suppress output
  )


(def test-file-1 "resources/testfiles/testfile1.txt")
(def test-file-2 "resources/testfiles/testfile2.txt")


(deftest remove-non-alphanum-test
  (is (= "bac0n" (#'riverford.indexer/remove-non-alphanum "bac0n")))
  (is (= "Sausages" (#'riverford.indexer/remove-non-alphanum "Sausages")))
  (is (= "Sausages" (#'riverford.indexer/remove-non-alphanum "Saus)ages")))
  (is (= "Sausages" (#'riverford.indexer/remove-non-alphanum "@Sausa!ges£")))
  (is (= "Unaltered" (#'riverford.indexer/remove-non-alphanum "Unaltered"))))


(deftest tokenise-string-test
  (is (= [] (#'tokenise-string "")))
  (is (= ["x"] (#'tokenise-string "x")))
  (is (= ["this", "is", "a", "string"] (#'tokenise-string "this is a string")))
  (is (= ["for", "the", "police", "call", "999"] (#'tokenise-string "For the Police, call 999!")))
  (is (= [] (#'tokenise-string " ! !   !  ! !     !!!!"))))


(deftest tokenise-file-test
  (is (= (#'riverford.indexer/tokenise-file test-file-1)
         {:file        "resources/testfiles/testfile1.txt",
          :token-count 24,
          :term-freqs  {"this"        1,
                        "it"          2,
                        "chars"       1,
                        "is"          2,
                        "contains"    1,
                        "nonalphanum" 1,
                        "gosh"        1,
                        "that"        1,
                        "even"        1,
                        "a"           2,
                        "sometimes"   1,
                        "but"         1,
                        "shouldnt"    1,
                        "be"          1,
                        "1"           1,
                        "file"        2,
                        "problem"     1,
                        "though"      1,
                        "test"        1,
                        "nice"        1}})))


(deftest get-filenames-test
  (is (= [true, true]
         (map #(.contains % "testfile") (#'riverford.indexer/get-filenames test-dir-path)))))


(deftest create-index-test
  (is (= [:file-index :term-idf-map]
         (keys (#'riverford.indexer/create-index test-dir-path))))
  (is (boolean (seq (filter pos?
                            (map count (vals (#'riverford.indexer/create-index test-dir-path))))))
      "Testfiles should create a non-empty index."))


(deftest store-index!-test
  (is (= [:file-index :term-idf-map] (keys @test-idx)))
  (is (boolean (seq (filter pos?
                            (map count (vals @test-idx))))) "Testfiles should store a non-empty index."))


(deftest term-idf-test
  (is (nil? (term-idf @test-idx "bacon")) "Terms not present in the index should return nil.")
  (is (double? (term-idf @test-idx "file")) "Terms that are present in the index should return a double."))


(deftest term-idfs-test
  (is (every? double? (vals (term-idfs @test-idx ["this" "is" "a" "file"])))
      "All valid tokens should yield a double term-idf value.")
  (is (= {} (term-idfs @test-idx [])) "An empty terms list should yield an empty terms-idfs map.")
  (is (= {"chips" nil, "bananas" nil} (term-idfs @test-idx ["chips" "bananas"]))
      "Words not in the test files should have a nil idf value")
  (is (every? double? (vals
                        (term-idfs
                          @test-idx
                          (vec
                            (concat
                              (map first (:term-freqs (#'riverford.indexer/tokenise-file test-file-1)))
                              (map first (:term-freqs (#'riverford.indexer/tokenise-file test-file-2))))))))
      "All valid tokens from the test files should yield a double term-idf value."))