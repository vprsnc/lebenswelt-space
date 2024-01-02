(ns analytics-abc.pages
   (:require [powerpack.markdown :as md]
             [datomic.api :as d]))

(defn get-blog-posts [db]
  (->> (d/q '[:find [?e ...]
              :where
              [?e :blog-post/author]]
            db)
       (map #(d/entity db %))))

(defn layout [{:keys [title]} & content]
  [:html.dark:bg-zinc-900
   [:head
    (when title [:title title])]
   [:body.py-8
    content]])

(def header
  [:header.mx-auto.dark:prose-invert.prose.mb-8
   [:a {:href "/"} "analytics-abc"]])

(defn render-frontpage [context page]
  (let [blog-posts (get-blog-posts (:app/db context))]
     (layout
      {:title "analytics-abc"}
      [:article.prose.dark:prose-invert.mx-auto
       (md/render-html (:page/body page))
       [:h2 [:i18n ::blog-posts {:n (count blog-posts)}]]
       [:ul
        (for [blog-post (get-blog-posts (:app/db context))]
          [:li [:a {:href (:page/uri blog-post)} (:page/title blog-post)]])]])))

(defn render-article [context page]
  (layout
   {}
   header
   [:article.prose.dark:prose-invert.mx-auto
    (md/render-html (:page/body page))]))

(defn render-blog-post [context page]
  (render-article context page))

(defn render-page [context page]
  (case (:page/kind page)
    :page.kind/frontpage (render-frontpage context page)
    :page.kind/blog-post (render-blog-post context page)
    :page.kind/article (render-article context page)))
