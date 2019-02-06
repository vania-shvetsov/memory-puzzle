(ns memory-puzzle.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [quil.applet :as a]
            [memory-puzzle.game :as game]))

(def width 3)
(def height 4)
(def card-size 60)
(def margin 10)
(def card-types [:a :b :c :d :e :f :g])

(def s1 (game/initial-state width height card-types))

(defn cards-coords [cards]
  (let [coords (keys cards)]
    (into {} (for [[x y] coords]
               [[x y] [(+ margin (* x card-size))
                       (+ margin (* y card-size))]]))))

(defn update-state [state]
  state)

(defn draw-state [state])

(defn setup []
  (q/color-mode :rgb)
  (q/background 0)
  {:view {:cards (cells->coords ())}})

(q/defsketch memory-puzzle
  :title "Memory Puzzle Game"
  :size [800 600]
  :setup setup
  :draw draw-state
  :update update-state
  :features [:keep-on-top :no-bind-output]
  :middleware [m/fun-mode m/pause-on-error])
