(ns wikipedia-add-language
  (:require [picoautomator.core :refer :all]))

(defn open-languages-screen
  [automator]

  (-> automator
      (launch "org.wikipedia")
      (tap-on {:content-desc "More"})
      (tap-on {:text "Settings"})
      (tap-on {:text "Wikipedia languages"}))

  automator)

(defn remove-language-if-added
  [automator]

  (when (visible? automator {:text "Deutsch"})
    (-> automator
        (tap-on {:content-desc "More options"})
        (tap-on {:text "Remove language"})
        (tap-on {:text "Deutsch"})
        (tap-on {:content-desc "Delete selected items"})
        (tap-on {:text "OK"})))

  automator)

(defn add-language
  [automator]

  (-> automator
      (tap-on {:text "Add language"})
      (tap-on {:content-desc "Search for a language"})
      (input-text "german")
      (tap-on {:text "Deutsch"}))

  automator)

(defn fail-if-language-not-visible
  [automator]

  (when-not (visible? automator {:text "Deutsch"})
    (fail automator "Language wasn't added"))

  automator)

(defn flow
  [automator]

  (-> automator
      (open-languages-screen)
      (remove-language-if-added)
      (add-language)
      (fail-if-language-not-visible)))

(defn -main
  [& args]

  (start-flow
    "Add Language"
    flow))
