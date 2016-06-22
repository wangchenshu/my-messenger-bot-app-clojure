(ns my-messenger-bot-app-clojure.my-router
  (:require [clojure.data.json :as json]
            [my-messenger-bot-app-clojure.message :as message]
            [my-messenger-bot-app-clojure.my-handler :as my-handler]))

(defn wanna-register? [message]
  (or (re-find #"register" message)
      (re-find #"報到" message)
      (re-find #"簽到" message)))

(defn wanna-baobao? [message]
  (or (re-find #"baobao" message)
      (re-find #"抱抱" message)))

(defn route [req]
  (let [req (json/read-str (slurp(req :body)) :key-fn keyword)
        messaging (->> (req :entry) first :messaging first)
        sender (-> messaging :sender :id)
        message (-> messaging :message :text)
        timestamp (messaging :timestamp)]
    (println messaging)

    (cond
      (wanna-register? message)
      (my-handler/send-registed-message sender message)
      (wanna-baobao? message)
      (my-handler/send-baobao-message sender message)
      :else (my-handler/send-text-message sender message))))
