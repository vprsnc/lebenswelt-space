(ns lebenswelt.layout)

(defn layout [{:keys [title]} & content]
  [:html {:data-theme "retro"}
   [:head
    (when title [:title title])]
   [:body
    [:script {:src "/prism.js"}]
    ;;    [:div.navbar.bg-primary [:a.btn.btn-ghosti "test"]]
    [:div.py-8 content]]])

(def header
  [:header.mx-auto.prose.mb-8
   [:a {:href "/"} "lebenswelt "]])  ;; TODO

(def footer
  [:div.text-center
   [:a {:href "/"} "https://lebenswelt.space"]])

(comment
  [:nav
   [:ul
    [:li [:a {:href "/"} "lebenswelt "]]
    [:li [:a {:href "/"} "test"]]]]
  )
