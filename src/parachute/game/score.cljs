(ns parachute.game.score)

(defn add [{score :score :as s} amt]
  (assoc s :score (+ score amt)))

(defn decriment [{score :score :as s} amt]
  (assoc s :score (dec score)))

(defn landed-reducer [[l r] {landed :landed x :x :as t}]
  (if landed
    (let [left (< x 50)] [(if left (inc l) l) (if left r (inc r))])
    [l r]))

(defn count-landed [{{troops :items} :troops :as s}]
  (let [[left right] (reduce [0 0] troops)]
    (assoc s :landed-left left :landed-right right)))
