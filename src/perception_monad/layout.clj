(ns perception-monad.layout)

(defn layout [{:keys [title]} & content]
  [:html {:data-theme "retro"}
   [:head
    (when title [:title title])]
   [:body.py-8
    content]])

(def header
  [:header.mx-auto.prose.mb-8
   [:a {:href "/"} "perception-monad "]])  ;; TODO

(comment
  [:nav
   [:ul
    [:li [:a {:href "/"} "perception-monad "]]
    [:li [:a {:href "/"} "test"]]]]
  )
