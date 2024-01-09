(ns perception-monad.pages.article
  (:require [powerpack.markdown :as md]
            [perception-monad.layout :as layout]
            [perception-monad.pages.frontpage :refer [get-tags]]))

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
    (md/render-html (:page/body page))
    [:h4.mx-auto.text-center "Topics"]
    [:ul.tagcloud
       (for [tag (map name (into [] (:blog-post/tags page)))]
         [:li [:a {:id (str "tag_" tag) :href (str "/tag/" tag "/")} tag]])]]))
