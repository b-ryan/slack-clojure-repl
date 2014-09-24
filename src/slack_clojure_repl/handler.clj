(ns slack-clojure-repl.handler
  (:require [clojure.data.json :as json]
            [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [clj-http.client :as client]))

(def url "http://tryclj.com/eval.json")

(defn eval-string [s]
  (-> (client/get url {:query-params {"expr" s}})
      :body
      json/read-str
      (get "result")))

(defn prettify [expr result]
  (str "```\n" expr "\n=> " result "\n```"))

(defroutes app-routes
  (POST "/slack" {:keys [params] :as request}
        (let [expr (:text params)]
          {:status 200
           :content-type "text/plain"
           :body (prettify expr (eval-string expr))}))
  (route/not-found "Not Found"))

(def app (handler/site #'app-routes))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty app {:port port :join? false})))
