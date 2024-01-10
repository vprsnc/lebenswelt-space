:page/title Dealing with faulty csv's with Clojure, Duckdb, and Parquet
:blog-post/description Processing badly formatted, heavy csv files so they take 7 times less storage, and read 30 times faster!
:blog-post/tags [:clojure :data-processing]
:blog-post/author {:person/id :georgy}
:blog-post/date-created 2024-01-09
:blog-post/last-updated 2024-01-09
:open-graph/title Clojure data-processing
:open-graph/description Processing badly formatted heavy csv files with Clojure Duckdb Parquet
:page/body


```{Clojure}
(+ 1 1)
```

```
684M    data/6166_new/

89M     ./transformed.parquet
```

```
19M     data/6166_new/apr_0-600000.csv
20M     data/6166_new/apr_1200001-1800000.csv
20M     data/6166_new/apr_1800001-2400000.csv
20M     data/6166_new/apr_2400001-3000000.csv
20M     data/6166_new/apr_3000001-3600000.csv
14M     data/6166_new/apr_3600001-4029497.csv
22M     data/6166_new/apr_600001-1200000.csv
22M     data/6166_new/aug_0-600000.csv
26M     data/6166_new/aug_1200001-1800000.csv
22M     data/6166_new/aug_1800001-2400000.csv
22M     data/6166_new/aug_2400001-3000000.csv
22M     data/6166_new/aug_3000001-3600000.csv
16M     data/6166_new/aug_3600001-4029497.csv
22M     data/6166_new/aug_600001-1200000.csv
20M     data/6166_new/jul_1200001-1800000.csv
20M     data/6166_new/jul_1800001-2400000.csv
20M     data/6166_new/jul_3000001-3600000.csv
14M     data/6166_new/jul_3600001-4029497.csv
16M     data/6166_new/jul_600001-1200000.csv
16M     data/6166_new/jun_0-600000.csv
22M     data/6166_new/jun_2400001-3000000.csv
22M     data/6166_new/jun_3000001-3600000.csv
16M     data/6166_new/jun_3600000-4029497.csv
15M     data/6166_new/jun_600001-1200000.csv
18M     data/6166_new/may_0-600000.csv
21M     data/6166_new/may_1200001-1800000.csv
21M     data/6166_new/may_1800001-2400000.csv
21M     data/6166_new/may_2400001-3000000.csv
24M     data/6166_new/may_3000001-3600000.csv
18M     data/6166_new/may_600001-1200000.csv
16M     data/6166_new/sep_0-600000.csv
19M     data/6166_new/sep_1200001-1800000.csv
23M     data/6166_new/sep_1800001-2400000.csv
22M     data/6166_new/sep_3000001-3600000.csv
13M     data/6166_new/sep_3600001-4029497.csv
16M     data/6166_new/sep_600001-1200000.csv
```
