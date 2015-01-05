(ns fondo.db-test
  (:require
      [clojure.test :refer :all]
      [fondo.db :as db]
      [fondo.encode :refer [encode-and-hash val-with-data]]
      [taoensso.faraday :as far]
      [validata.core :as v]))

(def db
  {:access-key "<AWS_DYNAMODB_ACCESS_KEY>"
   :secret-key "<AWS_DYNAMODB_SECRET_KEY>"
   :endpoint "http://localhost:8000"})

(def table-name :test-values)

(defn table-setup
  "Create table, then destroy on completion"
  [f]
  (db/initialize-table db table-name)
  (f)
  (far/delete-table db table-name))

(defn put-value
  "Put value, using test values table"
  [id val]
  (db/put-value db table-name id val))

(defn get-value
  "Get value, using test values table"
  [id]
  (db/get-value db table-name id))

(use-fixtures :once table-setup)

(deftest validate-value
  (testing "correctly validates values"
    (let [valid1 {:name "Name"
                  :uri  "http://example.com/"}
          valid2 {:name "Name"
                  :uri  "http://example.com/"
                  :description "Description"}
          invalid1 {:uri "http://example.com/"}
          invalid2 {:name "Name"}
          invalid3 {:uri "URI"
                    :name "Name"}]
      (is (v/valid? valid1 db/value-validations))
      (is (v/valid? valid2 db/value-validations))
      (is (not (v/valid? invalid1 db/value-validations)))
      (is (not (v/valid? invalid2 db/value-validations)))
      (is (not (v/valid? invalid3 db/value-validations))))))

(deftest put-and-get-values
  (testing "stores and retrieves values"
    (let [val {:name "Test"
               :uri "http://example.com/"}
          id (encode-and-hash (val-with-data val))]
      (put-value id val)
      (let [result (get-value id)
            missing (get-value "67890")]
        (is (= id (:id result)))
        (is (= val (:value result)))
        (is (= {:error :not-found} missing))))))

(deftest ensure-unique-id
  (testing "does not overwrite preexisting IDs"
    (let [val {:name "Test"
               :uri "http://example.com/"}
          id (encode-and-hash (val-with-data val))]
      (put-value id val)
      (let [failed (put-value id val)]
        (is (= ["Value with that ID exists"] (get-in failed [:errors :vid])))))))
