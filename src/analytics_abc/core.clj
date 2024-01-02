(ns analytics-abc.core
  (:require [analytics-abc.ingest :as ingest]
            [analytics-abc.pages :as pages]))

(def config
  {:site/title "analytics-abc"
   :powerpack/render-page #'pages/render-page
   :powerpack/create-ingest-tx #'ingest/create-tx
   :optimus/bundles {"app.css"
                     {:public-dir "public"
                      :paths ["/styles.css"]}}})
