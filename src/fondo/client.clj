(ns fondo.client
  (:require
   [clj-http.client :as client]
   [clojurewerkz.urly.core :as url]))

(defn get-value
  "Retrieve a value from a Fondo node. `node` should be a map
   with a key `:url` denoting its location."
  [node id]
  (let [url  (url/url-like (:url node))
        path (.mutatePath url (str "value/" id))
        resp (:body (client/get (str path) {:as :json
                                            :coerce :always
                                            :throw-exceptions false}))]
    resp))
