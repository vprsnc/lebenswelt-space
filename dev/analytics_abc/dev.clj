(ns analytics-abc.dev
  (:require [analytics-abc.core :as blog]
            [analytics-abc.dev :as dev]))

(defmethod dev/configure! :default []
  blog/config)  ;; 1

(comment

  (dev/start)   ;; 2
  (dev/stop)    ;; 3
  (dev/reset)   ;; 4

  (dev/get-app) ;; 5

  )
