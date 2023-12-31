(ns wikipedia-remove-language
  (:require [picoautomator.core :refer :all]
            [languages-screen-common :as languages]))

(defn flow
  [automator]

  (-> automator
      (launch "org.wikipedia")
      (languages/open-screen))

  (when-not (visible? automator {:text "Deutsch"})
    (languages/add-language automator "Deutsch"))

  (-> automator
      (assert-visible {:text "Deutsch"})
      (languages/remove-language-if-need "Deutsch")
      (assert-not-visible {:text "Deutsch"})))

(defn -main
  [& args]

  (start-flow
    "Remove language"
    flow))
