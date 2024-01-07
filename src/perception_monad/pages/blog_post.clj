(ns perception-monad.pages.blog-post
  (:require [perception-monad.pages.article :as article]))

(defn render-page [context page]
  (article/render-page context page))

