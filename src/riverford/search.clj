(ns riverford.search
  (:require [riverford.indexer :as indexer]
            [clojure.set :as cset]))


(defn term-freq
  "Find the term frequency of a term in an index-entry, or return nil if not found."
  [index-entry term]
  (get (:term-freqs index-entry) term))


(defn find-term
  "Return the set of index-entries in idx that contain term."
  [idx term]
  (set (filter #(term-freq % term) (idx :file-index))))


(defn find-terms
  "Return the set of index-entries in idx that contain each term in terms."
  [idx terms]
  (if (seq terms)
    (apply cset/intersection (map #(find-term idx %) terms))
    #{}))


(defn augment-search
  "Augment the results with metrics and eliminate unmatched terms in :term-freqs from each index-entry."
  [idx index-entry terms]
  (let [term-freqs (select-keys (:term-freqs index-entry) terms)
        term-idfs (indexer/term-idfs idx terms)
        term-tfidfs (merge-with
                      *
                      term-freqs
                      term-idfs)]
    (assoc
      index-entry
      :term-freqs term-freqs
      :term-idfs term-idfs
      :term-tfidfs term-tfidfs
      :tfidf-score (reduce * (vals term-tfidfs)))))


(defn perform-search
  "Return the raw search-results map for the query string."
  [idx query-string]
  (let [terms (indexer/tokenise-string query-string)]
    (->>
      (find-terms idx terms)
      (map #(augment-search idx % terms))
      (sort-by (juxt                                    ; Ranking is a composite sort comprising:
                 :tfidf-score                           ; - High tf-idf values rank highest.
                 #(reduce + (vals (:term-freqs %)))     ; - Sum of term frequencies.
                 #(- (:token-count %))                  ; - A lower token count will lift any result with an identical
                                                        ;   tfidf-score and term-freqs to another - an alternative would
                                                        ;   have been to use a weighted tf count (based on document
                                                        ;   length).
                 ))
      reverse)))
