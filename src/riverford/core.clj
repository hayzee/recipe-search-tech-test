(ns riverford.core
  (:require [riverford.config :as cfg]
            [riverford.indexer :as idx]
            [riverford.search :as search]
            [clojure.pprint :as pp]))


(defn- reindex
  "Reindexing dialog."
  [idx]
  (do
    (print "Creating index ......... ")
    (flush)
    (idx/store-index! idx (cfg/config :dir-path))
    (println "done!")))


(defn- results-renderer
  "Render the results as a list of files"
  [results]
  (println (count results) "results found ... ")
  (doall (for [result (map :file results)]
           (println result)))
  (println (count results) "results found ... ")
  (println "End of result list"))


(defn- rawmap-renderer
  "Render the results as a raw results map - useful for checking the results are correct"
  [results]
  (do
    (println (count results) "results found ... ")
    (pp/pprint results)
    (println (count results) "results found ... ")
    (println "End of result list")))


(defn- search-handler
  "Search handler: calls the search/perform-search and displays the results."
  [results-renderer search-string]
  (println "\nSearching for " search-string "\n")
  (let [results (search/perform-search @idx/idx search-string)]
    (if (seq results)
      (results-renderer results)
      (println "No results found!"))))


(defn- test-handler
  "Dummy handler. Used in dev when working on the input-loop function."
  [_ seach-string]
  (println "You entered " seach-string))


(def handler (partial search-handler results-renderer))


(defn set-raw-renderer
  "Set the handler's renderer, either to view normal results or raw, verbose results:
  This is set using the :m or :n command when running the program.
  It's also possible to connect to a running repl and call it from there:
    e.g. $ lein repl :connect localhost:<PORT>"
  [tf]
  (println "Switching to" (if tf "raw map" "normal") "output")
  (if tf
    (alter-var-root #'riverford.core/handler (constantly (partial search-handler rawmap-renderer)))
    (alter-var-root #'riverford.core/handler (constantly (partial search-handler results-renderer)))))


(defn- input-loop
  "Main input loop handler."
  [handler]
  (loop []
    (print "\nEnter search term : ")
    (flush)
    (let [line (read-line)]
      (when (not (#{":exit" ":x" ":quit" ":q"} (.toLowerCase line)))
        (cond
          (#{":reindex" ":r"} (.toLowerCase line))  (reindex idx/idx)
          (#{":map" ":m"} (.toLowerCase line))  (set-raw-renderer true)
          (#{":normal" ":n"} (.toLowerCase line)) (set-raw-renderer false)
          :else (handler line))
        (recur)))))


(defn- run-search-dialog
  "Main dialog. Can be run in the repl using to prevent reindexing at start."
  []
  (println "\nWelcome to recipe search")
  (println "Enter :x to exit, or :r to reindex")
  (input-loop #'riverford.core/handler)
  (println "\n\nFarewell from recipe search.")
  (println "\nPlease visit us again soon."))


(defn -main
  "Main entry point for the search program."
  [& _]
  (do
    (reindex idx/idx)
    (run-search-dialog)))
