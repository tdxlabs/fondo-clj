(ns fondo.db-test
  (:require [clojure.test :refer :all]
            [fondo.db :as db]
            [validata.core :as v]))

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
