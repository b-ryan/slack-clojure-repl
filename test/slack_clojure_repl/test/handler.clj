(ns slack-clojure-repl.test.handler
  (:require [clojure.test :refer :all]
            [slack-clojure-repl.handler :refer [app tryclj send-to-slack]]
            [ring.mock.request :as mock]))

(deftest test-app
  (testing "slack route"
    (with-redefs [tryclj (constantly {"error" true
                                      "message" "a message"})
                  send-to-slack #(is (= % {:text "```\n> 2\na message\n```"}))]
      (let [response (app (mock/request :post "/slack?text=2"))]
        (is (= (:status response) 200))
        ))
    (with-redefs [tryclj (constantly {"result" "the result"})
                  send-to-slack #(is (= % {:text "```\n> xyz\nthe result\n```"}))]
      (let [response (app (mock/request :post "/slack?text=xyz"))]
        (is (= (:status response) 200))
        )))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
