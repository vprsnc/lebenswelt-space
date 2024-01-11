pd(ns lebenswelt.dev
  (:require [lebenswelt.core :as blog]
            [powerpack.dev :as dev]))

(defmethod dev/configure! :default []
  blog/config)

(comment

  (set! *print-namespace-maps* false)

  (dev/start)
  (dev/stop)
  (dev/reset)

  (def app (dev/get-app))

  (require '[datomic.api :as d])

  (def db (d/db (:datomic/conn app)))

  (->> (d/entity db [:page/uri "/blog-posts/first-post/"])
       :blog-post/author
       (into {}))
  ;; => {:person/id :georgy, :person/full-name "Georgy Toporkov"}

  (->> (d/entity db [:page/uri "/blog-posts/path-to-this-website/"])
       :blog-post/tags
       (into []))
  ;; => [:climbing :nature]

  (-> (d/entity db [:page/uri "/blog-posts/path-to-this-website/"])
       (select-keys  [:blog-post/date-created
                      :blog-post/author])
       )

(reverse  (d/q '[:find [?e ...]
          :where
          [?e :blog-post/date-created ?d]]
        db))

  (d/q '[:find [?e ...]
         :where
         [?e :blog-post/date-created ?d]]
       db
       :result-transform (fn [result]
                           (sort-by (fn [d]
                                      (get d :blog-post/date-created)
                                      (reverse result)))))
  
  (defn get-tags []
    (->> (d/q '[:find [?e ...]
                :where
                [?e :blog-post/tags]]
              db)
         (map #(d/entity db %))
         (into [])))

 (d/q '[:find [?tag ...]
             :where
             [_ :blog-post/tags ?tag]] db)
 
  )
