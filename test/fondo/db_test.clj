(ns fondo.db-test
  (:require [clojure.test :refer :all]
            [fondo.db :as db]
            [taoensso.faraday :as far]
            [validata.core :as v]))

(defn table-setup
  "Create table, then destroy on completion"
  [f]
  (db/initialize-table :test-values)
  (f)
  (far/delete-table db/dynamodb :test-values))

(defn put-value
  "Put value, using test values table"
  [id val]
  (db/put-value id val :test-values))

(defn get-value
  "Get value, using test values table"
  [id]
  (db/get-value id :test-values))

(use-fixtures :once table-setup)

(deftest validate-value
  (testing "correctly validates values"
    (let [valid1 {:name "Name"
                  :uri  "http://example.com/data"}
          valid2 {:name "Name"
                  :uri  "http://example.com/data"
                  :description "Description"}
          invalid1 {:uri "http://example.com/data"}
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
    (let [id 12345
          val {:name "Test"
               :uri "http://example.com/data"}]
      (db/put-value id val :test-values)
      (let [result (get-value id)
            missing (get-value 67890)]
        (is (= id (:id result)))
        (is (= val (:value result)))
        (is (= {:error :not-found} missing))))))

(deftest ensure-unique-id
  (testing "does not overwrite preexisting IDs"
    (let [id 98765
          val {:name "Test"
               :uri "http://example.com/data"}]
      (put-value id val)
      (let [failed (put-value id val)]
        (is (= ["Value with that ID exists"] (:errors failed)))))))
