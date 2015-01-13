(defproject fondo "0.1.0-SNAPSHOT"
  :description "TODO"
  :url "TODO"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.7.0-alpha4"]
                 [org.clojure/data.codec "0.1.0"]
                 [com.amazonaws/aws-java-sdk "1.9.10" :exclusions [joda-time]]
                 [com.cognitect/transit-clj "0.8.259"]
                 [compojure "1.3.1"]
                 [ring/ring-core "1.3.1"
                  :exclusions [commons-codec]]
                 [ring/ring-jetty-adapter "1.3.1"]
                 [ring/ring-json "0.3.1"]
                 [cheshire "5.4.0"]
                 [com.taoensso/faraday "1.5.0" :exclusions [org.clojure/clojure]]
                 [clojurewerkz/urly "2.0.0-alpha5"]
                 [validata "0.1.8"]
                 [clj-http "1.0.1"]
                 [bencode "0.2.5"]
                 [pandect "0.4.1"]
                 [clj-aws-s3 "0.3.10" :exclusions [joda-time]]
                 [environ "1.0.0"]
                                        ;                 [clj-aws-auth "0.1.0"]
                 ]
  :plugins [[lein-ring "0.8.11"
             :exclusions [org.clojure/clojure
                          org.clojure/data.xml]]]
  :ring {:handler fondo.node/node-app}
  :profiles
  {:dev
   {:dependencies [[org.clojure/tools.namespace "0.2.7"]
                   [ring-server "0.3.1"]
                   [criterium "0.4.3"]]
    :source-paths ["dev"]}}
  :aliases {"init-db"
            ["run" "-m" "fondo.db/initialize-table"]})
