(ns parachute.core
  (:require [parachute.state :as state]
            [parachute.canvas :as canvas]
            [parachute.game :as game]
            [parachute.input :as input]
            [parachute.pause-modal :as pause]
            [parachute.time :as time]))

(declare frame)
(defn frame-loop [s] (.requestAnimationFrame js/window #(frame s)))

(defn frame [s]
  (-> s
      input/process
      canvas/process
      time/process
      game/process
      frame-loop))

(defn ^:export main []
  (->>
   (state/init)
   canvas/init
   input/init
   time/init
   game/init
   frame-loop))
