(ns parachute.game
  (:require [parachute.canvas :as canvas]
            [parachute.background :as bg]

            [parachute.game.gun :as gun]
            [parachute.game.bullets :as bullets]))

(defn init [s]
  (-> s
      gun/init
      bullets/init))

(defn process [s]
  (-> s
      bg/render
      gun/render
      bullets/process))
