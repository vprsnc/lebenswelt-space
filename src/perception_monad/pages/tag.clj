(ns perception-monad.pages.tag
  (:require [datomic.api :as d]
            [perception-monad.layout :as layout]))

(defn get-blog-posts [db tag]
  (->> (d/q '[:find [?e ...]
              :in $ ?tag
              :where
              [?e :blog-post/tags ?tag]]
            db tag)
       (map #(d/entity db %))))

(defn render-page [context page] ;; TODO localize
  (let [title (str "Blog posts about " (name (:tag-page/tag page)))]
    (layout/layout
     {:title title}
     layout/header
     [:article.prose.mx-auto
      [:h1 title]
      [:ul {:id "nobullets"}
       (for [blog-post (get-blog-posts (:app/db context) (:tag-page/tag page))]
         [:li
          [:h5
           [:small (:blog-post/date-created blog-post)]
           " - "
           [:a {:href (:page/uri blog-post)} (:page/title blog-post)]]
          [:p (:open-graph/description blog-post)]])]])))
