(ns parachute.game.bullets
  (:require [parachute.canvas :as can]
            [parachute.layout :as lo]
            [parachute.math :as m]
            [parachute.util :as u]
            [parachute.game.helicopter :as helicopter]))

(def width  0.45)
(def height 0.7)
(def speed  0.065)

(defn init [s] (assoc s :bullets {:items [] :ready true}))

(defn make [ang] {:angle ang :pos 0})

(defn control
  [{{{ksp :space} :key} :input
    {bullets :items ready :ready} :bullets
    {ang :angle} :gun
    :as s}]
  (cond
    (and ksp ready)
    (assoc s :bullets {:items (conj bullets (make ang)) :ready false})
    (and (not ksp) (not ready)) (assoc-in s [:bullets :ready] true)
    :else s))

(defn move [{{bullets :items} :bullets
             {td :diff} :time
             :as s}]
  (assoc-in
   s [:bullets :items]
   (map #(assoc % :pos (+ (:pos %) (* speed td))) bullets)))

(defn render-single
  [api
   {{w :w h :h} :inner-size}
   {:keys [cen-x turret-top radius]}
   {:keys [angle pos]}]
  (can/save api)
  (.translate api (- cen-x (lo/cent w (/ width 2))) turret-top)
  (.rotate api (m/deg->rad angle))
  (can/draw-rectangle
   api
   0 (* (+ radius (lo/cent h pos)) -1)
   (lo/cent w width) (lo/cent h height) "#FFF")
  (can/restore api))

(defn render [{api :api
               {bullets :items} :bullets
               gun :gun
               layout :layout
               :as s}]
  (doseq [b bullets] (render-single api layout gun b))
  s)

(defn get-position [{:keys [turret-top cen-x radius]}
                    {{h :h} :inner-size}
                    {:keys [angle pos]}]
  (let [pos (+ radius (lo/cent h pos))
        ang (m/deg->rad angle)
        x (* pos (m/sin ang))
        y (* pos (m/cos ang))]
    {:x (+ cen-x x)
     :y (+ turret-top (* y -1))}))

(defn border-collisions-reducer
  [gun
   layout
   bullet]
  (let [{bx :x by :y :as b} (get-position gun layout bullet)]
    (when (and (> bx (lo/left layout))
               (< bx (lo/right layout))
               (< by (lo/bottom layout))
               (> by (lo/top layout)))
      bullet)))

(defn border-collisions
  [{{bullets :items} :bullets
    gun :gun layout :layout
    :as s}]
  (let [reducer (partial border-collisions-reducer gun layout)]
    (assoc-in s [:bullets :items] (keep reducer bullets))))

(defn helicopter-coll-reducer
  [{layout :layout} {:keys [x y]} heli]
  (let [{:keys [top left right bottom]} (helicopter/get-square layout heli)]
    (when-not (and (> y top) (< y bottom)
                   (> x left) (< x right))
      heli)))

(defn helicopter-bull-coll-reducer
  [{gun :gun layout :layout :as s}
   [bullets helis] k v]
  (let [bpos (get-position gun layout v)
        reducer (partial helicopter-coll-reducer s bpos)
        pre-hc (count helis)
        nhelis (keep reducer helis)
        post-hc (count nhelis)
        nbullets (if (> pre-hc post-hc) (u/dissocv (vec bullets) k) bullets)]
    [nbullets nhelis]))

(defn helicopter-collisions
  [{{helis :items} :helicopters
    {bullets :items} :bullets
    :as s}]
  (let [reducer (partial helicopter-bull-coll-reducer s)
        [nbullets nhelis] (reduce-kv reducer [bullets helis] (vec bullets))]
    (-> s
         (assoc-in [:bullets :items] nbullets)
         (assoc-in [:helicopters :items] nhelis))))

(defn process [s]
  (-> s
      control
      move
      border-collisions
      helicopter-collisions
      render))
