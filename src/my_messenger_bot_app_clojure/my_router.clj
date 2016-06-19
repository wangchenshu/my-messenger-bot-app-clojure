(ns my-messenger-bot-app-clojure.my-router
  (:require [clojure.data.json :as json]
            [my-messenger-bot-app-clojure.message :as message]
            [my-messenger-bot-app-clojure.my-handler :as my-handler]))

(defn route [req]
  (let [req (json/read-str (slurp(req :body)) :key-fn keyword)
        messaging (->> (req :entry) first :messaging first)
        sender (-> messaging :sender :id)
        message (-> messaging :message :text)
        timestamp (messaging :timestamp)
        send-msg (message/create-text-message sender message)]    

    (println messaging)
    (my-handler/send-text-message sender send-msg)))
