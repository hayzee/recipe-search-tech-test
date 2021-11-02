(ns riverford.config
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import (java.io PushbackReader IOException)))


(defn- load-edn
  "Load edn from an io/reader source (filename or io/resource)."
  [source]
  (try
    (with-open [r (io/reader source)]
      (edn/read (PushbackReader. r)))
    (catch IOException e
      (printf "Couldn't open '%s': %s\n" source (.getMessage e)))
    (catch RuntimeException e
      (printf "Error parsing edn file '%s': %s\n" source (.getMessage e)))))


;; Note: Global state. Could use mount for this, but this is fine for now.
(def config (load-edn "resources/config.edn"))

