:page/title Dealing with faulty csv's with Clojure, Duckdb, and Parquet
:blog-post/description Processing badly formatted, heavy csv files so they take 7 times less storage, and read 30 times faster!
:blog-post/tags [:clojure :data-processing]
:blog-post/author {:person/id :georgy}
:blog-post/date-created 2024-01-09
:blog-post/last-updated 2024-01-09
:open-graph/title Clojure data-processing
:open-graph/description Processing badly formatted heavy csv files with Clojure Duckdb Parquet
:page/body

# Dealing with faulty csv's with Clojure, Duckdb, and Parquet


```bash
$ du -h data/6166_new/

=> 684M    data/6166_new/
```

```bash
$ du -h data/6166_new/*.csv

=> 19M     data/6166_new/apr_0-600000.csv
   20M     data/6166_new/apr_1200001-1800000.csv
   22M     data/6166_new/aug_0-600000.csv
   26M     data/6166_new/aug_1200001-1800000.csv
   20M     data/6166_new/jul_1200001-1800000.csv
   20M     data/6166_new/jul_1800001-2400000.csv
   15M     data/6166_new/jun_600001-1200000.csv
   18M     data/6166_new/may_0-600000.csv
   21M     data/6166_new/may_1200001-1800000.csv
   16M     data/6166_new/sep_0-600000.csv
   19M     data/6166_new/sep_1200001-1800000.csv
   23M     data/6166_new/sep_1800001-2400000.csv...
```

```Clojure
(ns marketing.transform-to-parquet
   (:require [tablecloth.api :as tc]
             [clojure.java.io :as io]
             [clojure.string :as str]
             [tmducken.duckdb :as duckdb]))
```

```Clojure
(take 5 (into [] (file-seq (io/file "data/6166_new"))))

;; => (#object[java.io.File 0x3d7bfda4 "data/6166_new"]
;;     #object[java.io.File 0x5cb490a9 "data/6166_new/sep_600001-1200000.csv"]
;;     #object[java.io.File 0x7ee979c "data/6166_new/jun_600001-1200000.csv"]
;;     #object[java.io.File 0x12ce241 "data/6166_new/aug_1800001-2400000.csv"]
;;     #object[java.io.File 0x47c2784f "data/6166_new/sep_0-600000.csv"])
```

```Clojure
(def files (rest (file-seq (io/file "data/6166_new"))))
;; => #'marketing.transform-to-parquet/files
``` 

```Clojure
(.getName (first files))

;; => "sep_600001-1200000.csv"
```

```Clojure
(.getName (first files))

;; => "sep_600001-1200000.csv"
```

```Clojure
(.getPath (first files))
;; => "data/6166_new/sep_600001-1200000.csv"
``` 

```Clojure
(-> (first files) .getPath tc/dataset
    tc/head)

;; => data/6166_new/sep_600001-1200000.csv [5 12]:
;;    
;;  | ﻿ID юзера | Страна регистрации | ID агента | ID субагента | Сумма депозитов, EUR | Количество депозитов |
;;  |----------:|--------------------|----------:|-------------:|----------------------|---------------------:|
;;  | 457696471 |          Argentina |       575 |        16464 |                13,95 |                   11 |
;;  | 358081755 |         Azerbaijan |       944 |        14137 |                68,19 |                   25 |
;;  |  44557537 |             Russia |       649 |        13984 |                 6,56 |                    2 |
;;  | 411559137 |            Senegal |       781 |        13745 |                 1,52 |                    2 |
;;  | 418899171 |         Bangladesh |       279 |         3878 |                 0,00 |                    0 |
```

