(ns parachute.layout
  (:require [parachute.math :as m]))

(def w-ratio (/ 4 3))
(def h-ratio (/ 3 4))

(defn h-from-w [w] (m/floor (* w h-ratio)))
(defn w-from-h [h] (m/floor (* h w-ratio)))

(def padding 1.25)
(def padding-x padding)
(def padding-y (w-from-h padding))

(defn make-size [{{w :w h :h} :size
                  layout :layout
                  :as s}]
  (let [hw (w-from-h h)
        wh (h-from-w w)
        ls (if (<= hw w)
            {:w hw :h (* hw h-ratio)}
            {:w (* wh w-ratio) :h wh})]
    (assoc-in s [:layout :size] ls)))

(defn cent [t p] (m/floor (* t (/ p 100))))

(defn make-padding [{{{sw :w sh :h} :size} :layout
                    :as s}]
  (assoc-in s [:layout :padding] {:x (cent sw padding-x) :y (cent sh padding-y)}))

(defn make-inner-size [{{{sw :w sh :h} :size
                         {px :x py :y} :padding} :layout
                        :as s}]
  (assoc-in s [:layout :inner-size] {:w (- sw (* px 2)) :h (- sh (* py 2))}))

(defn center [f i] (m/floor (- (/ f 2) (/ i 2))))

(defn make-pos
  [{{ww :w wh :h} :size
    {{lw :w lh :h} :size :as layout} :layout
    :as s}]
  (assoc-in s [:layout :pos] {:x (center ww lw) :y (center wh lh)}))

(defn make-inner-pos [{{{sw :w sh :h} :size
                        {px :x py :y} :pos
                        {iw :w ih :h} :inner-size} :layout
                       :as s}]
  (assoc-in s [:layout :inner-pos] {:x (+ px (center sw iw))
                                    :y (+ py (center sh ih))}))

(defn process [s]
  (-> s
      make-size
      make-padding
      make-inner-size
      make-pos
      make-inner-pos))

(defn top [{{y :y} :inner-pos}] y)
(defn bottom [{{h :h} :inner-size {y :y} :inner-pos}] (+ h y))
(defn left [{{x :x} :inner-pos}] x)
(defn right [{{x :x} :inner-pos {w :w} :inner-size}] (+ w x))
(defn center-x [{{w :w} :inner-size}] (/ w 2))
(defn center-y [{{h :h} :inner-size}] (/ h 2))
