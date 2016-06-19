(ns my-messenger-bot-app-clojure.message
  (:require [clojure.string :as string]))

(def image-link {:h4 "https://firebasestorage.googleapis.com/v0/b/walter-bot-a2142.appspot.com/o/line-bot%2Fimage%2Fh4-logo%2F700.jpg?alt=media&token=04ff5f19-2d0c-470f-8581-396085fbb10d"
                 :emacs "https://firebasestorage.googleapis.com/v0/b/walter-bot-a2142.appspot.com/o/line-bot%2Fimage%2Femacs-logo%2Femacs_logo_large.png?alt=media&token=a8a55896-f703-4e10-adce-302a44f792c5"})
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
                              "http://groups.google.com/group/hackingthursday ( Google group )"
                              "http://www.facebook.com/groups/hackingday/ ( Facebook group )"
                              "https://www.meetup.com/hackingthursday/ ( Meetup )"]})

(defn get-suggest-message [text]
  (case text
    "how to get to h4" (string/join "\n" (send-text :h4-place))
    "h4 怎麼去" (string/join "\n" (send-text :h4-place))
    "h4 怎麼去?" (string/join "\n" (send-text :h4-place))
    "h4 怎麼走" (string/join "\n" (send-text :h4-place))
    "h4 怎麼走?" (string/join "\n" (send-text :h4-place))
    "what h4 people do" (string/join "\n" (send-text :h4-people-do))
    "h4 都做些什麼" (string/join "\n" (send-text :h4-people-do))
    "h4 beginning" (string/join "\n" (send-text :h4-beginning))
    "h4 由來" (string/join "\n" (send-text :h4-beginning))
    "contact us" (string/join "\n" (send-text :contact-us))
    "聯絡" (string/join "\n" (send-text :contact-us))
    "聯絡我們" (string/join "\n" (send-text :contact-us))
    "emacs" (send-text :emacs)
    "how are you" (send-text :i-am-fine)
    "你好嗎" (send-text :i-am-fine)
    "你好嗎?" (send-text :i-am-fine)
    (send-text :default)))

(defn create-text-message [sender text]
  (let [text (string/lower-case text)
        send-text (get-suggest-message text)]
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
               :buttons [{:type "web_url"
                          :url "http://www.hackingthursday.org/"
                          :title "H4 Website"}
                         {:type "web_url"
                          :title "H4 Meetup"
                          :url "http://www.meetup.com/hackingthursday/"
                          }]}
              }}})

(defn create-image-message [sender]
  {:recipient {:id sender}
   :message {:attachment
             {:type "image"
              :payload {:url
                        (image-link :h4)}}}})
