(ns lebenswelt.pages.frontpage
  (:require [datomic.api :as d]
            [lebenswelt.layout :as layout]
            [powerpack.markdown :as md]))

(defn get-blog-posts [db]
  (->> (d/q '[:find [?e ...]
              :where
              [?e :blog-post/author]]
            db)
       (map #(d/entity db %))))

(defn get-tags [db]
   (->> (d/q '[:find [?tag ...]
               :where
               [_ :blog-post/tags ?tag]] db)
        (map name)))

(defn render-page [context page]
  (let [blog-posts (get-blog-posts (:app/db context))]
    (layout/layout
     {:title "Lebenswelt (space)"}
     [:article.prose.mx-auto
      (md/render-html (:page/body page))
      [:h2 [:i18n ::recent-posts {:n (count blog-posts)}]]
      [:ul.nobullets
       (for [blog-post (take 5 (get-blog-posts (:app/db context)))]
         [:li
          [:h3
           [:small (:blog-post/date-created blog-post)]
           " - "
           [:a {:href (:page/uri blog-post)} (:page/title blog-post)]]
          [:p (:blog-post/description blog-post)]])
       [:a.nodeco {:href "/blog/"} [:i18n ::blog-posts {:n (count blog-posts)}]]]
      [:h3.text-center {:id "Topics"} "Topics"]
      [:ul.tagcloud
       (for [tag (get-tags (:app/db context))]
         [:li [:a {:id (str "tag_" tag) :href (str "/tag/" tag "/")} tag]])]])))

