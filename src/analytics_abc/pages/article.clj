(ns analytics-abc.pages.article
  (:require [powerpack.markdown :as md]
            [analytics-abc.layout :as layout]))

(defn render-page [context page]
  (layout/layout
   {}
   layout/header
   [:article.prose.mx-auto
    (md/render-html (:page/body page))]))