```Clojure
(def colnames
  {"﻿ID юзера" :user_id
   "﻿\"ID юзера\"" :user_id
   "Страна регистрации" :country
   "Агент" :agent
   "ID агента" :agent_id
   "Субагент" :subagent
   "ID субагента" :subagent_id
   "Сумма депозитов, EUR" :deps_eur
   "Количество депозитов" :deps_count
   "Сумма Выплат, EUR" :outs_eur
   "Количество Выплат" :outs_count
   "Сумма Чарджбеков, EUR" :chbk_eur
   "Количество Чарджбеков" :chbk_count
   "Сумма Рефанд, EUR" :rfnd_eur
   "Количество Рефанд" :rfnd_count
   "Сумма комиссий за пополнения (% и фикса вместе)" :coms_deps
   "Сумма комиссий за выводы (% и фикса вместе)" :coms_outs})

;; => #'marketing.transform-to-parquet/colnames
```

```Clojure
(def ds
  (->
   (first files)
   .getPath
   tc/dataset
   (tc/rename colnames)))

(-> ds  tc/head)

;; => ds [5 15]:
;;    
;;  |  :user_id |    :country | :agent_id | :subagent_id | :deps_eur | :deps_count |
;;  |----------:|-------------|----------:|-------------:|-----------|------------:|
;;  | 457696471 |   Argentina |       575 |        16464 |     13,95 |          11 |
;;  | 358081755 |  Azerbaijan |       944 |        14137 |     68,19 |          25 |
;;  |  44557537 |      Russia |       649 |        13984 |      6,56 |           2 |
;;  | 411559137 |     Senegal |       781 |        13745 |      1,52 |           2 |
;;  | 418899171 |  Bangladesh |       279 |         3878 |      0,00 |           0 |
```

```Clojure
(-> ds :country frequencies)

  ;; => {"Myanmar" 3167,
  ;;     "Maldives" 2,
  ;;     "Norway" 1,
  ;;     "South Ossetia" 1,
  ;;     "Kuweit" 11,
  ;;     ...}
  )
```

```Clojure
(defn transform-file! [file-name]
  (let [filepath (.getPath file-name)
        month-name (-> (.getName file-name)
                       (str/split #"_")
                       first)
        replace-comma (fn [colname]
                        (if (string? colname)
                          (Double. (str/replace colname "," "."))
                          (double colname)))]
    (println "Processing file: " (.getName file-name))
    (-> (tc/dataset filepath {:bad-row-policy :skip
                              :dataset-name "ds"})
        (tc/rename-columns colnames)
        (tc/add-column :month month-name :cycle)
        (tc/map-rows
          (fn replace-commas
            [{:keys [deps_eur outs_eur chbk_eur rfnd_eur coms_deps coms_outs]}]
            {:deps_eur (replace-comma deps_eur)
             :outs_eur (replace-comma outs_eur)
             :chbk_eur (replace-comma chbk_eur)
             :rfnd_eur (replace-comma rfnd_eur)
             :coms_deps (replace-comma coms_deps)
             :coms_outs (replace-comma coms_outs)}))
        (tc/drop-columns #(= :boolean %) :datatype))))
```

```Clojure
(def transformed-files (pmap transform-file! files)) 

(def ds (apply tc/concat transformed-files))
   
(tc/info ds)
;; => ds: descriptive-stats [29 12]:
;;    
;;  |    :col-name | :datatype | :n-valid | :n-missing |
;;  |--------------|-----------|---------:|-----------:|
;;  |     :user_id |    :int32 |  3410291 |          0 |
;;  |     :country |   :string |  3410291 |          0 |
;;  |    :agent_id |   :object |  3410291 |          0 |
;;  | :subagent_id |   :object |  3410291 |          0 |
;;  |    :deps_eur |  :float64 |  3410291 |          0 |
;;  |  :deps_count |    :int16 |  3410291 |          0 |
;;  |    :outs_eur |  :float64 |  3410291 |          0 |
;;  |  :outs_count |    :int16 |  3410291 |          0 |
;;  |    :chbk_eur |  :float64 |  3410291 |          0 |
;;  |  :chbk_count |    :int16 |  3410291 |          0 |
;;  |    :rfnd_eur |  :float64 |  3410291 |          0 |
;;  |  :rfnd_count |    :int16 |  3410291 |          0 |
;;  |   :coms_deps |  :float64 |  3410291 |          0 |
;;  |   :coms_outs |  :float64 |  3410291 |          0 |
;;  |       :month |   :string |  3410291 |          0 |
```

