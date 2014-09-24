(ns slack-clojure-repl.handler
  (:require [clojure.data.json :as json]
            [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [clj-http.client :as client]))

(def url "http://tryclj.com/eval.json")
(def slack-webhook (env :slack-webhook))

(defn tryclj [s]
  (-> (client/get url {:query-params {"expr" s}})
      :body
      json/read-str))

(defn prettify [expr response]
  (let [pre (str "```\n> " expr "\n")
        mid (if (get response "error")
              (get response "message")
              (get response "result"))
        post  "\n```"]
  (str pre mid post)))

(defn send-to-slack [payload]
  (client/post slack-webhook {:body (json/write-str payload)
                              :content-type :json}))

(defroutes app-routes
  (POST "/slack" {:keys [params] :as request}
        (prn params)
        (let [expr (:text params)
              response-text (prettify expr (tryclj expr))]
          (send-to-slack {:text response-text})
          {:status 200}))
  (route/not-found "Not Found"))

(def app (handler/site #'app-routes))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty app {:port port :join? false})))
