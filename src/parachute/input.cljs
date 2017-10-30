(ns parachute.input)

(def state (atom {}))

(def key-mapping {37 [:key :left]
                  38 [:key :up]
                  39 [:key :right]
                  40 [:key :down]
                  32 [:key :space]
                  13 [:key :enter]})

(defn win-resize [ev] (swap! state assoc-in [:win-resize] true))

(defn key-down [e]
  (when-let [a (key-mapping (.-keyCode e))] (swap! state assoc-in a true)))
(defn key-up [e]
  (when-let [a (key-mapping (.-keyCode e))] (swap! state assoc-in a false)))

(defn set-dev-pos [dev x y]
  (swap! state #(assoc % dev (assoc (dev %) :x x :y y))))

(defn mouse-move [e] (set-dev-pos :mouse (.-offsetX e) (.-offsetY e)))

(defn mouse-down [e]
  (mouse-move e)
  (swap! state assoc-in [:mouse :down] true))

(defn mouse-up   [e]
  (mouse-move e)
  (swap! state assoc-in [:mouse :down] false))

(defn mtouch-pos [e]
  (let [t (-> e .-changedTouches (aget 0))]
    {:x (.-clientX t) :y (.-clientY t)}))

(defn touch-move! [e]
  (.preventDefault e)
  (let [{:keys [x y]} (mtouch-pos e)]
    (set-dev-pos :mouse x y)
    (set-dev-pos :touch x y)))

(defn touch-down! [e]
  (.preventDefault e)
  (let [{:keys [x y]} (mtouch-pos e)]
    (set-dev-pos :touch-start x y))
  (touch-move! e)
  (swap! state assoc-in [:mouse :down] true)
  (swap! state assoc-in [:touch :down] true))

(defn touch-up! [e]
  (set-dev-pos :touch-start 0 0)
  (touch-move! e)
  (swap! state assoc-in [:mouse :down] false)
  (swap! state [:touch :down] false))

(defn touch-diff []
  (let [{:keys [touch touch-start]} (deref state)]
    (when (:down touch)
      {:x (- (:x touch) (:x touch-start))
       :y (- (:y touch) (:y touch-start))})))

(def pasev (clj->js {:passive false}))
(defn on-bod [ev cb] (.addEventListener (.-body js/document) ev cb pasev))
(defn on-win [ev cb] (.addEventListener js/window ev cb pasev))

(defn init [s]
  (on-win "resize"     win-resize)
  (on-win "orientationchange" win-resize)
  (on-bod "keydown"    key-down)
  (on-bod "keyup"      key-up)
  (on-bod "mousemove"  mouse-move)
  (on-bod "mouseup"    mouse-up)
  (on-bod "mousedown"  mouse-down)
  (on-bod "touchstart" touch-down!)
  (on-bod "touchend"   touch-up!)
  (on-bod "touchmove"  touch-move!)
  s)

(defn process [s]
  (let [res (assoc s :input @state)]
    (swap! state assoc-in [:win-resize] false)
    res))
