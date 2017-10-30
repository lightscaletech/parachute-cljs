(ns parachute.game
  (:require [parachute.canvas :as canvas]
            [parachute.background :as bg]

            [parachute.game.gun :as gun]))

(defn init [s]
  (-> s
      gun/init))

(defn process [s]
  (-> s
      bg/render
      gun/render))
