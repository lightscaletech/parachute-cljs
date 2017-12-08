(ns parachute.game.helicopter
  (:require [parachute.canvas :as can]
            [parachute.layout :as lo]
            [parachute.math :as m]
            [parachute.util :as u]
            [parachute.time :as t]
            [parachute.sprite-utils :as su]

            [parachute.game.gun :as gun]
            [parachute.game.troop.details :as troop]))

(def min-time (* 0.5 1000))
(def max-time (* 4   1000))

(def min-pos 5)
(def max-pos 35)

(def width   5)
(def height  4)

(def get-square (partial su/get-square width height))

(def speed 0.02)

(def min-troop-pos 7)
(def max-troop-pos 93)
(def exclude-troop-pos {:left (- 50 troop/width (/ gun/base-w 1.5))
                        :right (+ 50 (/ gun/base-w 1.5))})
(def troop-probability 0.75)

(def directions [:left :right])
(def direction-fn {:left - :right +})

(defn get-time [min max] (+ (t/now) (u/random-int min max)))
(defn get-next-time [] (get-time min-time max-time))

(defn init [s]
  (assoc
   s
   :helicopters {:items [] :next (get-next-time)}))

(defn troop-pos []
  (let [p (u/random min-troop-pos max-troop-pos)
        {exl :left exr :right} exclude-troop-pos]
    (cond
      (and (<= exl p) (>= 50 p)) exl
      (and (<  50 p) (>= exr p)) exr
      :else p)))

(defn decide-troop [] (< (rand) troop-probability))
(defn choose-direction [] (rand-nth directions))

(defn make []
  (let [dir (choose-direction)
        x (if (= dir :right) (- 0 width) 100)
        y (u/random min-pos max-pos)]
    {:x x :y y :direction dir
     :has-troop (decide-troop)
     :troop-pos (troop-pos)}))

(defn launch
  [{{now :now} :time
    {:keys [next items]} :helicopters
    :as s}]
  (if (> now next)
    (assoc s :helicopters
           {:items (conj items (make))
            :next (get-next-time)})
    s))

(defn move
  [{{heli :items} :helicopters
    {td :diff} :time
    :as s}]
  (assoc-in
   s [:helicopters :items]
   (map #(assoc % :x ((->> % :direction (get direction-fn))
                      (:x %) (* speed td))) heli)))

(defn border-collisions-reducer
  [{{w :w} :inner-size
    {x :x} :inner-pos
    :as layout}
   {hx :x :as heli}]
  (let [left (- (lo/left layout) (lo/cent w (+ width 0.4)))
        right (+ (lo/right layout) (lo/cent w (+ width 0.4)))
        htx (+ (lo/cent w hx) x)]
    (if (and (< left htx) (> right htx))
      heli)))

(defn border-collisions
  [{layout :layout
    {heli :items} :helicopters
    :as s}]
  (let [reducer (partial border-collisions-reducer layout)]
    (assoc-in
     s [:helicopters :items]
     (keep reducer heli))))

(defn render-single
  [api
   {{x :x y :y} :inner-pos
    {w :w h :h} :inner-size}
   {hx :x hy :y}]
  (can/draw-rectangle
   api
   (+ x (lo/cent w hx)) (+ y (lo/cent h hy))
   (lo/cent w width) (lo/cent h height) "#EEE"))

(defn render
  [{api :api
    {heli :items} :helicopters
    layout :layout
    :as s}]
  (doseq [h heli] (render-single api layout h))
  s)

(defn process [s]
  (-> s
      launch
      move
      border-collisions
      render))
