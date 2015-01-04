(ns fondo.db
  (:require
   [bencode.core :refer [bencode]]
   [clojurewerkz.urly.core :refer [url-like absolute?]]
   [pandect.core :refer [sha3-384]]
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

(defn ^:internal bencode-and-hash
  [val]
  (sha3-384 (bencode val)))

(defn put-value
  "Put val in the database with ID id.
   Validates existence of :name and :uri in val, and :uri
   must be a valid URI. No value with id may exist in the
   database already, and id must equal the SHA3-384 hashed
   result of bencoding val"
  [id val & [table-name]]
  (let [e (v/errors val value-validations)
        t (or table-name default-table-name)]
    (if (empty? e)
      (if (= :not-found (:error (get-value id t)))
        (if (= id (bencode-and-hash val))
          (do
            (far/put-item dynamodb
                          t
                          {:vid id
                           :value (far/freeze val)})
            {:stored true :vid id})
          {:errors {:vid ["Hash does not match ID"]}})
        {:errors {:vid ["Value with that ID exists"]}})
      {:errors e})))

