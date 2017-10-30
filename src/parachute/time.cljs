(ns parachute.time)

(defn now [] (.now js/Date))

(defn init [s]
  (assoc s :time {:now (now) :diff 0}))

(defn process [{{ot :now} :time
                :as s}]
  (let [n (now)] (assoc s :time {:now n :diff (- n ot)})))
