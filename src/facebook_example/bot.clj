(ns facebook-example.bot
  (:gen-class)
  (:require [clojure.string :as s]
            [environ.core :refer [env]]
            [facebook-example.facebook :as fb]))

(def points (atom 0))
(def level (atom 0))

(def right-options [
  {:image "https://s-media-cache-ak0.pinimg.com/736x/41/58/e6/4158e6c6c5204c458574b8de4c9bdbc3.jpg" :text "Lion King"}
  {:image "http://images6.fanpop.com/image/polls/1160000/1160581_1356712201319_full.png?v=1356712261" :text "Game of Thrones"}
  {:image "http://www.doc.govt.nz/global/images/places/lord-of-the-rings-locations/mt-sunday-edoras-1000.jpg" :text "Lord of the Rings"}
  {:image "http://www.testedich.at/quiz38/picture/pic_1443842765_1.jpg" :text "Harry Potter"}
  {:image "https://www.google.at/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&ved=0ahUKEwidsoT6gL_SAhVGxRQKHSMWBKMQjBwIBA&url=https%3A%2F%2Fstatic1.squarespace.com%2Fstatic%2F5266b2bde4b08e763cc132d2%2Ft%2F530673bfe4b09fae2c91bbf8%2F1392931776486%2F&psig=AFQjCNFAaTJ28A3I8NDv5B3R2h5pqV6ALg&ust=1488791025505415" :text "Forrest Gump"}
  {:image "http://soyuz-pisatelei.ru/_fr/100/0420333.jpg" :text "Avatar"}
  {:image "https://s-media-cache-ak0.pinimg.com/originals/21/4c/fb/214cfbfd41c1196747c61cceda5678de.jpg" :text "James Bond Octopussy"}
])

(def wrong-options [
 [
    {:content_type "text" :title "Out of Africa" :payload "WRONG_ANSWER"}
    {:content_type "text" :title "Wild Life" :payload "WRONG_ANSWER"}
 ]
 [
    {:content_type "text" :title "The Great Wall" :payload "WRONG_ANSWER"}
    {:content_type "text" :title "The Wizard of Oz" :payload "WRONG_ANSWER"}
 ]
 [
    {:content_type "text" :title "The Hobbit" :payload "WRONG_ANSWER"}
    {:content_type "text" :title "The Dark Knight" :payload "WRONG_ANSWER"}
 ]
 [
    {:content_type "text" :title "The Chronicles of Narnia" :payload "WRONG_ANSWER"}
    {:content_type "text" :title "King Arthur" :payload "WRONG_ANSWER"}
 ]
 [
    {:content_type "text" :title "One upon a time in the West" :payload "WRONG_ANSWER"}
    {:content_type "text" :title "National Lampoon's Vacation" :payload "WRONG_ANSWER"}
 ]
 [
    {:content_type "text" :title "The Avengers" :payload "WRONG_ANSWER"}
    {:content_type "text" :title "Gravity" :payload "WRONG_ANSWER"}
 ]
 [
    {:content_type "text" :title "James Bond Casino Royal" :payload "WRONG_ANSWER"}
    {:content_type "text" :title "James Bond Goldfinger" :payload "WRONG_ANSWER"}
 ]
])

(defn send-movie [sender-id]
  (let [right-answer (get right-options @level)]
    (fb/send-message sender-id (fb/image-message (:image right-answer)))
    (fb/send-message sender-id
      (fb/quick-reply-message "Which movie is this scene from?"
        (shuffle (conj (get wrong-options @level) {:content_type "text" :title (:text right-answer) :payload "RIGHT_ANSWER"})))))
)

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
      (s/includes? (s/lower-case message-text) "movie") (send-movie sender-id)
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
    (cond
      (= quick-reply-payload "RIGHT_ANSWER") (do
        (fb/send-message sender-id (fb/text-message (str "Well done, congratulations!" (format " %c" (int 127881)))))
        (fb/send-message sender-id (fb/text-message (str "Your current movie score is: " (swap! points (partial + 10)) (format " %c" (int 127916))))))
      :else (do
        (fb/send-message sender-id (fb/text-message "Sorry, the answer is not correct :("))
        (fb/send-message sender-id (fb/text-message (str "Your current movie score is: " @points (format " %c" (int 127916))))))
    )
    (swap! level inc)
    (send-movie sender-id)))
