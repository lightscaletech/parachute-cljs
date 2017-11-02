(ns parachute.util)

(defn get-window-size []
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)]
    [w h]))

(defn random-int [min max] (+ min (rand-int (- (+ max 1) min))))

(defn random [min max] (+ min (rand (- max min))))

(defn dissocv [c i] (into (subvec c 0 i) (subvec c (inc i))))
