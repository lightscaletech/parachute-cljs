(ns parachute.game
  (:require [parachute.canvas :as canvas]
            [parachute.state :as state]
            [parachute.background :as bg]
            [parachute.grid :as grid]
            [parachute.sidebar :as sidebar]
            [parachute.gameover :as gameover]
            [parachute.startgame :as startgame]
            [parachute.pause-modal :as pause]))

(declare frame)
(defn frame-loop [] (.requestAnimationFrame js/window frame))

(defn render-game []
  (grid/render)
  (sidebar/render)
  (pause/render))

(defn frame []
  (canvas/clear)
  (canvas/save)

  (bg/render)
  (condp = @state/game-state
    :start (startgame/render)
    :game (render-game)
    :gameover (gameover/render))

  (canvas/restore)
  (frame-loop))

(defn start []
  (pause/init)
  (state/startscreen)
  (frame-loop))
