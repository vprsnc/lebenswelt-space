(ns lebenswelt.pages
   (:require [lebenswelt.layout :as layout]
             [lebenswelt.pages.blog-listing :as blog-listing]
             [lebenswelt.pages.frontpage :as frontpage]
             [lebenswelt.pages.article :as article]
             [lebenswelt.pages.tag :as tag]
             [lebenswelt.pages.blog-post :as blog-post]))

(defn render-page [context page]
  (case (:page/kind page)
    :page.kind/frontpage (frontpage/render-page context page)
    :page.kind/blog-post (blog-post/render-page context page)
    :page.kind/blog-listing (blog-listing/render-page context page)
    :page.kind/tag (tag/render-page context page)
    :page.kind/article (article/render-page context page)))
