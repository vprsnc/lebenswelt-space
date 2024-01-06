(ns analytics-abc.layout)

(defn layout [{:keys [title]} & content]
  [:html.dark:bg-zinc-900
   [:head
    (when title [:title title])]
   [:body.py-8
    content]])

(def header
  [:header.mx-auto.dark:prose-invert.prose.mb-8
   [:a {:href "/"} "analytics-abc "]])  ;; TODO

(comment
  [:nav
   [:ul
    [:li [:a {:href "/"} "analytics-abc "]]
    [:li [:a {:href "/"} "test"]]]]
  )
