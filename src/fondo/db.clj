(ns fondo.db
  (:require
   [taoensso.faraday :as far]))

(def values (atom {}))

(def dynamodb
  {:access-key "<AWS_DYNAMODB_ACCESS_KEY>"
   :secret-key "<AWS_DYNAMODB_SECRET_KEY>"
   :endpoint "http://localhost:8000"})

(defn put-value
  [id val]
  (swap! values assoc id val))

(defn get-value
  [id]
  (@values id))
