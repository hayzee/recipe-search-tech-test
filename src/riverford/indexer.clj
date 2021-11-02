(ns riverford.indexer
  (:require [clojure.string :as s]
            [clojure.java.io :as io]))


;; Note: Global state. Could use mount for this, and/or a database, but this is fine for now.
(def idx (atom {:file-index   nil
                :term-idf-map nil}))


(defn- remove-non-alphanum
  "Remove non-alphanumeric characters from the string st'"
  [st]
  (apply str (filter #(Character/isLetterOrDigit ^char %) st)))


(defn tokenise-string
  "Extract all words from a string tstr, stripping all non-alphanumeric characters - returns a vector of words."
  [st]
  (filter
    seq
    (map
      remove-non-alphanum
      (mapcat
        #(s/split % #" ")
        (s/split-lines
          (s/lower-case st))))))


(defn- tokenise-file
  "Tokenise the contents of a file"
  [file-name]
  (let [file-tokens (tokenise-string (slurp file-name))
        term-freqs (frequencies file-tokens)]
    {:file file-name
     :token-count (count file-tokens)
     :term-freqs term-freqs}))


(defn- get-filenames
  "Given a directory name, scan for files (excluding directories)"
  [dir-path]
  (->>
    (file-seq (io/file dir-path))
    (filter #(.isFile %))
    (map #(.getAbsolutePath %))))


(defn- create-index
  "Builds the index, which is map comprising two things:

  :file-index - A list of all indexed files as map entries -

      {:file <filename>
       :token-count <num-terms>
       :term-freqs: <map-of-term-frequencies>}

       The <map-of-term-frequencies> is of the form

          {word1 tf1,
           word2 tf2
           .
           .
           wordn tfn}

           for all words in the given file (i.e. document)

  :term-idf-map - A list of all terms across all documents and their idf (inverse doc. frequency) value as a map:

      {<word1> <idf-value-word1>
       <word2> <idf-value-word2>
       .
       .
       .
       <wordn> <idf-value-wordn>}

  The idf value of a given term is calculated as log(num-docs-containing-term / num-docs)

  The idf value is used at search time to derive the tf-idf value for each term in a given document. These are
  multiplied together to give the final tfidf-score which is the main sort item for ranking. Documents with the highest
  tfidf value relative to a given query are displayed first.

  See riverford.search/perform-search for further details relating to ranking order.
  "
  [dir-path]
  (let [file-index (map tokenise-file (get-filenames dir-path))
        term-idf-map (into
                       {}
                       (->>
                         (mapcat :term-freqs file-index)
                         (group-by first)
                         (mapv #(vector (first %) (Math/log (/ (count file-index) (count (second %))))))
                         ))]
    {:file-index   file-index
     :term-idf-map term-idf-map}))


(defn store-index!
  "Set the idx atom with a call to create-index."
  [idx dir-path]
  (swap! idx merge (create-index dir-path)))


(defn term-idf
  "Return the idf value for the given term from the index."
  [idx term]
  (get (:term-idf-map idx) term))


(defn term-idfs
  "Return the idfs value for the given terms from the index, returning a map of [term idf] pairs."
  [idx terms]
  (into {} (map #(vector % (term-idf idx %)) terms)))
