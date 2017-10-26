(ns parachute.core
  (:require [parachute.canvas :as canvas]
            [parachute.sidebar :as sidebar]
            [parachute.layout :as lo]
            [parachute.game :as game]
            [parachute.input :as input]))

(defn resize []
  (canvas/resize)
  (lo/resize)
  (sidebar/resize))

(defn ^:export main []
  (.addEventListener js/window "resize" resize true)
  (canvas/init)
  (lo/resize)
  (input/init)
  (game/start))
