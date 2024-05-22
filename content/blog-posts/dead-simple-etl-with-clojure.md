:page/title A dead simple ETL with Clojure (no JDK required)
:blog-post/description
:blog-post/tags [:clojure :data-processing]
:blog-post/author {:person/id :georgy}
:blog-post/date-created 2024-05-02
:blog-post/last-updated 2024-05-02
:open-graph/title A dead simple ETL with Clojure (no JDK required)
:open-graph/description
:page/body

# A dead simple ETL with Clojure (no JDK required)

<div align="center">

![](/images/data-everywhere.jpg)

</div>

With that article, I would like to start a series of such. It will be about building a data app with Clojure, from data collection to visualization, 

## Extract

```Clojure
(require '[babashka.curl :as curl]
         '[clojure.walk :as walk]
         '[cheshire.core :as json]
         '[clojure.string]
         '[babashka.fs :as fs]
         '[clojure.math :refer [ceil]])
```


```Clojure
(def today
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd")
           (new java.util.Date)))
```

```Clojure
(def currencies
  ["ARS" "EUR" "USD" "AED" "AUD" "BDT" "BHD" "BIF" "BOB" "BRL" "CAD"
   "CLP" "CNY" "COP" "CRC" "CZK" "DOP" "DZD" "EGP" "GBP" "GEL" "GHS"
   "HKD" "IDR" "INR" "JPY" "KES" "KHR" "KRW" "KWD" "KZT" "LAK" "LBP"
   "LKR" "MAD" "MMK" "MXN" "MYR" "NGN" "OMR" "SGD" "PAB" "PEN" "PHP"
   "PKR" "PLN" "PYG" "QAR" "RON" "ETB" "SAR" "SDG" "SEK" "SGD" "THB"
   "TND" "TRY" "IQD" "TWD" "UAH" "UGX" "UYU" "VES" "VND" "ZAR" "NPR"
   "UZS" "BDT" "XOF" "SOS" "MNT" "IRR" "MAD" "XAF" "LKR" "PKR" "AUD"
   "BGN" "BRL" "CHF" "CLP" "COP" "CRC" "CZK" "DKK" "HUF" "ILS" "ISK"
   "JOD" "JPY" "KES" "KRW" "MYR" "NGN" "NOK" "NZD" "PLN" "QAR" "RON"
   "RSD" "SEK" "SGD" "THB" "TND" "TRY" "TWD" "ZAR" "BGN" "CHF" "CRC"
   "DKK" "HUF" "ILS" "ISK" "KRW" "MYR" "NOK" "NZD" "RON" "RSD" "SEK"])
```


```Clojure
(defn generate-payload
  [curr page]
  {:page page
   :rows 20
   :asset "USDT"
   :tradeType "BUY"
   :fiat curr
   :merchantCheck true})
```

```Clojure
(defn get-request
  [curr page]
  (let [response (curl/post
                  "https://p2p.binance.com/bapi/c2c/v2/friendly/c2c/adv/search"
                  {:throw false  ;; not to throw exception when status>400
                   :headers {:content-type "application/json"
                             :accept "application/json"}
                   :body (json/encode (generate-payload curr page))})
        status (:status response)
        status=? (partial = status)]
    (cond (status=? 200) response
          (status=? 429) (do
                           (println "Sleeping 3s...")
                           (Thread/sleep 3000)
                           (recur curr page))
          :else (do (println "Something went wrong...")
                    (println (:err response))))))
```

```Clojure
(defn parse-response
  [resp]
  (-> (:body resp)
      json/parse-string
      clojure.walk/keywordize-keys))
```

## Transform

## Load
