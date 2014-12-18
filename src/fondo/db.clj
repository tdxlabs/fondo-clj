(ns fondo.db
  (:require
   [taoensso.faraday :as far]))

(def values (atom {}))

(defonce dynamodb
  {:access-key "<AWS_DYNAMODB_ACCESS_KEY>"
   :secret-key "<AWS_DYNAMODB_SECRET_KEY>"
   :endpoint "http://localhost:8000"})

(defn initialize-table
  "Set up Dynamo table to store Fondo values"
  []
  (far/create-table dynamodb
                    :values
                    [:vid :n]
                    {}))

;; TODO: Validate `val`
;; TODO: Ensure no current value with `id` is stored
(defn put-value
  [id val]
  (far/put-item dynamodb
                :values
                {:vid id
                 :value (far/freeze val)}))

(defn get-value
  [id]
  (if-let [val (:value (far/get-item dynamodb :values {:vid id}))]
    {:id id :value val}
    {:error :not-found}))
