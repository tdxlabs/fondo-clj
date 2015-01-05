(ns fondo.db
  (:require
   [clojurewerkz.urly.core :refer [url-like absolute?]]
   [fondo.encode :refer [encode-and-hash val-with-data]]
   [taoensso.faraday :as far]
   [validata.core :as v]))

(defn initialize-table
  "Set up Dynamo table to store Fondo values"
  [db table-name]
  (far/create-table db
                    table-name
                    [:vid :s]
                    {}))

(defn ensure-table
  "Checks to see if the given table has been initialized,
   and if not, initializes it."
  [db table-name]
  (if-not (some #{table-name} (far/list-tables db))
    (initialize-table db table-name)
    true))

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
  [db table-name id]
  (if-let [val (:value (far/get-item db
                                     table-name
                                     {:vid id}))]
    {:id id :value val}
    {:error :not-found}))

(defn put-value
  "Put val in the database with ID id.
   Validates existence of :name and :uri in val, and :uri
   must be a valid URI. The data at :uri will be downloaded
   and added to val as :data, and id must match the bencoded
   and SHA3-384 hashed result."
  [db table-name id val]
  (let [e (v/errors val value-validations)]
    (if (empty? e)
      (if (= :not-found (:error (get-value db table-name id)))
        (let [with-data (val-with-data val)]
          (if (= id (encode-and-hash with-data))
            (do
              (far/put-item db
                            table-name
                            {:vid id
                             :value (far/freeze val)})
              {:stored true :vid id})
            {:errors {:vid ["Hash does not match ID"]}}))
        {:errors {:vid ["Value with that ID exists"]}})
      {:errors e})))

