(ns parachute.pause-modal
  (:require [parachute.layout :as lo]
            [parachute.state :as state]
            [parachute.canvas :as can]
            [parachute.widgets :as widg]
            [parachute.overlay :as overlay]))

(declare height
         pos-sy
         pos-y
         widgets)

(defn init []
  (def height 6.5)
  (def pos-sy (lo/cpause-modal-sy height))
  (def widgets
    {:txtpause (widg/make-text-line
                {:x (+ lo/pause-modal-x 0.5) :y (+ pos-sy 1.75)
                 :size 1 :text "PAUSED"})
     :btnresume (widg/make-button
                 {:x (+ lo/pause-modal-x 0.5) :y (+ pos-sy 3)
                  :w 4 :h 1 :text "Resume"
                  :cb state/unpause})
     :btnrestart (widg/make-button
                  {:x (+ lo/pause-modal-x 0.5) :y (+ pos-sy 4.5)
                   :w 4 :h 1 :text "Restart"
                   :cb state/restart})
     :btnquit (widg/make-button
               {:x (+ lo/pause-modal-x 0.5) :y (+ pos-sy 6)
                :w 4 :h 1 :text "Quit"
                :cb state/startscreen})}))

(defn render []
  (when @state/paused
    (overlay/render)
    (can/draw-rectangle (lo/cpause-modal-x) (lo/cpause-modal-y height)
                        (lo/cpause-modal-w) (* height @state/square-size) "#111")
    (widg/render widgets)))
