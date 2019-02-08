(ns memory-puzzle.render
  (:require [quil.core :as q]
            [memory-puzzle.game :as game]))

(def screen-width 500)
(def screen-height 321)
(def canvas-width 400)

(defn- ms->time [ms]
  (let [all-secs (int (/ ms 1000))
        mins (int (/ all-secs 60))
        secs (rem all-secs 60)]
    {:m mins :s secs}))

(defn- format-time [time]
  (let [{:keys [m s]} time]
    (str (if (< m 10) "0" "")
         m ":"
         (if (< s 10) "0" "")
         s)))

(defn- render-timer [state]
  (q/translate canvas-width 0)
  (q/text-size 30)
  (q/fill 255)
  (let [time-str (-> (max 0 (- (:end-time state) (q/millis)))
                     (ms->time)
                     (format-time))]
    (q/text time-str 5 30)))

(defn- render-cards [state]
  (doseq [[_ card] (:cards state)
          :let [color (if (:flipped card) (:color card) [100])
                [x y] (:position card)]]
    (apply q/fill color)
    (q/rect x y game/card-size game/card-size)))

(defn- render-menu [state]
  (q/text-align :center :center)
  (q/text-size 35)
  (q/fill 0 204 0)
  (q/text "Click to start" 245 130))

(defn- render-play [state]
  (render-cards state)
  (render-timer state))

(defn- render-game-over [state]
  (q/text-align :center :center)
  (q/text-size 35)
  (q/fill 255 0 0)
  (q/text "Game Over" 245 170))

(defn- render-win [state]
  (q/text-align :center :center)
  (q/text-size 35)
  (q/fill 255)
  (q/text "You win!" 245 130))

(defn render-state [state]
  (q/background 30 30 30)
  (case (:mode state)
    :menu (render-menu state)
    :play (render-play state)
    :game-over (render-game-over state)
    :win (render-win state)))
