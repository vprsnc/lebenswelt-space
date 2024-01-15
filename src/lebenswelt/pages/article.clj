(ns lebenswelt.pages.article
  (:require [powerpack.markdown :as md]
            [lebenswelt.layout :as layout]))

(defn render-page [context page]
  (layout/layout
   {}
;;   layout/header
   [:article.prose.mx-auto
    [:p
     [:ul.info]]
    (md/render-html (:page/body page))
    layout/footer]))
