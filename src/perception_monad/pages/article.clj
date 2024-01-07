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
     [:ul {:id "tagcloud"}
;;      [:li [:small "Author: " (:blog-post/author page)]]
      [:li [:small "Created at: " (:blog-post/date-created page)]]
      [:li [:small "Last updated: " (:blog-post/last-updated page)]]]]
    (md/render-html (:page/body page))
    [:h6.mx-auto.text-center "Tags"]
    [:ul  {:id "tagcloud"}
       (for [tag (get-tags (:app/db context))]
         [:li [:a {:href (str "/tag/" tag "/")} tag]])]]))
