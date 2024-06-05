(ns test
  (:require [picoautomator.core :refer :all])
  (:gen-class))

(defn -main
  [& args]

  (start-flow
    "Search Wikipedia"
    (fn [automator]
      (-> automator
          (launch "com.github.aivanovski.picoautomator.android.driver")
          (assert-visible {:text "Start driver"})
          (tap-on {:text "Stop test"})
          ))))
