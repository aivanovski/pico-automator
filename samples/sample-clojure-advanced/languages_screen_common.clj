(ns languages-screen-common
  (:require [picoautomator.core :refer :all]))

(defn open-screen
  [automator]

  (-> automator
      (assert-visible {:id "main_view_pager"})
      (tap-on {:content-desc "More"})
      (tap-on {:text "Settings"})
      (tap-on {:text "Wikipedia languages"})
      (assert-visible {:text "Your languages"}))

  automator)

(defn remove-language-if-need
  [automator language]

  (when (visible? automator {:text language})
    (-> automator
        (tap-on {:content-desc "More options"})
        (tap-on {:text "Remove language"})
        (tap-on {:text language})
        (tap-on {:content-desc "Delete selected items"})
        (tap-on {:text "OK"})))

  automator)

(defn add-language
  [automator language]

  (-> automator
      (assert-visible {:text "Your languages"})
      (tap-on {:text "Add language"})
      (tap-on {:content-desc "Search for a language"})
      (input-text language)
      (tap-on {:text language}))

  automator)
