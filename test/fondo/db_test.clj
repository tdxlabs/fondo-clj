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

(use-fixtures :once table-setup)

(deftest validate-value
  (testing "correctly validates values"
    (let [valid1 {:name "Name"
                  :uri  "URI"}
          valid2 {:name "Name"
                  :uri  "URI"
                  :description "Description"}
          invalid1 {:uri "URI"}
          invalid2 {:name "Name"}]
      (is (v/valid? valid1 db/value-validations))
      (is (v/valid? valid2 db/value-validations))
      (is (not (v/valid? invalid1 db/value-validations)))
      (is (not (v/valid? invalid2 db/value-validations))))))

(deftest put-and-get-values
  (testing "stores and retrieves values"
    (let [id 12345
          val {:name "Test"
               :uri "URI"}]
      (db/put-value id val :test-values)
      (let [result (db/get-value id :test-values)
            missing (db/get-value 67890 :test-values)]
        (is (= id (:id result)))
        (is (= val (:value result)))
        (is (= {:error :not-found} missing))))))
