(ns parachute.game.helicopter
  (:require [parachute.canvas :as can]
            [parachute.layout :as lo]
            [parachute.math :as m]
            [parachute.util :as u]
            [parachute.time :as time]))

(def min-time (* 0.5 1000))
(def max-time (* 4   1000))

(def min-pos 5)
(def max-pos 35)

(def width   5)
(def height  4)

(def speed 0.02)

(def directions [:left :right])
(def direction-fn {:left -
                   :right +})

(defn get-next-time []
  (+ (time/now)
     (u/random-int min-time max-time)))

(defn init [s]
  (assoc
   s
   :helicopters {:items [] :next (get-next-time)}))

(defn choose-direction [] (rand-nth directions))

(defn make []
  (let [dir (choose-direction)
        x (if (= dir :right) (- 0 width) 100)
        y (u/random min-pos max-pos)]
    {:x x :y y :direction dir}))

(defn launch
  [{{now :now} :time
    {:keys [next items]} :helicopters
    :as s}]
  (if (> now next)
    (assoc s :helicopters
           {:items (conj items (make))
            :next (get-next-time)})
    s))

(defn get-square
  [{{w :w h :h} :inner-size
    :as layout}
   {:keys [x y]}]
  (let [left (+ (lo/left layout) (lo/cent w x))
        top (+ (lo/top layout) (lo/cent h y))
        right (+ left (lo/cent w width))
        bottom (+ top (lo/cent h height))]
    {:left left :right right :top top :bottom bottom}))

(defn drop-troop [s] s)

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
      drop-troop
      move
      border-collisions
      render))
