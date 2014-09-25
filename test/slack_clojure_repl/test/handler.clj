(ns slack-clojure-repl.test.handler
  (:require [clojure.test :refer :all]
            [slack-clojure-repl.handler :refer [app tryclj send-to-slack] :as h]
            [ring.mock.request :as mock]))

(deftest test-response-channel
  (testing "response channel"
    (is (= (h/response-channel {:channel_name "privategroup"
                                :user_name "buck"}) "@buck"))
    (is (= (h/response-channel {:channel_name "directmessage"
                                :user_name "buck"}) "@buck"))
    (is (= (h/response-channel {:channel_name "general"}) "#general"))
    ))

(deftest test-app
  (testing "slack route"
    (with-redefs [tryclj (constantly {"error" true
                                      "message" "a message"})
                  send-to-slack #(is (= % {:channel "#"
                                           :text "```\n@buck> 2\na message\n```"}))]
      (let [response (app (mock/request :post "/slack?text=2&user_name=buck"))]
        (is (= (:status response) 200))
        ))
    (with-redefs [tryclj (constantly {"result" "the result"})
                  send-to-slack #(is (= % {:channel "#"
                                           :text "```\n@buck> xyz\nthe result\n```"}))]
      (let [response (app (mock/request :post "/slack?text=xyz&user_name=buck"))]
        (is (= (:status response) 200))
        )))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
