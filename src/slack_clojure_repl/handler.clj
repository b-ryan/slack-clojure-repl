(ns slack-clojure-repl.handler
  (:require [clojure.data.json :as json]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [clj-http.client :as client]))

(defroutes app-routes
  (POST "/slack" {:keys [params] :as request}
        {:status 200
         :content-type "text/plain"
         :body (str "```\n" (pr-str (load-string (:text params))) "\n```")})
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
