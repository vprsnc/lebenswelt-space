:page/title Dealing with faulty csv's with Clojure, Duckdb, and Parquet
:blog-post/description Processing badly formatted, heavy csv files so they take 7 times less storage, and read 30 times faster!
:blog-post/tags [:clojure :data-processing]
:blog-post/author {:person/id :georgy}
:blog-post/date-created 2024-01-22
:blog-post/last-updated 2024-01-22
:open-graph/title Dealing with faulty csv's with Clojure, Duckdb, and Parquet
:open-graph/description Processing badly formatted heavy csv files with Clojure Duckdb Parquet
:page/body

# Dealing with out-of-memory faulty csv's with Clojure, Duckdb, and Parquet

<div align="center">

![](/images/data-fun.jpg)

</div>

Say, you are working on remote on your not-so-fancy laptop, and
get a zip archive with a bunch of csv files,
they say you need to upload them all to the database.
You unpack the archive and try to upload them directly.
Database immediately throws an error at you saying that in
the schema there are no such columns!

You start your investigation and see that all the column names are
in Cyrillic, columns are delimited with comma and moreover comma
is used as a decimal separator in floats...
Who's the guy who has prepared those files?!

You're beginning to realize that you cannot trust such sneaky folk,
and that you cannot upload those files without processing and a thorough check.
Renaming the columns is only 50% of the problem,
because your database is not able to read floats with commas,
and you don't have the rights to create a temporary table to process those.
You could use something like pandas, but files are heavy and won't fit 
in the memory, and processing'em in batches will take an eternity...

That's it, just your 8gb laptop, and a bunch of heavy faulty csv's.
But do not be discouraged for I know the ways out the trouble!

## Introduction

The first thing we need to do is `pip uninstall pandas`.

Just joking, but Pandas has lots of caveats,
it is rather fast for processing tabular data compared to bare Python,
but it is much slower than more modern tools,
it's not declarative and often unintuitive.
You can easily mess up the whole dataset with Pandas' setters,
and search for a place where you've made a mistake for hours!

