(ns search-wikipedia
    (:require [picoautomator.core :refer :all]))

(defn -main
  [& args]

  (start-flow
    "Search Wikipedia Flow"
    (fn [automator]
      (-> automator
          (launch "org.wikipedia")
          (wait-for {:text "Search"} {:seconds 10} {:millis 1000})
          (tap-on {:text "Search"})
          (tap-on {:text "Search Wikipedia"})
          (input-text "Dunning" {:text "Search Wikipedia"})
          (tap-on {:text "Dunning–Kruger effect"})
          (assert-visible {:id "page_web_view"})))))
