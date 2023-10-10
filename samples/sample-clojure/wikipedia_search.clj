(ns wikipedia-search
  (:require [picoautomator.core :refer :all]))

(defn -main
  [& args]

  (start-flow
    "Search Wikipedia"
    (fn [automator]
      (-> automator
          (launch "org.wikipedia")
          (tap-on {:text "Search Wikipedia"})
          (assert-visible {:text "Recent searches:"})
          (input-text "Monad")
          (assert-visible {:text "Monad (functional programming)"})))))
