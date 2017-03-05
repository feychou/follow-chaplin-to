(ns facebook-example.bot
  (:gen-class)
  (:require [clojure.string :as s]
            [environ.core :refer [env]]
            [facebook-example.facebook :as fb]))

(def points (atom 0))
(def level (atom 0))

(def right-options [
  {:image "http://i.imgur.com/7wAzXRr.png" :text "Lion King"}
  {:image "http://i.imgur.com/S2cJ1da.png" :text "Game of Thrones"}
  {:image "http://i.imgur.com/UYfvj04.png" :text "Lord of the Rings"}
  {:image "http://i.imgur.com/rVKbxnn.png" :text "Harry Potter"}
  {:image "http://i.imgur.com/UtGfrWZ.png" :text "Forrest Gump"}
  {:image "http://i.imgur.com/9eWwuP2.png" :text "Avatar"}
  {:image "http://i.imgur.com/IsaOEoh.png" :text "James Bond Octopussy"}
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
      (= postback "GET_STARTED") (do
        (fb/send-message sender-id (fb/image-message "https://media.giphy.com/media/11zhPvvouIdyi4/giphy.gif"))
        (fb/send-message sender-id (fb/button-message "Hello movie lover, you may know me. I am Charlie Chaplin. Follow me to amazing places from movies past and guess the right one." [{:type "postback" :title (format "And action! %c" (int 127916)) :payload "ACTION"}])))
      (= postback "ACTION") (send-movie sender-id)
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
        (fb/send-message sender-id (fb/text-message (str "Your current movie score is " (swap! points (partial + 10)) (format " %c" (int 127916))))))
      :else (do
        (fb/send-message sender-id (fb/text-message "Sorry, you guessed wrong :("))
        (fb/send-message sender-id (fb/text-message (str "The correct answer was " (:text (get right-options @level)) ".")))
        (fb/send-message sender-id (fb/text-message (str "Your current movie score is " @points (format " %c" (int 127916))))))
    )
    (swap! level inc)
    (send-movie sender-id)))
