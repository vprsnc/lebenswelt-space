(ns analytics-abc.export
  (:require [analytics-abc.core :as blog]
            [powerpack.export :as export]))

(defn ^:export export! [& args]
  (-> blog/config
      (assoc :site/base-url "https://analytics-abc.xyz")
       export/export!))
