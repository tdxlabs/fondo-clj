(ns user
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer [javadoc]]
   [clojure.pprint :refer [pprint]]
   [clojure.reflect :refer [reflect]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [clojure.set :as set]
   [clojure.string :as str]
   [clojure.test :as test]
   [clojure.tools.namespace.repl :refer [refresh refresh-all]]
   [environ.core :refer [env]]
   [fondo.client :as client]
   [fondo.db :as db]
   [fondo.node :as node]
   [ring.server.standalone :refer :all]))

(def system
  "A Var containing an object representing the application under
  development."
  {:db           {:access-key (env :dynamo-access-key)
                  :secret-key (env :dynamo-secret-key)
                  :endpoint   (env :dynamo-endpoint)}
   :table-name   (env :dynamo-table-name)
   :zone-id      (env :zone-id)
   :port         (env :node-port)
   :s3           {:cred {:access-key (env :s3-access-key)
                         :secret-key (env :s3-secret-key)}
                  :bucket-name (env :s3-bucket-name)}})

(defn init
  "Creates and initializes the system under development in the Var
  #'system."
  []
  (db/ensure-table (:db system)
                   (:table-name system)))

(defn start
  "Starts the system running, updates the Var #'system."
  []
  (let [server (serve (node/node-app (:db system)
                                     (:table-name system)
                                     (:zone-id system))
                      {:port (:port system)})]
    (alter-var-root #'system assoc :server server
                                   :node   {:url (str "http://localhost:"
                                                      (:port system))})))

(defn stop
  "Stops the system if it is currently running, updates the Var
  #'system."
  []
  (.stop (:server system))
  (alter-var-root #'system dissoc :server))

(defn go
  "Initializes and starts the system running."
  []
  (init)
  (start)
  :ready)

(defn reset
  "Stops the system, reloads modified source files, and restarts it."
  []
  (stop)
  (refresh :after 'user/go))

;; Convenience functions

(defn get-value
  [id]
  (client/get-value (:node system) id))

(defn put-value
  [val]
  (client/put-value (:node system) val))
