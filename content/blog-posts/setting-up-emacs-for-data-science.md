:page/title Emacs configuration for interactive R
:blog-post/description Emacs configuration for R interactive programming with Rmarkdown, ESS, and Polymode
:blog-post/tags []
:blog-post/author {:person/id :georgy}
:open-graph/title Clojure data-processing
:open-graph/description Setting up R interactive programming environment with Rmarkdown, ESS, and Polymode
:page/body


`M-x complie`


```bash
processing file: presentation.Rmd
  |....................                        |  46% [standard-deviation]
Quitting from lines 151-152 [standard-deviation] (presentation.Rmd)
Error in `parse()`:
! <text>:1:38: unexpected input
1: print(data.sport |> sd() |> round( x=_
                                         ^
Backtrace:
  1. rmarkdown::render("presentation.Rmd")
  2. knitr::knit(knit_input, knit_output, envir = envir, quiet = quiet)
  3. knitr:::process_file(text, output)
  7. knitr:::process_group.block(group)
  8. knitr:::call_block(x)
     ...
 10. knitr:::eng_r(options)
 13. knitr (local) evaluate(...)
 14. evaluate::evaluate(...)
 16. evaluate:::parse_all.character(...)
 17. base::parse(text = x, srcfile = src)
                                                                                        
 Execution halted
 ```
