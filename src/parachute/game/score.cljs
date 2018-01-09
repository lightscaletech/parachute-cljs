(ns parachute.game.score
  (:require [parachute.canvas :as can]
            [parachute.layout :as lo]))

(def max-landed 4)
(def left? #(< % 50))
(def right? #(not (left? %)))

(defn init [s]
  (assoc s
         :score 0
         :landed-left 0
         :landed-right 0))

(defn add [{score :score :as s} amt]
  (assoc s :score (+ score amt)))

(defn decriment [{score :score :as s}]
  (if (pos? score)
    (assoc s :score (dec score))
    s))

(defn mark-landed-reducer [side-fn {:keys [x landed] :as t}]
  (if (and landed (side-fn x))
    (assoc t :status :attack)
    t))

(defn mark-landed [{{troops :items} :troops :as s} side-fn]
  (assoc-in s [:troops :items]
            (map (partial mark-landed-reducer side-fn) troops)))

(defn landed-reducer [[l r] {landed :landed x :x status :status}]
  (if (and landed (not (= status :attack)))
    [(if (left? x) (inc l) l) (if (right? x) r (inc r))]
    [l r]))

(defn check-landed [{{troops :items} :troops :as s}]
  (let [[left right] (reduce landed-reducer [0 0] troops)]
    (cond
      (>= left  max-landed) (mark-landed s left?)
      (>= right max-landed) (mark-landed s right?)
      :else s)))

(defn render [{api :api score :score
               {{lx :x ly :y } :inner-pos}:layout
               :as s}]
  (let [size 18]
    (can/draw-text
     api (str "Score: " score) size
     (+ lx size) (+ ly (* 2 size)) "#FFF"))
  s)

(defn process [s]
  (-> s
      render
      check-landed))
