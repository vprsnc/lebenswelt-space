(ns analytics-abc.pages.blog-post
  (:require [analytics-abc.pages.article :as article]))

(defn render-page [context page]
  (article/render-page context page))

