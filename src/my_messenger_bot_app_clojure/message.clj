(ns my-messenger-bot-app-clojure.message
  (:require [clojure.string :as string]))

(def image-link {:h4 "https://firebasestorage.googleapis.com/v0/b/walter-bot-a2142.appspot.com/o/line-bot%2Fimage%2Fh4-logo%2F700.jpg?alt=media&token=04ff5f19-2d0c-470f-8581-396085fbb10d"
                 :emacs "https://firebasestorage.googleapis.com/v0/b/walter-bot-a2142.appspot.com/o/line-bot%2Fimage%2Femacs-logo%2Femacs_logo_large.png?alt=media&token=a8a55896-f703-4e10-adce-302a44f792c5"
                 :baobao "https://firebasestorage.googleapis.com/v0/b/walter-bot-a2142.appspot.com/o/line-bot%2Fimage%2Fother%2Fiyiy.jpg?alt=media&token=1d0dfd87-d61a-48d5-8ea0-0bd86c9b9458"
                 :fb-good "https://firebasestorage.googleapis.com/v0/b/walter-bot-a2142.appspot.com/o/line-bot%2Fimage%2Fother%2Ffb-good.png?alt=media&token=52fde993-26a0-4872-bbc7-eb78933cf470"})
(def h4-web "http://www.hackingthursday.org/")
(def h4-meetup "http://www.meetup.com/hackingthursday/")
(def h4-fb "http://www.facebook.com/groups/hackingday/")
(def send-text {:default "Welcome to h4!"
                :h4 "Welcome to h4!"
                :emacs "Welcome to Emacs Taiwan!"
                :how-are-you-today "How are you today?"
                :i-am-fine "Fine, how do you do."
                :h4-place '["餐廳：田中園光華店"
                            "地址：台北市中正區臨沂街 1 號"
                            "(捷運忠孝新生站一號出口直走第一個路口右轉)"
                            "時間：7:30pm ~ 10:00pm"
                            "Restaurant : 田中園 (Tian Jung Yuan)"
                            ""
                            "Venue : No. 1, Linyi St, Zhongzheng District, Taipei City"
                            "(MRT JungXiao Xingshen Station Exit 1)"
                            "Time : 7:30pm ~ 10:00pm"]
                :h4-people-do '["1. 討論 web, network, programming, system, blablah…."
                                "2. 交流系統工具 & 使用技巧"
                                "3. 八卦"]
                :h4-beginning '["Hacking Thursday 是由幾位居住於台北地區的自由軟體/開放原碼開發者所發起，"
                                "每週四晚上會於特定咖啡店聚會。以非會議形式、交換並實做各自提出的想法，"
                                "輕鬆的會議過程以禮貌、謙遜與尊重的互信態度接納並鼓勵概念發想、發起新計畫、"
                                "並從開發者的協同開發與經驗分享中互相學習。"]
                :contact-us '["除了實體聚會外，我們使用 Google group / Facebook group 做為大家的溝通聯絡管道。"
                              "聊天，討論，及聚會通告都會在這裡發佈。如果您對我們的聚會有興趣，隨時都歡迎您加入/訂閱我們的討論區，和我們交流！！"
                              ""
                              "FB: http://www.facebook.com/groups/hackingday/"
                              "Meetup: https://www.meetup.com/hackingthursday/"
                              "Google Group: http://groups.google.com/group/hackingthursday"]
                :registed " 您好, 已完成報到手續, 謝謝。"
                :good-d-ya "好的呀"
                :iyiy "搖搖照騙"})

(defn get-suggest-message [text]
  (cond
    (or (re-find #"how are you" text)
        (or (re-find #"好" text)
            (and (re-find #"你" text)
                 (re-find #"您" text)))) (send-text :i-am-fine)
    (or (re-find #"contact" text)
        (re-find #"找" text)
        (re-find #"聯絡" text)
        (re-find #"連絡" text)) (string/join "\n" (send-text :contact-us))
    (or (re-find #"由來" text)
        (re-find #"開始" text)
        (re-find #"beginning" text)) (string/join "\n" (send-text :h4-beginning))
    (or (re-find #"go" text)
        (re-find #"to" text)
        (re-find #"去" text)
        (re-find #"走" text)) (string/join "\n" (send-text :h4-place))
    (or (re-find #"do" text)
        (re-find #"做" text)) (string/join "\n" (send-text :h4-people-do))
    (or (re-find #"register" text)
        (re-find #"報到" text)
        (re-find #"簽到" text)) (send-text :register)
    (re-find #"已給到" text) (send-text :good-d-ya)
    (re-find #"抱抱" text) (send-text :iyiy)
    (re-find #"web" text) h4-web
    (re-find #"meetup" text) h4-meetup
    (re-find #"fb" text) h4-fb
    :else (send-text :default)))

(defn create-text-message [sender text]
  (let [text (string/lower-case text)
        send-text (get-suggest-message text)]
    {:recipient {:id sender}
     :message {:text send-text}}))

(defn create-resgisted-message [sender user-name text]
  (let [send-text (str user-name (send-text :registed))]
    {:recipient {:id sender}
     :message {:text send-text}}))

(defn say-hello [sender user-name]
  {:recipient {:id sender}
   :message {:text (str "Hello, " user-name " :")}})

(defn create-butten-template-message [sender]
  {:recipient {:id sender}
   :message {:attachment
             {:type "template"
              :payload
              {:template_type "button"
               :text "What do you want to do next?"
               :buttons [{:type "web_url" :url h4-web :title "Website"}
                         {:type "web_url" :title "FB" :url h4-fb}
                         {:type "web_url" :title "Meetup" :url h4-meetup}]
               }}}})

(defn create-image-message [sender img-key]
  {:recipient {:id sender}
   :message {:attachment
             {:type "image"
              :payload {:url (image-link img-key)}}}})

(defn create-image-message-url [sender url]
  {:recipient {:id sender}
   :message {:attachment
             {:type "image"
              :payload {:url url}}}})

(defn create-baobao-image-message [sender]
  {:recipient {:id sender}
   :message {:attachment
             {:type "image"
              :payload {:url (image-link :baobao)}}}})

(defn wanna-register? [message]
  (or (re-find #"register" message)
      (re-find #"報到" message)
      (re-find #"簽到" message)))

(defn wanna-baobao? [message]
  (or (re-find #"baobao" message)
      (re-find #"抱抱" message)))
