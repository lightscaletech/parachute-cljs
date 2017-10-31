(ns parachute.game.bullets
  (:require [parachute.canvas :as can]
            [parachute.layout :as lo]
            [parachute.math :as m]))

(def width  0.25)
(def height 0.5)
(def speed  0.25)

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
   0 (* (+ radius pos) -1)
   (lo/cent w width) (lo/cent h height) "#FFF")
  (can/restore api))

(defn render [{api :api
               {bullets :items} :bullets
               gun :gun
               layout :layout
               :as s}]
  (doseq [b bullets] (render-single api layout gun b))
  s)

(defn get-position [{:keys [turret-top cen-x radius]} {:keys [angle pos]}]
  (let [pos (+ radius pos)
        ang (m/deg->rad angle)
        x (* pos (m/sin ang))
        y (* pos (m/cos ang))]
    {:x (+ cen-x x)
     :y (+ turret-top (* y -1))}))

(defn border-collisions-reducer
  [gun
   layout
   bullet]
  (let [{bx :x by :y :as b} (get-position gun bullet)]
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

(defn process [s]
  (-> s
      control
      move
      border-collisions
      render))
