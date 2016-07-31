(ns my-messenger-bot-app-clojure.core
  (:require [clojure.string :as string]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [my-messenger-bot-app-clojure.my-router :as my-router])
  (:use org.httpkit.server)
  (:gen-class))

(def config (load-file "config.clj"))
(def port (config :port))
(def verify-token (config :verify-token2))
(def ok 200)

(defn check-webhook [query-string]
  (if (= (query-string :hub.verify_token) verify-token)
    {:status ok
     :body (query-string :hub.challenge)}
    (println "Error, wrong validation token")))

(defn webhook-get-handler [req]
  (->> (string/split (req :query-string) #"&")
       (map #(string/split % #"="))
       (map (fn [[k v]] [(keyword k) v]))
       (into {})
       (check-webhook)))

(defn webhook-post-handler [req]
  (my-router/route req)
  {:status ok})

(defroutes main-routes
  (GET "/webhook" req (webhook-get-handler req))
  (POST "/webhook" req (webhook-post-handler req)))

(defn -main [& args]
  ;(jetty/run-jetty main-routes {:port port}))
  (run-server main-routes {:port port}))
