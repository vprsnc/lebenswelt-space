(ns analytics-abc.core
  (:require [m1p.core :as m1p]
            [analytics-abc.ingest :as ingest]
            [analytics-abc.pages :as pages]))

(defn pluralize [opt n & plurals]
  (let [np (second (first n))]
      (->
       (nth plurals (min (if (number? np) np 0) (dec (count plurals))))
       (m1p/interpolate-string n opt))))

(def config
  {:site/title "analytics-abc"
   :powerpack/render-page #'pages/render-page
   :powerpack/create-ingest-tx #'ingest/create-tx

   :optimus/bundles {"app.css"
                     {:public-dir "public"
                      :paths ["/styles.css"]}}
   
   :optimus/assets [{:public-dir "public"
                     :paths [#".*\.jpg"]}]

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
   
   :m1p/dictionaries {:ru ["src/analytics_abc/i18n/ru.edn"]
                      :en ["src/analytics_abc/i18n/en.edn"]}
   
   :m1p/dictionary-fns {:fn/plural #'pluralize}})
