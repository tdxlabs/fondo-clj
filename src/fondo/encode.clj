(ns fondo.encode
  (:require
   [bencode.core :refer [bencode]]
   [pandect.core :refer [sha3-384]]))

(defn encode-and-hash
  [val]
  (sha3-384 (bencode val)))
