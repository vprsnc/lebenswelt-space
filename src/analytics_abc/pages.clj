(ns analytics-abc.pages
   (:require [powerpack.markdown :as md]
             [analytics-abc.layout :as layout]
             [analytics-abc.pages.frontpage :as frontpage]))

(defn render-article [context page]
  (layout/layout
   {}
   layout/header
   [:article.prose.dark:prose-invert.mx-auto
    (md/render-html (:page/body page))]))

(defn render-blog-post [context page]
  (render-article context page))

(defn render-page [context page]
  (case (:page/kind page)
    :page.kind/frontpage (frontpage/render-page context page)
    :page.kind/blog-post (render-blog-post context page)
    :page.kind/article (render-article context page)))
