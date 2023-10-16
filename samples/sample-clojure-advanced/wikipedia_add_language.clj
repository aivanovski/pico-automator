(ns wikipedia-add-language
  (:require [picoautomator.core :refer :all]
            [languages-screen-common :as languages]))

(defn flow
  [automator]

  (-> automator
      (launch "org.wikipedia")
      (languages/open-screen)
      (languages/remove-language-if-need "Deutsch")
      (languages/add-language "Deutsch")
      (assert-visible {:text "Deutsch"})))

(defn -main
  [& args]

  (start-flow
    "Add Language"
    flow))
