(ns lebenswelt.layout)

(def header
  [:div.navbar.bg-secondary.content
   [:buttion.btn.btn-ghost.text-xl [:a {:href "/"} "lebenswelt space"]]])

(def footer
  [:div.text-center.prose.mx-auto
   [:p [:a {:href "/"} "https://lebenswelt.space"]]
   [:p "This page is licensed under "
    [:a
     {:href "http://creativecommons.org/licenses/by-nc-sa/4.0/?ref=chooser-v1"}
     "BY-NC-SA 4.0"]]])

(defn layout [{:keys [title]} & content]
  [:html {:data-theme "retro"}
   [:head
    [:script "localStorage.theme = 'light'"]
    (when title [:title title])]
   [:body
;;    header
    [:script {:src "/prism.js"}]
    [:div.py-8 content]
    [:div.py-8 footer]]])


