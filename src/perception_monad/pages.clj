(ns perception-monad.pages
   (:require [perception-monad.layout :as layout]
             [perception-monad.pages.blog-listing :as blog-listing]
             [perception-monad.pages.frontpage :as frontpage]
             [perception-monad.pages.article :as article]
             [perception-monad.pages.tag :as tag]
             [perception-monad.pages.blog-post :as blog-post]))

(defn render-page [context page]
  (case (:page/kind page)
    :page.kind/frontpage (frontpage/render-page context page)
    :page.kind/blog-post (blog-post/render-page context page)
    :page.kind/blog-listing (blog-listing/render-page context page)
    :page.kind/tag (tag/render-page context page)
    :page.kind/article (article/render-page context page)))
