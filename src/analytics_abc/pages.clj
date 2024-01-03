(ns analytics-abc.pages
   (:require [analytics-abc.layout :as layout]
             [analytics-abc.pages.blog-listing :as blog-listing]
             [analytics-abc.pages.frontpage :as frontpage]
             [analytics-abc.pages.article :as article]
             [analytics-abc.pages.blog-post :as blog-post]))

(defn render-page [context page]
  (case (:page/kind page)
    :page.kind/frontpage (frontpage/render-page context page)
    :page.kind/blog-post (blog-post/render-page context page)
    :page.kind/blog-listing (blog-listing/render-page context page)
    :page.kind/article (article/render-page context page)))
