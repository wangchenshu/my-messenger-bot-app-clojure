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

(def user-profile-url
  (fn [sender]
    (str base-url
         sender
         "?fields=first_name,last_name,locale,timezone,gender"
         "&access_token=" access-token)))

(def get-body
  (fn [msg]
    (assoc options :body (json/write-str msg))))

(defn send-text-message [sender send-msg]
  (let [user-profile-url (user-profile-url sender)
        user-profile-resp (http/get user-profile-url options)]
    (let [body (json/read-str (:body @user-profile-resp) :key-fn keyword)
          user-name (str (body :first_name) " " (body :last_name))
          ;say-hello (message/say-hello sender user-name)
          ;say-hello-resp (http/post text-msg-url (get-body say-hello))
          send-msg-resp (http/post text-msg-url (get-body send-msg))
          send-butten-msg (message/create-butten-template-message sender)
          send-butten-msg-resp (http/post text-msg-url (get-body send-butten-msg))]

      (println (:body @user-profile-resp)))))

(defn send-butten-message [sender options]
  (let [send-butten-message-resp (http/post text-msg-url options)]
    (:body @send-butten-message-resp)))
