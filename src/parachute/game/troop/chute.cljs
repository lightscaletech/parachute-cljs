(ns parachute.game.troop.chute
  (:require [parachute.time :as t]
            [parachute.math :as m]
            [parachute.util :as u]
            [parachute.canvas :as can]
            [parachute.layout :as lo]
            [parachute.game.troop.details :as troop]))

(def width 2.7)
(def height 2)

(def speed 0.0075)

(def min-time (* 1000 0.75))
(def max-time (* 1000 1.5))

(defn get-square
  [{{w :w h :h} :inner-size
    :as layout}
   {:keys [x y]}]
  (let [left (+ (lo/left layout) (lo/cent w (- x (/ troop/width 2))))
        top (+ (lo/top layout) (lo/cent h (- y troop/height)))
        right (+ left (lo/cent w width))
        bottom (+ top (lo/cent h height))]
    {:left left :right right :top top :bottom bottom}))

(defn get-deploy-time [] (+ (t/now) (u/random min-time max-time)))

(defn make [] {:status :undeployed :when (get-deploy-time)})

(defn deploy-reducer
  [now
   {{:keys [status when]} :chute :as troop}]
  (if (and (= status :undeployed)
           (> now when))
    (assoc-in troop [:chute :status] :deployed)
    troop))

(defn deploy
  [{{now :now} :time
    {troops :items} :troops
    :as s}]
  (let [reducer (partial deploy-reducer now)]
    (assoc-in s [:troops :items] (map reducer troops))))

(defn render
  [{{troops :items} :troops
    api :api
    {{lw :w lh :h} :inner-size
     {lx :x ly :y} :inner-pos} :layout
    :as s}]
  (doseq [{{status :status} :chute
           x :x y :y landed :landed}
          troops]
    (when (and (= status :deployed)
               (not landed))
      (can/draw-rectangle
       api
       (+ lx (lo/cent lw (- x (/ troop/width 2)))) (+ ly (lo/cent lh (- y height)))
       (lo/cent lw width) (lo/cent lh height)
       "#FFF")))
  s)

(defn process [s ]
  (-> s
      deploy
      render))
