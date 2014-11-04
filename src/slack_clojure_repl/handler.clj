(ns slack-clojure-repl.handler
  (:require [clojure.data.json :as json]
            [compojure.core :refer [defroutes POST]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [clj-http.client :as client]
            [clj-http.cookies :refer [cookie-store]]))

(def url "http://tryclj.com/eval.json")
(def slack-webhook (env :slack-webhook))
(def tryclj-cookies (cookie-store))

(defn tryclj [s]
  (let [response (client/get url {:query-params {"expr" s}
                                  :cookie-store tryclj-cookies
                                  :debug true})]
    (prn "tryclj response" response)
    (-> response
        :body
        json/read-str)))

(defn send-to-slack [payload]
  (client/post slack-webhook {:body (json/write-str payload)
                              :content-type :json}))

(defn response-channel [params]
  (let [channel-name (:channel_name params)]
    (if (or (= "privategroup" channel-name) (= "directmessage" channel-name))
      (str "@" (:user_name params))
      (str "#" channel-name))))

(defn response-text [params]
  (let [expr (:text params)
        response (tryclj expr)
        pre (str "```\n@" (:user_name params) "> " expr "\n")
        mid (if (get response "error")
              (get response "message")
              (get response "result"))
        post  "\n```"]
  (str pre mid post)))

(defroutes app-routes
  (POST "/slack" {:keys [params] :as request}
        (prn "request params" params)
        (send-to-slack {:channel (response-channel params)
                        :text (response-text params)})
        {:status 200})
  (route/not-found "Not Found"))

(def app (handler/site #'app-routes))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty app {:port port :join? false})))
