(ns analytics-abc.ingest)

(defn create-tx [file-name txes]
  (cond->> txes
    (re-find #"^blog-posts/" file-name)
    (map #(assoc % :page/kind :page.kind/blog-post))))
