(ns api
  (:require [babashka.http-client :as http]
            [cheshire.core :as json]))

(def TOKEN "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJodHRwOi8vMC4wLjAuMDo4MDgwL2hlbGxvIiwiaXNzIjoiaHR0cDovLzAuMC4wLjA6ODA4MC8iLCJ1c2VybmFtZSI6ImFkbWluIiwiZXhwIjoxNzE4NDkzOTY3fQ.NpUq4fCJNgDpDJAROcyyfni1J_vctQqoqG253MimYew")

(defn format-response
  [response]
  (let [json-values (json/parse-string (:body response) true)]
    (println (json/generate-string json-values {:pretty true}))))

(defn user
  [name password]
  (json/encode {:username name, :password password}))

(comment

  (format-response
    (http/get
      "http://127.0.0.1:8080/flow"
      {:throw false}))

  (format-response
    (http/post
      "http://127.0.0.1:8080/login"
      {:headers {:content-type "application/json"}
       :body (user "admin" "abc123")
       :throw false}))

  (format-response
    (http/get
      "http://127.0.0.1:8080/project"
      {:header {:Authorization (str "Bearer " TOKEN)}
       :throw false}))
  )
