(ns fondo.core
  (:require
   [cheshire.core :as json]
   [compojure.core :refer [routes GET]]
   [compojure.route :as route]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.json :refer [wrap-json-response]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.util.response :refer [response]]))


;; FUTURE:
;; * use DynamoDB
;; * add authentication

