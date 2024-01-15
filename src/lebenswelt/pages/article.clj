(ns lebenswelt.pages.article
  (:require [powerpack.markdown :as md]
            [lebenswelt.layout :as layout]))

(defn render-page [context page]
  (layout/layout
   {}
   layout/header
   [:article.prose.mx-auto
    [:p
     [:ul.info
      [:li [:small "By: " ( :person/full-name (:blog-post/author page))]]
      [:li [:small "| Created at: " (:blog-post/date-created page)]]
      [:li [:small "| Last updated: " (:blog-post/last-updated page)]]]]
    (md/render-html (:page/body page))]))
