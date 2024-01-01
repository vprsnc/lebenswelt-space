(ns analytics-abc.core
  (:require [powerpack.markdown :as md]))

(defn render-page [context page]
  (md/render-html (:page/body page)))

(def config
  {:site/title "analytics-abc"
   :powerpack/render-page #'render-page})
