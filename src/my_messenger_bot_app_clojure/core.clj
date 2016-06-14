(ns my-messenger-bot-app-clojure.core
  (:require [clojure.data.json :as json]
            [clojure.string :as string]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [org.httpkit.client :as http]
            [clj-http.client :as client]
            [my-messenger-bot-app-clojure.message :as message])
  (:use org.httpkit.server))

(def listening-port 3000)
(def config (load-file "config.clj"))
(def access-token (config :access-token))
(def verify-token (config :verify-token2))
(def messenger-url "https://graph.facebook.com/v2.6/me/messages")
(def headers {"Content-Type" "application/json"})
;(def options {:timeout 100 :headers headers :keepalive 1000}) ;; for http-kit
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
    (let [sender (-> messaging :sender :id)
          message (-> messaging :message :text)
          timestamp (messaging :timestamp)
          send-msg (message/create-text-message sender message)]
      ;resp (http/post (str messenger-url "?access_token=" access-token)
                      ;(assoc options :body (json/write-str send-msg)))
      ;(:body @resp)
      (client/post (str messenger-url "?access_token=" access-token)
                   {:body (json/write-str send-msg)
                    :content-type :json}))))

(defroutes main-routes
  (GET "/webhook" req (webhook-get-handler req))
  (POST "/webhook" req (webhook-post-handler req)))

(defn -main []
  (jetty/run-jetty main-routes {:port listening-port}))
  ;(run-server main-routes {:port listening-port}))
