(ns memory-puzzle.game
  (:require [quil.core :as q]
            [memory-puzzle.render :as render]))

(def card-size 80)

(def ^{:private true} rows 4)
(def ^{:private true} cols 4)

(def ^{:private true} stop-time (* 1 60 1000)) ;; 2min
(def ^{:private true} reflip-timeout (* 0.5 1000)) ;; 0.5s

(def ^{:private true} colors [[255 153 153]
                              [255 0 255]
                              [0 255 255]
                              [204 0 0]
                              [51 51 255]
                              [127 0 255]
                              [255 102 178]
                              [0 153 0]])

(defn- half [x] (/ x 2.0))

(defn- future-time [offset]
  (+ (q/millis) offset))

(defn- time-has-come? [to]
  (>= (q/millis) to))

(defn in-rect? [x y pos size]
  (let [[px py] pos]
    (and (<= px x (+ px size))
         (<= py y (+ py size)))))

(defn- shuffle-colors []
  (let [c (take (half (* rows cols)) colors)]
    (apply concat (repeatedly 2 (partial #(shuffle c))))))

(defn- gen-cards []
  (into {} (map (fn [c id]
                  (let [[cx cy] id]
                    [id {:id id
                         :flipped false
                         :color c
                         :position [(* cx card-size) (* cy card-size)]}]))
                (shuffle-colors)
                (for [x (range cols)
                      y (range rows)] [x y]))))

(defn- flip-card [state card]
  (let [id (:id card)]
    (-> state
        (assoc-in [:cards id :flipped] true)
        (update :guess conj id))))

(defn- guess-full? [state]
  (= (count (:guess state)) 2))

(defn- pick [state x y]
  (let [card (some (fn [[_ card]]
                     (when (and (in-rect? x y (:position card) card-size)
                                (not (:flipped card)))
                       card))
                   (:cards state))
        not-full (not (guess-full? state))]
    (if (and card not-full)
      (-> state
          (flip-card card)
          (assoc :reflip-time (future-time reflip-timeout)))
      state)))

(defn- reflip-guess [state]
  (->
   (reduce (fn [acc id]
             (assoc-in acc [:cards id :flipped] false))
           state
           (:guess state))
   (assoc :guess [])))

(defn- handle-guess [state]
  (let [[a b] (:guess state)
        color-a (get-in state [:cards a :color])
        color-b (get-in state [:cards b :color])]
    (cond
      (= color-a color-b) (-> state
                              (assoc :guess [])
                              (update :goal #(- % 2)))
      (time-has-come? (:reflip-time state)) (reflip-guess state)
      :else state)))

(defn- game-over? [state]
  (time-has-come? (:end-time state)))

(defn initial-state []
  {:mode :menu
   :cards (gen-cards)
   :guess []
   :goal (* rows cols)
   :end-time 0
   :reflip-time 0})

(defn- start [state]
  (assoc state
         :end-time (future-time stop-time)
         :mode :play))

(defn- play [state]
  (cond
    (game-over? state) (assoc state :mode :game-over)
    (win? state) (assoc state :mode :win)
    (guess-full? state) (handle-guess state)
    :else state))

(defn- win? [state]
  (zero? (:goal state)))

(defn update-state [state]
  (case (:mode state)
    (:menu :game-over :win) state
    :play (play state)
    state))

(defn- handle-click-left-btn [state event]
  (case (:mode state)
    :menu (start state)
    :play (pick state (:x event) (:y event))
    (:game-over :win) (initial-state)
    state))

(defn handle-mouse [state event]
  (if (= (:button event) :left)
    (handle-click-left-btn state event)
    state))
