(ns parachute.canvas
  (:require [parachute.util :as u]))


(defn resize [{:keys [canvas]}]
  (let [[w h] (u/get-window-size)]
    (set! (.-width canvas) w)
    (set! (.-height canvas) h)
    {:w w :h h}))

(defn process [s]
  (if (:win-resize s)
    (assoc s
           :size (resize s)
           :win-resize false)
    s))

(defn init [s]
  (let [c (.getElementById js/document "canvas")
        a (.getContext c "2d")]
   (assoc s :canvas c :api a :size (resize s))))

(defn clear [api]    (.clearRect api 0 0 0 0))
(defn save [api]     (.save api))
(defn restore [api] (.restore api))

(defn draw-rectangle [api x y w h c]
  (save api)
  (set! (.-fillStyle api) c)
  (.fillRect api x y w h)
  (restore api))

(defn draw-stroke-rectangle [api x y w h c s]
  (save api)
  (set! (.-strokeStyle api) c)
  (set! (.-lineWidth api) s)
  (.strokeRect api x y w h)
  (restore api))

(defn draw-text [api text s x y c]
  (save api)
  (set! (.-font api) (str s "px arial"))
  (set! (.-fillStyle api) c)
  (.fillText api text x (- y (/ s 2)))
  (restore api))
