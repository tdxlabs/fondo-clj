(ns fondo.db
  (:require
   [clojurewerkz.urly.core :refer [url-like absolute?]]
   [fondo.encode :refer [encode-and-hash val-with-data]]
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
                    [:vid :s]
                    {}))

(defn ^:internal uri?
  [k v & [_]]
  (if (nil? k) true
      (and (url-like v)
           (absolute? v))))

(def ^:internal uri
  {:validator uri?
   :error "value must be a valid URI"})

(def value-validations
  {:name [v/string v/required]
   :uri [v/string v/required uri]})

(defn get-value
  "Get a value specified by id from the database."
  [id & [table-name]]
  (if-let [val (:value (far/get-item dynamodb
                                     (or table-name default-table-name)
                                     {:vid id}))]
    {:id id :value val}
    {:error :not-found}))

(defn put-value
  "Put val in the database with ID id.
   Validates existence of :name and :uri in val, and :uri
   must be a valid URI. The data at :uri will be downloaded
   and added to val as :data, and id must match the bencoded
   and SHA3-384 hashed result."
  [id val & [table-name]]
  (let [e (v/errors val value-validations)
        t (or table-name default-table-name)]
    (if (empty? e)
      (if (= :not-found (:error (get-value id t)))
        (let [with-data (val-with-data val)]
          (if (= id (encode-and-hash with-data))
            (do
              (far/put-item dynamodb
                            t
                            {:vid id
                             :value (far/freeze val)})
              {:stored true :vid id})
            {:errors {:vid ["Hash does not match ID"]}}))
        {:errors {:vid ["Value with that ID exists"]}})
      {:errors e})))

