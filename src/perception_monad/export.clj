(ns perception-monad.export
  (:require [perception-monad.core :as blog]
            [powerpack.export :as export]))

(defn ^:export export! [& args]
  (-> blog/config
      (assoc :site/base-url "https://perception-monad.xyz")
       export/export!))
