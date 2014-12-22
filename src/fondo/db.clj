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

(defn get-value
  [id & [table-name]]
  (if-let [val (:value (far/get-item dynamodb
                                     (or table-name default-table-name)
                                     {:vid id}))]
    {:id id :value val}
    {:error :not-found}))

(defn put-value
  [id val & [table-name]]
  (let [e (v/errors val value-validations)
        t (or table-name default-table-name)]
    (if (empty? e)
      (if (= :not-found (:error (get-value id t)))
        (do
          (far/put-item dynamodb
                        t
                        {:vid id
                         :value (far/freeze val)})
          {:stored true :vid id})
        {:errors ["Value with that ID exists"]})
      {:errors e})))

