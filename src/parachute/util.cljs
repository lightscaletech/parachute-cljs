(ns parachute.util)

(defn get-window-size []
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)]
    [w h]))
