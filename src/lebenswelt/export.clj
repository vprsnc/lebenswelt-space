(ns lebenswelt.export
  (:require [lebenswelt.core :as blog]
            [powerpack.export :as export]))

(defn ^:export export! [& args]
  (-> blog/config
      (assoc :site/base-url "https://lebenswelt.space")
       export/export!))
