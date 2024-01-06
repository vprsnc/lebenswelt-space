(ns analytics-abc.pages.blog-listing
  (:require [analytics-abc.layout :as layout]
            [analytics-abc.pages.frontpage :as frontpage]))

(defn render-page [context page]
  (layout/layout
   {:title [:i18n ::page-title]}
   layout/header
   [:article.prose.dark:prose-invert.mx-auto
    [:h1 [:i18n ::page-title]]
    [:ul
    (for [blog-post (frontpage/get-blog-posts (:app/db context))]
      [:li
       [:small (:blog-post/date-created blog-post)]
       " - "
       [:a {:href (:page/uri blog-post)} (:page/title blog-post)]])]]
   ))
