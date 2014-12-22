(ns fondo.db
  (:require
   [taoensso.faraday :as far]
   [validata.core :as v]))

(defonce dynamodb
  {:access-key "<AWS_DYNAMODB_ACCESS_KEY>"
   :secret-key "<AWS_DYNAMODB_SECRET_KEY>"
   :endpoint "http://localhost:8000"})

(def default-table-name :values)

(defn initialize-table
  "Set up Dynamo table to store Fondo values"
  [& [table-name]]
  (far/create-table dynamodb
                    (or table-name default-table-name)
                    [:vid :n]
                    {}))

(def value-validations
  {:name [v/string v/required]
   :uri [v/string v/required]})

;; TODO: Ensure no current value with `id` is stored
(defn put-value
  [id val & [table-name]]
  (let [e (v/errors val value-validations)]
    (if (empty? e)
      (do
        (far/put-item dynamodb
                      (or table-name default-table-name)
                      {:vid id
                       :value (far/freeze val)})
        {:stored true :vid id})
      {:errors e})))

(defn get-value
  [id & [table-name]]
  (if-let [val (:value (far/get-item dynamodb
                                     (or table-name default-table-name)
                                     {:vid id}))]
    {:id id :value val}
    {:error :not-found}))