```Clojure
(-> ds
    (tc/select-rows (fn [row] (and
                               (int? (:agent_id row))
                               (int? (:subagent_id row)))))
    (tc/convert-types :type/object :int64)
    (tc/info ds))

;; => ds: descriptive-stats [29 12]:
;;    
;;  |    :col-name | :datatype | :n-valid | :n-missing |
;;  |--------------|-----------|---------:|-----------:|
;;  |     :user_id |    :int32 |  3410291 |          0 |
;;  |     :country |   :string |  3410291 |          0 |
;;  |    :agent_id |    :int64 |  3410291 |          0 |
;;  | :subagent_id |    :int64 |  3410291 |          0 |
;;  |    :deps_eur |  :float64 |  3410291 |          0 |
;;  |  :deps_count |    :int16 |  3410291 |          0 |
;;  |    :outs_eur |  :float64 |  3410291 |          0 |
;;  |  :outs_count |    :int16 |  3410291 |          0 |
;;  |    :chbk_eur |  :float64 |  3410291 |          0 |
;;  |  :chbk_count |    :int16 |  3410291 |          0 |
;;  |    :rfnd_eur |  :float64 |  3410291 |          0 |
;;  |  :rfnd_count |    :int16 |  3410291 |          0 |
;;  |   :coms_deps |  :float64 |  3410291 |          0 |
;;  |   :coms_outs |  :float64 |  3410291 |          0 |
;;  |       :month |   :string |  3410291 |          0 |
```



```Clojure
(defn transform-file! [file-name]
  (let [filepath (.getPath file-name)
        month-name (-> (.getName file-name)
                       (str/split #"_")
                       first)
        replace-comma (fn [colname]
                        (if (string? colname)
                          (Double. (str/replace colname "," "."))
                          (double colname)))]
    (println "Processing file: " (.getName file-name))
    (-> (tc/dataset filepath {:bad-row-policy :skip
                              :dataset-name "ds"})
        (tc/rename-columns colnames)
        (tc/add-column :month month-name :cycle)
        (tc/select-rows (fn [row] (and
                                   (int? (:agent_id row))
                                   (int? (:subagent_id row)))))
        (tc/convert-types :type/object :int64)
         (tc/map-rows
          (fn replace-commas
            [{:keys [deps_eur outs_eur chbk_eur rfnd_eur coms_deps coms_outs]}]
            {:deps_eur (replace-comma deps_eur)
             :outs_eur (replace-comma outs_eur)
             :chbk_eur (replace-comma chbk_eur)
             :rfnd_eur (replace-comma rfnd_eur)
             :coms_deps (replace-comma coms_deps)
             :coms_outs (replace-comma coms_outs)})))))
```

```Clojure
(duckdb/initialize!)
;; => true
    
(def db (duckdb/open-db))
;; => #'marketing.transform-to-parquet/db
  
  
(def conn (duckdb/connect db))
;; => #'marketing.transform-to-parquet/conn
  
  
(duckdb/create-table! conn ds)
;; => "ds"

(duckdb/insert-dataset! conn ds)
;; => 3410291
```

```Clojure
(duckdb/sql->dataset
 conn
 "SELECT COUNT() FROM ds")
;; => :_unnamed [1 1]:
;;    
;;  | count_star() |
;;  |-------------:|
;;  |      3410291 |
```

```Clojure
(tc/head (duckdb/sql->dataset
          conn
          "SELECT * EXCLUDE (agent, subagent) FROM ds"))

;; => :_unnamed [5 15]:
;;    
;;  |   user_id |     country | agent_id | subagent_id | deps_eur | deps_count |
;;  |----------:|-------------|---------:|------------:|----------|-----------:|
;;  | 457696471 |   Argentina |      575 |       16464 |    13.95 |         11 |
;;  | 358081755 |  Azerbaijan |      944 |       14137 |    68.19 |         25 |
;;  |  44557537 |      Russia |      649 |       13984 |     6.56 |          2 |
;;  | 411559137 |     Senegal |      781 |       13745 |     1.52 |          2 |
;;  | 418899171 |  Bangladesh |      279 |        3878 |     0.00 |          0 |
```

