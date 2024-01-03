(ns analytics-abc.pages
   (:require [analytics-abc.layout :as layout]
             [analytics-abc.pages.frontpage :as frontpage]
             [analytics-abc.pages.article :as article]))

(defn render-blog-post [context page]
  (render-article context page))

(defn render-page [context page]
  (case (:page/kind page)
    :page.kind/frontpage (frontpage/render-page context page)
    :page.kind/blog-post (render-blog-post context page)
    :page.kind/article (article/render-page context page)))
