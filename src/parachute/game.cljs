(ns parachute.game
  (:require [parachute.canvas :as canvas]
            [parachute.background :as bg]

            [parachute.game.gun :as gun]
            [parachute.game.bullets :as bullets]
            [parachute.game.helicopter :as helicopter]
            [parachute.game.troop :as troop]
            [parachute.game.score :as score]))

(defn init [s]
  (-> s
      gun/init
      bullets/init
      helicopter/init
      troop/init
      score/init))

(defn process [s]
  (-> s
      bg/render
      gun/render
      bullets/process
      helicopter/process
      troop/process
      score/process))
