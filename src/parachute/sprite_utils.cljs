(ns parachute.sprite-utils
  (:require [parachute.layout :as lo]))


(defn get-square
  [width height
   {{w :w h :h} :inner-size
    :as layout}
   {:keys [x y]}]
  (let [left (+ (lo/left layout) (lo/cent w x))
        top (+ (lo/top layout) (lo/cent h y))
        right (+ left (lo/cent w width))
        bottom (+ top (lo/cent h height))]
    {:left left :right right :top top :bottom bottom}))
