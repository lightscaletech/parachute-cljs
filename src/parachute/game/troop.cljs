(ns parachute.game.troop
  (:refer-clojure :exclude [drop])
  (:require [parachute.game.troop.details :as td]
            [parachute.game.troop.chute :as chute]
            [parachute.canvas :as can]
            [parachute.layout :as lo]
            [parachute.time :as t]))

(defn init [s] (assoc s :troops {:items '()}))

(defn make [x y] {:x x :y y :chute (chute/make)})

(defn get-speed [{{status :status} :chute}]
  (if (= status :deployed) chute/speed td/speed))

(defn move-reducer [td {:keys [y landed] :as t}]
  (if landed
    t
    (assoc t :y (+ y (* (get-speed t) td)))))

(defn move
  [{{td :diff} :time
    {troops :items} :troops
    :as s}]
  (let [reducer (partial move-reducer td)]
    (assoc-in
     s [:troops :items]
     (map reducer troops))))

(defn render-single
  [api
   {{lw :w lh :h} :inner-size
    {lx :x ly :y} :inner-pos}
   {x :x y :y
    :as troop}]
  (can/draw-rectangle
   api
   (+ lx (lo/cent lw x)) (+ ly (lo/cent lh y))
   (lo/cent lw td/width) (lo/cent lh td/height)
   "#FFF"))

(defn render [{{troops :items} :troops
               layout :layout
               api :api
               :as s}]
  (doseq [t troops] (render-single api layout t))
  s)

(def direction-fn {:left < :right >})

(defn drop-reducer
  [[troops helis] i {:keys [has-troop troop-pos x y direction]}]
  (if (and ((direction direction-fn) x troop-pos) has-troop)
    [(conj troops (make x y))
     (assoc-in helis [i :has-troop] false)]
    [troops helis]))

(defn drop [{{troops :items} :troops
             {helis :items} :helicopters
             :as s}]
  (let [helis (vec helis)
        [troops helis] (reduce-kv drop-reducer [troops helis] helis)]
    (-> s
        (assoc-in [:troops :items] troops)
        (assoc-in [:helicopters :items] helis))))

(defn stack-troop? [{x :x} {bx :x}]
  (and (>= x (- bx td/width))
       (<= x (+ bx td/width))))

(defn hit-chute?
  [{x :x y :y}
   {rx :x ry :y
    {status :status} :chute}]
  (let [tl x tr (+ tl td/width)
        tt y tb (+ tt td/height)
        rl (- rx (/ chute/width 2)) rr (+ rl chute/width)
        rt (- ry chute/height) rb ry]
    (and (= status :deployed)
         (or (and (> tl rl) (< tl rr))
             (and (< tr rr) (> tr rl)))
         (or (and (> tb rt) (< tb rb))
             (and (< tt rb) (> tt rt))))))

(defn below-troop? [{y :y} {by :y landed :landed}]
  (let [bot (- by td/height)]
    (when (and landed (> y bot)) bot)))

(defn land! [t bot] (assoc t :landed true :y bot))
(defn break-chute! [t] (assoc-in t [:chute :status] :hit))

(defn compare-falling-below
  [{x :x y :y
    {status :status} :chute :as c}
   {bx :x by :y landed :landed :as rc}]
  (let [stack (stack-troop? c rc)
        below (below-troop? c rc)
        ground (- 100 td/height)
        belowg (> y ground)
        crash (not= status :deployed)
        chute-hit (hit-chute? c rc)]
    (cond
      chute-hit [c (break-chute! rc)]
      (and belowg crash) [nil rc]
      (and stack below crash) [nil nil]
      (and stack below) [(land! c below) rc]
      belowg [(land! c ground) rc]
      :else [c rc])))

(defn filter-landed-rest
  [{:keys [y x landed] :as c}
   r]
  (if landed
    [c r]
    (loop [c c k [] rc (first r) rr (rest r)]
      (if rc
        (let [[c rc] (if c (compare-falling-below c rc) [c rc])]
          (recur c (if rc (conj k rc) k) (first rr) (rest rr)))
        [c k]))))

(defn filter-landing [troops]
  (loop [k '() c (first troops) r (rest troops)]
    (if c
      (let [[c r] (filter-landed-rest c r)]
        (recur (if c (conj k c) k) (first r) (rest r)))
      k)))

(defn landed
  [{{troops :items} :troops
    :as s}]
  (assoc-in s [:troops :items] (filter-landing troops)))

(defn process [s]
  (-> s
      move
      landed
      drop
      chute/process
      render))
