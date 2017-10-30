(ns parachute.game.gun
  (:require [parachute.layout :as lo]
            [parachute.canvas :as can]
            [parachute.math :as m]))

(def base-w   7)
(def base-h   8)
(def turret-w 3)
(def turret-h 2)
(def barrel-w 0.5)
(def barrel-h 2.5)

(def speed 0.2)
(def max-angle 90)

(def pos-x (- 50 (/ base-w 2)))
(def pos-y (- 100 base-h))

(defn init [s]
  (assoc s :gun {:angle 0}))

(defn barrel
  [{:keys [cen-x turret-top]}
   {api :api
    {{w :w h :h} :inner-size} :layout
    {ang :angle} :gun}]
  (can/save api)
  (.translate api (- cen-x (lo/cent w (/ barrel-w 2))) turret-top)
  (.rotate api (m/deg->rad ang))
  (let [radius (lo/cent w turret-w)]
    (can/draw-rectangle
     api
     0 (* radius -1)
     (lo/cent w barrel-w) (lo/cent h barrel-h) "#FFF"))
  (can/restore api))

(defn turret-hat
  [{:keys [cen-x turret-top] :as r}
   {:keys [api]
    {{w :w h :h} :inner-size} :layout}]
  (can/save api)
  (set! (.-fillStyle api) "#FFF")
  (.beginPath api)

  (.arc api
        cen-x turret-top
        (lo/cent w (/ turret-w 2))
        0 m/pi true)

  (.closePath api)
  (.fill api)
  (can/restore api)
  r)

(defn turret
  [{:keys [cen-x base-top]}
   {{{w :w h :h} :inner-size} :layout
    api :api}]
  (let [tw (lo/cent w turret-w)
        th (lo/cent h turret-h)
        turret-top (- base-top th)]
    (can/draw-rectangle
     api
     (- cen-x (/ tw 2))
     (- base-top th)
     tw th "#FFF")
    {:turret-top turret-top :cen-x cen-x}))

(defn base
  [{{{w :w h :h} :inner-size :as slo
     {x :x} :inner-pos} :layout
    api :api
    :as s}]
  (let [bot (lo/bottom slo) cen-x (+ x (lo/center-x slo))
        base-top (- bot (lo/cent h base-h))]
    (can/draw-rectangle
     api
     (- cen-x (lo/cent w (/ base-w 2)))
     base-top
     (lo/cent w base-w) (lo/cent h base-h)
     "#FFF")
    {:cen-x cen-x :base-top base-top}))

(defn max-angle-calc [ang add]
  (let [n (+ ang add)]
    (if (or (< n (* max-angle -1))
            (> n max-angle))
      ang
      n)))

(defn control [{{td :diff} :time
                {ang :angle} :gun
                {{kl :left kr :right} :key} :input
                :as s}]
  (assoc-in
   s [:gun :angle]
   (max-angle-calc
    ang
    (cond
      kl (* td speed -1)
      kr (* td speed)
      :else 0))))

(defn render [s]
  (let [ns (control s)]
    (-> ns
        base
        (turret ns)
        (turret-hat ns)
        (barrel ns))
    ns))
