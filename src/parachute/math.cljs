(ns parachute.math)

(def pi (.-PI js/Math))

(defn deg->rad [d] (* d (/ pi 180)))

(defn round [n] (.round js/Math n))
(defn floor [n] (.floor js/Math n))
(defn ceil  [n] (.ceil  js/Math n))
