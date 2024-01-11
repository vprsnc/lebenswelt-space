(ns lebenswelt.pages.blog-post
  (:require [lebenswelt.pages.article :as article]))

(defn render-page [context page]
  (article/render-page context page))

