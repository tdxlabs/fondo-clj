(ns fondo.encode
  (:require
   [bencode.core :refer [bencode]]
   [clj-http.client :as client]
   [pandect.core :refer [sha3-384]]))

(defn val-with-data
  "Download data and add to value map"
  [val]
  (let [data (:body (client/get (:uri val)))]
    (assoc val :data data)))

(defn encode-and-hash
  [val]
  (sha3-384 (bencode val)))
