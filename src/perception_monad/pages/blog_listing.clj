(ns perception-monad.pages.blog-listing
  (:require [perception-monad.layout :as layout]
            [perception-monad.pages.frontpage :as frontpage]))

(defn render-page [context page]
  (layout/layout
   {:title [:i18n ::page-title]}
   layout/header
   [:article.prose.mx-auto
    [:h1 [:i18n ::page-title]]
    [:ul {:id "nobullets"}
    (for [blog-post (frontpage/get-blog-posts (:app/db context))]
      [:li
       [:h5
        [:small (:blog-post/date-created blog-post)]
        " - "
        [:a {:href (:page/uri blog-post)} (:page/title blog-post)]]
       [:p (:open-graph/description blog-post)]])]]
   ))