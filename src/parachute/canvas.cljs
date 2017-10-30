(ns parachute.canvas
  (:require [parachute.util :as u]
            [parachute.layout :as layout]))

(defn resize [{:keys [canvas input] :as s}]
  (if (:win-resize input)
    (let [[w h] (u/get-window-size)]
      (set! (.-width canvas) w)
      (set! (.-height canvas) h)
      (-> s
          (assoc :size {:w w :h h}
                 :input (assoc input :win-resize false))
          layout/process))
    s))


(defn clear [{:keys [api size] :as s}]
  (.clearRect api 0 0 (:w size) (:h size))
  s)

(defn process [s]
  (-> s
      resize
      clear))

(defn init [s]
  (let [c (.getElementById js/document "canvas")
        a (.getContext c "2d")]
    (-> s
        (assoc :canvas c :api a)
        resize)))

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
