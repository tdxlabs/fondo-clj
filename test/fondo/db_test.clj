(ns fondo.db-test
  (:require
      [clojure.test :refer :all]
      [fondo.db :as db]
      [fondo.encode :refer [encode-and-hash val-with-data]]
      [fondo.util :refer [run-echo-app]]
      [taoensso.faraday :as far]
      [validata.core :as v]))

(def db
  {:access-key "<AWS_DYNAMODB_ACCESS_KEY>"
   :secret-key "<AWS_DYNAMODB_SECRET_KEY>"
   :endpoint "http://localhost:8000"})

(def table-name :test-values)
(def port 9876)
(def echo-server (str "http://localhost:" port))

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

(defn run-echo-server
  [f]
  (let [server-thread (Thread. #(run-echo-app port))]
    (.start server-thread)
    (f)
    (.stop server-thread)))

(use-fixtures :once table-setup run-echo-server)

(deftest validate-value
  (testing "correctly validates values"
    (let [valid1 {:name "Name"
                  :uri  echo-server}
          valid2 {:name "Name"
                  :uri  echo-server
                  :description "Description"}
          invalid1 {:uri echo-server}
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
               :uri echo-server}
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
               :uri echo-server}
          id (encode-and-hash (val-with-data val))]
      (put-value id val)
      (let [failed (put-value id val)]
        (is (= ["Value with that ID exists"] (get-in failed [:errors :vid])))))))

(deftest timestamp-ranges
  (testing "returns list of new IDs since timestamp"
    (let [val1 {:name "Range Test 1"
                :uri echo-server}
          id1  (encode-and-hash (val-with-data val1))
          res   (put-value id1 val1)
          timestamp (:timestamp (far/get-item db table-name {:vid id1}))
          val2 {:name "Range Test 2"
                :uri  echo-server}
          id2  (encode-and-hash (val-with-data val2))
          val3 {:name "Range Test 3"
                :uri echo-server}
          id3  (encode-and-hash (val-with-data val3))]
      (put-value id2 val2)
      (put-value id3 val3)
      (let [results (db/get-since db table-name timestamp)]
        (is (= 2 (count results)))
        (is (= (list id2 id3) (map :vid results)))))))
