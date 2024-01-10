(ns perception-monad.core
  (:require [m1p.core :as m1p]
            [perception-monad.ingest :as ingest]
            [perception-monad.pages :as pages]))

(defn pluralize [opt n & plurals]
  (let [np (second (first n))]
      (->
       (nth plurals (min (if (number? np) np 0) (dec (count plurals))))
       (m1p/interpolate-string n opt))))

(def config
  {:site/title "perception-monad"
   :powerpack/render-page #'pages/render-page
   :powerpack/create-ingest-tx #'ingest/create-tx
   :powerpack/on-ingested #'ingest/on-ingested

   :optimus/bundles {"app.css"
                     {:public-dir "public"
                      :paths ["/styles.cssg"]}}
   
   :optimus/assets [{:public-dir "public"
                     :paths [#".*\.jpg" #".\.woff2"]}]

   :imagine/config {:prefix "image-assets"
                    :resource-path "public"
                    :disk-cache? true
                    :transformations
                    {:preview-small
                     {:transformations [[:fit {:width 184 :height 184}]
                                        [:crop {:preset :square}]]
                      :retina-optimized? true
                      :retina-quality 0.4
                      :width 184}}}
   
   :m1p/dictionaries {:ru ["src/perception_monad/i18n/ru.edn"]
                      :en ["src/perception_monad/i18n/en.edn"]}
   
   :m1p/dictionary-fns {:fn/plural #'pluralize}})
