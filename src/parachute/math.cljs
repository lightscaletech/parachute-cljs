(ns parachute.math)

(def pi (.-PI js/Math))

(defn deg->rad [d] (* d (/ pi 180)))

(defn pcent [t p] (* t (/ p 100)))

(defn round [n] (.round js/Math n))
(defn floor [n] (.floor js/Math n))
(defn ceil  [n] (.ceil  js/Math n))

(defn sin   [n] (.sin js/Math n))
(defn cos   [n] (.cos js/Math n))
(defn tan   [n] (.tan js/Math n))
