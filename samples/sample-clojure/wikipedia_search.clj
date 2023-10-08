(ns wikipedia-search
  (:require [picoautomator.core :refer :all]))

(defn -main
  [& args]

  (start-flow
    "Search Wikipedia"
    (fn [automator]
      (-> automator
          (launch "org.wikipedia")
          (assert-visible {:text "Search"})
          (tap-on {:text "Search"})
          (tap-on {:text "Search Wikipedia"})
          (input-text "Dunning" {:text "Search Wikipedia"})
          (tap-on {:text "Dunning–Kruger effect"})
          (assert-visible {:id "page_web_view"})))))
