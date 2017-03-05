(ns facebook-example.bot
  (:gen-class)
  (:require [clojure.string :as s]
            [environ.core :refer [env]]
            [facebook-example.facebook :as fb]))

(def points (atom 0))
(def level (atom 0))

;(def movie-guesses [
;  {:image "" id: "" :text ""}
;  {:image "" id: "" :text ""}
;])
;
;(def random-options [
;  "",
;  "",
;  "",
;  "",
;  "",
;  ""
;])

(defn on-message [payload]
  (println "on-message payload:")
  (println payload)
  (let [sender-id (get-in payload [:sender :id])
        recipient-id (get-in payload [:recipient :id])
        time-of-message (get-in payload [:timestamp])
        message-text (get-in payload [:message :text])]
    (cond
      (s/includes? (s/lower-case message-text) "help") (fb/send-message sender-id (fb/text-message "Hi there, happy to help :)"))
      (s/includes? (s/lower-case message-text) "image") (fb/send-message sender-id (fb/image-message "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/M101_hires_STScI-PRC2006-10a.jpg/1280px-M101_hires_STScI-PRC2006-10a.jpg"))
      (s/includes? (s/lower-case message-text) "movie") (fb/send-message sender-id
        (fb/quick-reply-message "HELLO"
          [
            {:content_type "text" :title "hello back" :payload "HELLO_BACK"}
            {:content_type "text" :title "hello to you" :payload "HELLO_TO_YOU"}
          ]))
      ; If no rules apply echo the user's message-text input
      :else (fb/send-message sender-id (fb/text-message message-text)))))

(defn on-postback [payload]
  (println "on-postback payload:")
  (println payload)
  (let [sender-id (get-in payload [:sender :id])
        recipient-id (get-in payload [:recipient :id])
        time-of-message (get-in payload [:timestamp])
        postback (get-in payload [:postback :payload])
        referral (get-in payload [:postback :referral :ref])]
    (cond
      (= postback "GET_STARTED") (fb/send-message sender-id (fb/text-message "Welcome =)"))
      :else (fb/send-message sender-id (fb/text-message "Sorry, I don't know how to handle that postback")))))

(defn on-attachments [payload]
  (println "on-attachment payload:")
  (println payload)
  (let [sender-id (get-in payload [:sender :id])
        recipient-id (get-in payload [:recipient :id])
        time-of-message (get-in payload [:timestamp])
        attachments (get-in payload [:message :attachments])]
    (fb/send-message sender-id (fb/text-message "Thanks for your attachments :)"))))

(defn on-attachments [payload]
  (println "on-attachment payload:")
  (println payload)
  (let [sender-id (get-in payload [:sender :id])
        recipient-id (get-in payload [:recipient :id])
        time-of-message (get-in payload [:timestamp])
        attachments (get-in payload [:message :attachments])]
    (fb/send-message sender-id (fb/text-message "Thanks for your attachments :)"))))

(defn on-quickreply [payload]
  (println "on-quickreply payload:")
  (println payload)
  (let [sender-id (get-in payload [:sender :id])
        recipient-id (get-in payload [:recipient :id])
        time-of-message (get-in payload [:timestamp])
        message (get-in payload [:message])
        quick-reply (get-in payload [:message :quick_reply])
        quick-reply-payload (get-in payload [:message :quick_reply :payload])]
    (fb/send-message sender-id (fb/text-message (str "Thanks for your guess!" (swap! points (partial + 10)))))))
