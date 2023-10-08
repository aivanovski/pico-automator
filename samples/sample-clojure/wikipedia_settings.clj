(ns wikipedia-settings
  (:require [picoautomator.core :refer :all]))

(defn -main
  [& args]

  (start-flow
    "Open Wikipedia Settings"
    (fn [automator]
      (-> automator
          (launch "org.wikipedia")
          (assert-visible {:text "More"})
          (tap-on {:text "More"})
          (tap-on {:text "Settings"})
          (assert-visible {:text "Wikipedia languages"})))))