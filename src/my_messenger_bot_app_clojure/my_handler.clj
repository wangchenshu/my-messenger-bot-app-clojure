(ns my-messenger-bot-app-clojure.my-handler
  (:require[org.httpkit.client :as http]
           [clj-http.client :as client]
           [clojure.data.json :as json]
           [my-messenger-bot-app-clojure.message :as message]))

(def config (load-file "config.clj"))
(def access-token (config :access-token))
(def base-url (config :base-url)) 
(def messenger-url (config :messenger-url))
(def text-msg-url (str messenger-url "?access_token=" access-token))
(def headers {"Content-Type" "application/json"})
(def options {:timeout 500 :headers headers})

(def resp-handler
  (fn [{:keys [status headers body error]}]
    (if error
      (println "Failed, exception is " error)
      (println "Response Status:" status))))

(defn user-profile-url [sender]
  (str base-url
       sender
       "?fields=first_name,last_name,locale,profile_pic,timezone,gender"
       "&access_token=" access-token))

(defn get-body [msg]
  (assoc options :body (json/write-str msg)))

(defn get-user-name [body]
  (str (body :first_name) " " (body :last_name)))

(defn get-user-profile [sender]
  (let [user-profile-url (user-profile-url sender)
        user-profile-resp (http/get user-profile-url options)]
    user-profile-resp))

(defn send-button-message [sender]
  (let [send-butten-msg (message/create-butten-template-message sender)
        send-butten-msg-resp (http/post text-msg-url (get-body send-butten-msg))]
    send-butten-msg-resp))

(defn send-text-message [sender text]
  (let [send-msg (message/create-text-message sender text)
        user-profile-resp (get-user-profile sender)
        body (json/read-str (:body @user-profile-resp) :key-fn keyword)
        user-name (get-user-name body)
        send-msg-resp (http/post text-msg-url (get-body send-msg))
        send-butten-msg-resp (send-button-message sender)]
    (println user-name)
    (println (:body @user-profile-resp))))

(defn send-registed-message [sender text]
  (let [user-profile-resp (get-user-profile sender)
        body (json/read-str (:body @user-profile-resp) :key-fn keyword)
        user-name (get-user-name body)
        send-msg (message/create-resgisted-message sender user-name text)
        send-msg-resp (http/post text-msg-url (get-body send-msg))]
    (println send-msg)
    (println (:body @user-profile-resp))))

(defn send-image-message [sender img-key]
  (let [send-image-msg (message/create-image-message sender img-key)
        send-image-resp (http/post text-msg-url (get-body send-image-msg))]
    send-image-resp))

(defn send-image-message-url [sender url]
  (let [send-image-msg (message/create-image-message-url sender url)
        send-image-resp (http/post text-msg-url (get-body send-image-msg))]
    send-image-resp))
