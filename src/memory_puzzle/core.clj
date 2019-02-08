(ns memory-puzzle.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [quil.applet :as a]
            [memory-puzzle.game :as game]
            [memory-puzzle.render :as render]))

(defn setup []
  (q/color-mode :rgb)
  (q/frame-rate 30)
  (game/initial-state))

(q/defsketch memory-puzzle
  :title "Memory Puzzle Game"
  :size [render/screen-width render/screen-height]
  :setup setup
  :draw render/render-state
  :update game/update-state
  :mouse-pressed game/handle-mouse
  :features [:keep-on-top :no-bind-output]
  :middleware [m/fun-mode m/pause-on-error])
