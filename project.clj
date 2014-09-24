(defproject slack-clojure-repl "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.5"]
                 [compojure "1.1.8"]
                 [clj-http "1.0.0"]]
  :plugins [[lein-ring "0.8.11"]]
  :ring {:handler slack-clojure-repl.handler/app}
  :uberjar-name "slack-clojure-repl-standalone.jar"
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
