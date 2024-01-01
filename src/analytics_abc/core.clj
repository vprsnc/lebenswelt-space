(ns analytics-abc.core
  (:require [powerpack.markdown :as md]))

(defn render-frontpage [context page]
  [:html
   [:head
    [:title "The Powerblog"]]
   [:body
    (md/render-html (:page/body page))
    [:h2 "Blog posts"]]])

(defn render-page [context page]
  (cond
    (= "/" (:page/uri page))
    (render-frontpage context page)

    :else
    (md/render-html (:page/body page))))

(def config
  {:site/title "analytics-abc"
   :powerpack/render-page #'render-page})
