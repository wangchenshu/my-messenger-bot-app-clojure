(ns my-messenger-bot-app-clojure.my-router
  (:require [clojure.data.json :as json]
            [my-messenger-bot-app-clojure.message :as message]
            [my-messenger-bot-app-clojure.my-handler :as my-handler]))

(defn req-messaging [req]
  (->> (req :entry) first :messaging first))

(defn attachments-payload-url [message]
  (->> (message :attachments) first :payload :url))

(defn messaging-sender-id [messaging]
  (-> messaging :sender :id))

(defn route [req]
  (let [req (json/read-str (slurp(req :body)) :key-fn keyword)
        messaging (req-messaging req)
        sender (messaging-sender-id messaging)
        message (messaging :message)
        text (message :text)
        timestamp (messaging :timestamp)]
    (println message)

    (if (contains? message :attachments)
        (let [img-url (attachments-payload-url message)]
          (my-handler/send-image-message-url sender img-url))
        (try
          (cond
          (message/wanna-register? text)
          (my-handler/send-registed-message sender text)
          (message/wanna-baobao? text)
          (my-handler/send-image-message sender :baobao)
          :else (my-handler/send-text-message2 sender text))
          (catch Exception e (str "caught exception: " (.getMessage e)))))))
