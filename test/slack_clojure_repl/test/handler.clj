(ns slack-clojure-repl.test.handler
  (:require [clojure.test :refer :all]
            [slack-clojure-repl.handler :refer [app tryclj]]
            [ring.mock.request :as mock]))

(deftest test-app
  (testing "slack route"
    (with-redefs [tryclj (constantly {"error" true
                                      "message" "a message"})]
      (let [response (app (mock/request :post "/slack?text=2"))]
        (is (= (:status response) 200))
        (is (= (:body response) "```\n> 2\na message\n```"))))
    (with-redefs [tryclj (constantly {"result" "the result"})]
      (let [response (app (mock/request :post "/slack?text=xyz"))]
        (is (= (:status response) 200))
        (is (= (:body response) "```\n> xyz\nthe result\n```")))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
