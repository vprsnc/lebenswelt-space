(ns analytics-abc.core)

(defn render-page [context page]
  "<h1>Hello world</h1>")

(def config
  {:site/title "analytics-abc"
   :powerpack/render-page #'render-page})
