(ns lebenswelt.pages.blog-listing
  (:require [lebenswelt.layout :as layout]
            [lebenswelt.pages.frontpage :as frontpage]))

(defn render-page [context page]
  (layout/layout
   {:title [:i18n ::page-title]}
   layout/header
   [:article.prose.mx-auto
    [:h1 [:i18n ::page-title]]
    [:h3.text-center "Topics"]
      [:ul.tagcloud
       (for [tag (frontpage/get-tags (:app/db context))]
         [:li [:a {:id (str "tag_" tag) :href (str "/tag/" tag "/")} tag]])]
    [:ul {:id "nobullets"}
     (for [blog-post (frontpage/get-blog-posts (:app/db context))]
       [:li
        [:h5
         [:small (:blog-post/date-created blog-post)]
         " - "
         [:a {:href (:page/uri blog-post)} (:page/title blog-post)]]])]]
   ))
