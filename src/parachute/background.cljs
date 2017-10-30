(ns parachute.background
  (:require [parachute.canvas :as can]
            [parachute.layout :as lo]))

(def border 20)

(defn render
  [{{ww :w wh :h} :size
    {{lx :x ly :y} :pos {lw :w lh :h} :size
     {py :y} :padding
     {iw :w ih :h} :inner-size {ix :x iy :y} :inner-pos :as slo} :layout
    api :api :as s}]
  (can/draw-rectangle api 0 0 ww wh "#000")
  (can/draw-rectangle api lx ly lw lh "#555")
  (let [b (lo/cent py border) b2 (/ b 2)]
    (can/draw-stroke-rectangle
     api
     (- ix b2) (- iy b2)
     (+ iw b)  (+ ih b)
     "#FFF"
     b))
  s)
