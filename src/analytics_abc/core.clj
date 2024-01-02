(ns analytics-abc.core
  (:require [powerpack.markdown :as md]
            [datomic.api :as d]))

(defn create-tx [file-name txes]
  (cond->> txes
    (re-find #"^blog-posts/" file-name)
    (map #(assoc % :page/kind :page.kind/blog-post))))

(defn get-blog-posts [db]
  (->> (d/q '[:find [?e ...]
              :where
              [?e :blog-post/author]]
            db)
       (map #(d/entity db %))))

(defn render-frontpage [context page]
  [:html
   [:head
    [:title "The Powerblog"]]
   [:body
    (md/render-html (:page/body page))
    [:h2 "Blog posts"]
    [:ul
     (for [blog-post (get-blog-posts (:app/db context))]
       [:li [:a {:href (:page/uri blog-post)} (:page/title blog-post)]])]]])

(defn render-page [context page]
  (cond
    (= "/" (:page/uri page))
    (render-frontpage context page)

    :else
    [:html [:body (md/render-html (:page/body page))]]))

(def config
  {:site/title "analytics-abc"
   :powerpack/render-page #'render-page
   :powerpack/create-ingest-tx #'create-tx})