```Clojure
(defn transform-directory! [path]
  (when (not (duckdb/initialized?)) (duckdb/initialize!))
  (let  [files (rest (file-seq (io/file path)))
         transformed-files (pmap transform-file! files)
         db (duckdb/open-db)
         conn (duckdb/connect db)
         ds (apply tc/concat transformed-files)]
    (duckdb/create-table! conn ds)
    (duckdb/insert-dataset! conn ds)
    (duckdb/run-query!
     conn
     "COPY (SELECT * FROM ds) TO 'transformed.parquet' (FORMAT PARQUET)")))
```

```Clojure
(transform-directory! "data/6166_new")
```


```bash
$ du -h ./transformed.parquet

=> 89M     ./transformed.parquet
```

```Clojure
(ns marketing.transform-to-parquet
   (:require [tablecloth.api :as tc]
             [clojure.java.io :as io]
             [clojure.string :as str]
             [tmducken.duckdb :as duckdb]))

(def colnames
  {"﻿ID юзера" :user_id
   "﻿\"ID юзера\"" :user_id
   "Страна регистрации" :country
   "Агент" :agent
   "ID агента" :agent_id
   "Субагент" :subagent
   "ID субагента" :subagent_id
   "Сумма депозитов, EUR" :deps_eur
   "Количество депозитов" :deps_count
   "Сумма Выплат, EUR" :outs_eur
   "Количество Выплат" :outs_count
   "Сумма Чарджбеков, EUR" :chbk_eur
   "Количество Чарджбеков" :chbk_count
   "Сумма Рефанд, EUR" :rfnd_eur
   "Количество Рефанд" :rfnd_count
   "Сумма комиссий за пополнения (% и фикса вместе)" :coms_deps
   "Сумма комиссий за выводы (% и фикса вместе)" :coms_outs})

(defn transform-file! [file-name]
  (let [filepath (.getPath file-name)
        month-name (-> (.getName file-name)
                       (str/split #"_")
                       first)
        replace-comma (fn [colname]
                        (if (string? colname)
                          (Double. (str/replace colname "," "."))
                          (double colname)))]
    (println "Processing file: " (.getName file-name))
    (-> (tc/dataset filepath {:bad-row-policy :skip
                              :dataset-name "ds"})
        (tc/rename-columns colnames)
        (tc/add-column :month month-name :cycle)
        (tc/select-rows (fn [row] (and
                                   (int? (:agent_id row))
                                   (int? (:subagent_id row)))))
        (tc/convert-types :type/object :int64)
         (tc/map-rows
          (fn replace-commas
            [{:keys [deps_eur outs_eur chbk_eur rfnd_eur coms_deps coms_outs]}]
            {:deps_eur (replace-comma deps_eur)
             :outs_eur (replace-comma outs_eur)
             :chbk_eur (replace-comma chbk_eur)
             :rfnd_eur (replace-comma rfnd_eur)
             :coms_deps (replace-comma coms_deps)
             :coms_outs (replace-comma coms_outs)})))))

(defn transform-directory! [path]
  (when (not (duckdb/initialized?)) (duckdb/initialize!))
  (let  [files (rest (file-seq (io/file path)))
         transformed-files (pmap transform-file! files)
         db (duckdb/open-db)
         conn (duckdb/connect db)
         ds (apply tc/concat transformed-files)]
    (duckdb/create-table! conn ds)
    (duckdb/insert-dataset! conn ds)
    (duckdb/run-query!
     conn
     "COPY (SELECT * FROM ds) TO 'transformed.parquet' (FORMAT PARQUET)")))

(transform-directory! "data/6166_new")
```