Luckily, nothing lasts forever, and now we have much faster 
functional, declarative dataframe tools like
[Polars](https://pola.rs/),
which has a wonderful API for Python, and R Tideverse's
[data.table](https://rdatatable.gitlab.io/data.table/)
Anyway, both of them deserve their own articles
(and there are lots of them online),
but today we're going to look at Clojure's
[Tablecloth](https://github.com/scicloj/tablecloth).

Tablecloth is an API for another wonderful Clojure library -
[tech.ml.dataset](https://github.com/techascent/tech.ml.dataset), 
and it adds more of a data manipulation functionality.
It is *lazy* by default, which allows it to process out-of-memory files, 
and it does it fast!

*...Wait but why Clojure, in the first place?*

### What's Clojure?

<div class="disclaimer"

**DISCLAIMER**: This is not an introduction to Clojure language of any kind,
although some basic concepts are here, so, I believe, 
you will be fine reading it with basic knowledge of Python.
For Clojure itself, you'd probably like to check the resources at the end
of the section.


</div>

Well, it's a beautiful language, first of all, it is functional, persistent,
and clean.
There are precise definitions of functional programming languages out there,
basically, it means you are not dealing with the billion of objects, 
with a set of methods for each, so that each object is a kind of black box,
but you have a bunch of functions and a few data types you can use.
All that makes it easier to reason about, and it is perfect for
any task concerning working with data.

Another thing is that Clojure is a hosted language, 
it's built originally on JVM,
and you can use Java classes and methods,
and then compile it all to Java byte-code. And that makes it quite fast.

It is also possible to compile it to
[JavaScript](https://clojurescript.org/), 
add here [babashka](https://babashka.org/), and you'll get
a universal language for data-processing, backend, frontend, and scripting!

Finally, Clojure's got the
[REPL](https://clojure.org/guides/repl/introduction),
that means it is extremely interactive:
you can execute code on the fly without leaving an editor,
you can evaluate it line-by-line just like in R,
and you can then
[render it to a notebook format](https://scicloj.github.io/clay/)
using comment lines to write markdown.

If you'd like to learn more about how could you use Clojure for your data job,
I suggest you to check out these talks:

* `2023-12-13` [Cooking up a workflow for data](https://www.youtube.com/watch?v=skMMvxWjmNM)

* `2023-05-11` [Clojure in fintech ecosysetm](https://www.youtube.com/watch?v=QCxcLsxQeYs)

* `2023-05-12` [Clojure for data science in the Real World](https://www.youtube.com/watch?v=MguatDl5u2Q)

* `2023-10-11` [TMD 7.0 - Higher Performance Functional Data Science ](https://www.youtube.com/watch?v=WA5O7jNoNGE)

And about some resources about Clojure itself:

<div class="columns-2">

<div>

* [Scicloj website: data science for Clojrue](https://scicloj.github.io/)

* [Clojure for Brave and True](https://www.braveclojure.com/)

* [Babashka Babooka](https://www.braveclojure.com/quests/babooka/)

* [Learn ClojureScript](https://www.learn-clojurescript.com/)

</div>

<div>

* [Daniel Amber's Channel](https://www.youtube.com/@onthecodeagain)

* [Kelvin Mai's Channel](https://www.youtube.com/@KelvinMai)

* [Andrew Fedeev's Channel](https://www.youtube.com/@andrey.fadeev/)

</div>

</div>

### What's the other thing?

[Duckdb](https://duckdb.org/)
is pocket OLAP tool which is able to process
[billions of rows](https://techascent.com/blog/just-ducking-around.html)
right on your laptop, it is extremely fast, and easy to use.
It's vast functionality thought is beyond the topic of the article, 
as we'll be using it to write *parquet* without a dependency hassle 
in Clojure. You can check this article about
[building a poor man's datalake](https://dagster.io/blog/duckdb-data-lake)
with Duckdb.

Parquet is an efficient modern data-storing format, it takes
[much less time](https://www.databricks.com/glossary/what-is-parquet)
to read, and much less storage space.

## Getting started

First, we need to organize a Clojure project, that is dead simple:
we need a file to place our dependencies, and the file to write code in.

```bash
clj-data-processing/
├── data
├── deps.edn
└── src
    └── clj_data_processing
        └── transform_to_parquet.clj
```

This is how you would do it by a convention.
`deps.edn` file is where the dependencies and the setting for the
project will go, `edn` stands for *extensible data notation*,
and it is similar to `json` is some sort of sense.

<div id="info">

Note that you need to use *underscores* for your file names inside
a project. This is becasue of the JVM. In Clojure code though, 
you should use hyphens. 
Hyphens will be translated to underscores at the compilation time.

</div>

Hashmaps are surronded by `{}`, and they are pretty much the same 
what you call *dictionaries* in Python.
Keywords in Clojure have syntax of `:keyword` and are used everywhere,
we'll spot them often later.
Values can be anything: strings, vectors `[]`, or other hashmaps.

```Clojure
{:paths ["src" "data"] ;; that allows clojure to read from those dirs
 :deps {org.clojure/clojure {:mvn/version "1.10.3"}
        scicloj/tablecloth {:mvn/version "7.014"} ;; our dataframes
        com.techascent/tmducken {:mvn/version "0.8.1-12"} ;; duckdb
        }}
```

We need to install Duckdb for our project as well,
so that we're able to call it from Clojure.

```bash
curl -LO https://raw.githubusercontent.com/techascent/tmducken/main/scripts/enable-duckdb
sh ./enable-duckdb
```

Finally, let's unpack the zip file, and check the files.


```bash
unzip new_data.zip data/

du -h data/new_data/

=> 684M    data/6166_new/
```

```bash
du -h data/new_data/*.csv

=> 19M     data/new_data/apr_0-600000.csv
   20M     data/new_data/apr_1200001-1800000.csv
   22M     data/new_data/aug_0-600000.csv
   26M     data/new_data/aug_1200001-1800000.csv
   20M     data/new_data/jul_1200001-1800000.csv
   20M     data/new_data/jul_1800001-2400000.csv
   15M     data/new_data/jun_600001-1200000.csv
   18M     data/new_data/may_0-600000.csv
   21M     data/new_data/may_1200001-1800000.csv
   16M     data/new_data/sep_0-600000.csv
   19M     data/new_data/sep_1200001-1800000.csv
   23M     data/new_data/sep_1800001-2400000.csv...
```

## Reading the data 

Let's first talk a bit about syntax in Clojure. 
The main element of it are functions.
If you want to *call* a functions you surround it with *round* brackets.
So, if in Python you'd have `f(x)` in Clojure it is `(f x)`, simple as that.
Besides, you don't need *commas* in Clojure, you can put them,
but they will be treated as a *white space*.

Our first function will be `ns` for declaring a 
[namespace](https://clojure.org/reference/namespaces),
namespaces allow you to import your own code from another file in the project.

```Clojure
(ns data-processing.transform-to-parquet
   (:require [tablecloth.api :as tc]
             [clojure.java.io :as io]
             [clojure.string :as str]
             [tmducken.duckdb :as duckdb]))
```

For reading and writing files Clojure uses I/O utility, 
to read files in a directory we can use `file-seq` function.
We're going to read the directory and check the first 5 files.

```Clojure
(take 5 (into [] (file-seq (io/file "data/new_data"))))

;; => (#object[java.io.File 0x3d7bfda4 "data/new_data"]
;;     #object[java.io.File 0x5cb490a9 "data/new_data/sep_600001-1200000.csv"]
;;     #object[java.io.File 0x7ee979c "data/new_data/jun_600001-1200000.csv"]
;;     #object[java.io.File 0x12ce241 "data/new_data/aug_1800001-2400000.csv"]
;;     #object[java.io.File 0x47c2784f "data/new_data/sep_0-600000.csv"])
```

As you can see, the first entry contains directory itself, 
to take all but first elements there's function `rest`.
Let's put assign our files to a variable with `def`.
That's right, in Clojure `def` assigns to variable, 
to define a function `defn` is used we'll see it later.

```Clojure
(def files (rest (file-seq (io/file "data/new_data"))))
;; => #'data-processing.transform-to-parquet/files
``` 

Did you notice that `file-seq` function returned us a bunch of Java objects?
We can get actual file names and paths of them by calling Java function
from Clojure.

This sort of functions is prefixed with a dot (`.`),
let's call a couple of them:

```Clojure
(.getName (first files))

;; => "sep_600001-1200000.csv"
```


```Clojure
(.getPath (first files))
;; => "data/new_data/sep_600001-1200000.csv"
```

Most plausibly, you also have noticed that we're putting functions one into 
another, Clojure deals with that just fine, 
but when there will be more functions, it will be hard to read the code.

That's where *pipe macros* come into play. 
You call them as a function, and they will pass the output of the function
just like *pipe operator* (`|>` or `%>%`) in R.
`->` will put the output as the *first* argument,
and `->>` -- as the last one.
So `(first files)` is the same as `(-> files first)`.
Note that when *there are no other arguments* in a function
we can skip the parenthesies.

At last we can read our data to a *dataframe*, 
we're going to call some functions from the `tablecloth`.
To call the function from the other namespace, you use the name of that 
namespace and `/` to get the insides. 
Just like the dot (`.`) in Python.

```Clojure
(-> files first .getPath tc/dataset tc/head)

;; => data/new_data/sep_600001-1200000.csv [5 12]:
;;    
;;  | ﻿ID юзера | Страна регистрации | ID агента | ID субагента | Сумма депозитов, EUR | Количество депозитов |
;;  |----------:|--------------------|----------:|-------------:|----------------------|---------------------:|
;;  | 457696471 |          Argentina |       575 |        16464 |                13,95 |                   11 |
;;  | 358081755 |         Azerbaijan |       944 |        14137 |                68,19 |                   25 |
;;  |  44557537 |             Russia |       649 |        13984 |                 6,56 |                    2 |
;;  | 411559137 |            Senegal |       781 |        13745 |                 1,52 |                    2 |
;;  | 418899171 |         Bangladesh |       279 |         3878 |                 0,00 |                    0 |
```

And it worked!
Now we can see our why our columns are causing trouble and rename it.
For that we can pass a hash map with the old column names as *keys* and 
new names as *values*.

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
   "Количество депозитов" :deps_count})

;; => #'data-processing.transform-to-parquet/colnames
```

Let's call `rename` function from the `tablecloth` namespace.
This function takes to arguments, so we have to pack it into the parenthesies:

```Clojure
(def ds
  (-> files
      first
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

Keywords in Clojure have lots of uses,
one of them is that you can easily extract values by calling a keyword
as function, e.g. `(:key {:key "value"})` will return you `"value"`.

Same way, you can select columns from the `tc/dataset`:

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

One thing we've noticed,
is that our dataset does not have **year-month** month column,
so if we put our files in one dataset,
our data will become completely useless, as we won't be able
to make a difference... Some data-joker decided to put it just to a filename.

So, we need to extract the month from a filename. 
We'll split the file name by a underscore and take the first element with
the `split` function from `clojure.string`.

Function expects from us the string itself and a *pattern* to split a string on.
Patterns are denoted with syntactic sugar `#"\\d+"`,
which means that there's some regexp stuff going on.

```Clojure
(-> files first .getName
    (str/split #"_")
    first)

  ;; => "sep"
```

## Transformations

Let's combine everyting to a single function that we can map to all the files.
And let's use some temporary variables that will be available only inside
the function. 
For that we need to use `let` function which expects a *vector* in a form of
`(let [var-name (foo bar)])`.

Same way, we can even define a function inside a function.
You see, `defn` is just a syntactic sugar for 
`def foo (fn [arg1 arg2])`, and `fn` is an *anonymous function*!

By the way, we also need to replace commas, and convert columns to float.


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
            [{:keys [deps_eur]}]
            {:deps_eur (replace-comma deps_eur)}))))
```

There's really a lot going here, and it can be a bit challenging
to comprehend.
I suggest you to
[take a tour](https://scicloj.github.io/tablecloth/#introduction)
into the documentation of tablecloth, and with some practice
you will see that it is not that complex.

Another thing Clojure great in is *parallelism*, because it is a
functional programming language with 
[immutable data structures](https://en.wikipedia.org/wiki/Persistent_data_structure).
Out of the box it has `pmap` to apply a function to e.g. list of files.
Let's use it to process our files into one datasest.

```Clojure
(def transformed-files (pmap transform-file! files)) 

(def ds (apply tc/concat transformed-files))
   
(info ds)
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
;;  |       :month |   :string |  3410291 |          0 |
```

We can see now another problem -- two of important columns have type `:object`.
That means there's something unreadable there, 
and without proper `:agent_id` we cannot perform an analysis.

For now it is better to filter them out.

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
;;  |       :month |   :string |  3410291 |          0 |
```

That's better, let's re-define our function to take care of such rows when
reading files.

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
            [{:keys [deps_eur]}]
            {:deps_eur (replace-comma deps_eur)})))))
```

## Initializing Duckdb

Working with Duckdb is quite straightforwand:
we need to initialize *in-memory* database, connect to it,
create a table, and insert our data.

Duckdb for Clojure can create table schema from the dataset, so we'll supply it.

```Clojure
(duckdb/initialize!)
;; => true
    
(def db (duckdb/open-db))
;; => #'data-processing.transform-to-parquet/db
  
  
(def conn (duckdb/connect db))
;; => #'data-processing.transform-to-parquet/conn
  
  
(duckdb/create-table! conn ds)
;; => "ds"
```

Now let's test the inserting.

```Clojure
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
          "SELECT * FROM ds"))

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

Seems everything is in the right place. 
Let's truncate the table, and insert all the data we got, 
and save it as a parquet.

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
(transform-directory! "data/new_data")
```


```bash
du -h ./transformed.parquet

=> 89M     ./transformed.parquet
```

Wow, not bad at all, if you compare it to more than a half a GB we had in
the beginning!
Most modern OLAP databases used for data analytics do support parquets,
as well as the most analytical tool, including Pandas.
But even if you only have a laptop with 8 GB of RAM, 
you should be fine if you use efficient tools with lazy calculations
like Polars' LazyFrame or Tablecloth.

## Afterwords

I know a thnig or two about data-processing, 
but Clojure is a new domain for me, 
although Java is not a stranger in data science.
There is a lot of work going on in the field done by **Scicloj**, 
**Technasent**, and many others to make it possible to do 
data science in Clojure.

With Tablecloth for tabular data,
[Hanami](https://github.com/jsa-aerial/hanami) for data visualization,
[scicloj.ml](https://github.com/scicloj/scicloj.ml) for machine learning,
[Clerk](https://github.com/nextjournal/clerk), and 
[Clay](https://github.com/scicloj/clay) for notebookish output,
we can now do full cycle of data analysis in Clojure,
And when you miss something, there's always great 
[Python](https://github.com/clj-python/libpython-clj) and 
[R](https://github.com/scicloj/clojisr) interops, 
not to mention Java that you can call out of the box.

All that with Clojure's design, persistent data, and pragmatic approach
makes it shine, and I hope to see more and more data science projects
done in Clojure.

One problem is that there is a superstition that Clojure is hard to learn
and it is good only for *"tired seniors"*, which is not true --
it often does actually make much more sense because it does not have
tons of objects, but just functions and limited amount of data structures.
But sadly there's not much hype around it, and there's not many
materials about how to do some basic stuff, 
and I hope this will improve in the future.

## Final script

```Clojure
(ns data-processing.transform-to-parquet
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
   "Количество депозитов" :deps_count})

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
            [{:keys [deps_eur]}]
            {:deps_eur (replace-comma deps_eur)})))))

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

(transform-directory! "data/new_data")
```
