(ns analytics-abc.dev
  (:require [analytics-abc.core :as blog]
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

  (->> (d/entity db [:page/uri "/blog-posts/first-post/"])
       :blog-post/tags
       (into []))
  ;; => [:climbing :nature]
  
  )
