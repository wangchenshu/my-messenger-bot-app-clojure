(ns my-messenger-bot-app-clojure.core
  (:require [clojure.data.json :as json]
            [clojure.string :as string]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [org.httpkit.client :as http]
            [my-messenger-bot-app-clojure.message :as message]))

(def config (load-file "config.clj"))
(def access-token (config :access-token))
(def verify-token (config :verify-token2))
(def messenger-url "https://graph.facebook.com/v2.6/me/messages")
(def headers {"Content-Type" "application/json"})
(def options {:timeout 200 :headers headers})

(defn check-webhook [query-string]
  (if (= (query-string :hub.verify_token) verify-token)
    {:status 200
     :body (query-string :hub.challenge)}
    (println "Error, wrong validation token")))

(defn webhook-get-handler [req]
  (->> (string/split (req :query-string) #"&")
       (map #(string/split % #"="))
       (map (fn [[k v]] [(keyword k) v]))
       (into {})
       (check-webhook)))

(defn webhook-post-handler [req]
  (let [req (json/read-str (slurp(req :body)) :key-fn keyword)
        messaging (->> (req :entry)
                        first
                        :messaging
                        first)]
    (let [sender (get-in messaging [:sender :id])
          message (get-in messaging [:message :text])
          timestamp (messaging :timestamp)
          resp (http/post (str messenger-url "?access_token=" access-token)
                          (assoc options :body (json/write-str (message/create-text-message sender message))))]
      (if (< (- (System/currentTimeMillis) timestamp) 10)
        (println "Response 's status: " (:status @resp))
        (println "Get the older message!")))))

(defroutes main-routes
  (GET "/webhook" req (webhook-get-handler req))
  (POST "/webhook" req (webhook-post-handler req)))

(defn -main []
  (jetty/run-jetty main-routes {:port 3000}))
