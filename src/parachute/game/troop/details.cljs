(ns parachute.game.troop.details
  (:require [parachute.sprite-utils :as su]))

(def width 1.5)
(def height 2.5)
(def speed 0.02)

(def get-square (partial su/get-square width height))
