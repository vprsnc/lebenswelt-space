:page/title Relief of pain making presentations
:blog-post/description Emacs configuration for R interactive programming with Rmarkdown, ESS, and Polymode
:blog-post/tags [:R :emacs :programming]
:blog-post/author {:person/id :georgy}
:blog-post/date-created 2024-01-11
:blog-post/last-updated 2024-01-11
:open-graph/title Clojure data-processing
:open-graph/description Setting up R interactive programming environment with Rmarkdown, ESS, and Polymode
:page/body

# Relief of pain making presentations

I've always found preparing presentations using tools like
powerpoint frustrating.

You have to insert all those text boxes, adjust them,
pick the right font size, and then you have to pick the font size for
another text box because powerpoint has reset it to default...
There are lots of settings and options that you struggle to get the idea of,
and all that automatically inserted bullet points!

Finally, you need to copy-paste all the graphs you've built with matplotlib
and adjust the size... 

Sometimes that takes longer than the actual analysis you did.
And it definitely takes much more mental health!

Good news is that you can solve all that issues all at once
with **R Markdown** -
a tool specifically developed for reproducible data storytelling.
With it you can run Python or R code in separate cells exposing the output,
embed graphs, and accompany all that with nicely formatted comments,
all that without ever leaving the text editor of your preference!

## Getting started
Firstly, **R markdown** has its own quite extensive
[documentation](https://rmarkdown.rstudio.com/lesson-1.html),
which I suggest you to check.

There are couple of presentation output formats available,
we are going to use is *ioslides*,
and export it to `html`, so that it can be opened on any device
with a web browser (excluding iPhones).
That's right, you don't need any crap like powerpoint to be installed,
it just works!

So, let's first install it.
We will also install couple of other things to get things going. 

```R
install.packages("rmarkdown")
```

We're goot to go now, let's create file called `presentation.Rmd`
and start rolling.

First things first, we need to add a `YAML` header to our file
where all the settings for the out put would be.
You can find all the options in *ioslides*
[documentation](https://bookdown.org/yihui/rmarkdown/ioslides-presentation.html).

```YAML
---
title: "Presentation chaddery"
author: Georgy
date: "`r Sys.Date()`"
output:
  ioslides_presentation:
    logo: logo.png
    widescreen: true
    smaller: true
    css: "./styles.css"
---
```

One thing to note is that out of the box it looks a bit too old-fashioned,
so it is a good idea to add some custom css.
Unfortunately, it is not very well documented, 
so it can be quite challenging.
[This repo](https://github.com/matteocourthoud/ioslides-theme)
contains pretty good defaults that you can use
(don't forget to give it a star).

<div id="info">

*Most folks do use R Studio for that kind of things,
but you can not beat Emacs in ability to highlight the syntax
of R, Python, LaTeX, and Yaml, run code on the fly or by blocks, and
all that in one file!*<br>

*If you'd like to learn more about the setup check out
[this post](/blog-posts/setting-up-emacs-for-data-science/).*

</div>

## Slides

Adding slides is pretty straight forward you either create a
second lecond level header (`##` or `<h2>`),
or add a line separator (`---`) to create a new slide.

First level headers will go on separate slides, 
and all the headers with lower level than the second will not create
a slide but will be rendered in the same one.

Basically you write the regular **markdown**, 
difference is with the code blocks.
With regular markdown syntax you would create them with ````,
and optionally specifying the language right after.

In R markdown you put the into the curly brackets, 
and you can also specify the name of the code block!
(This will come in handy when *rendering* the document).
Here's the examble of such a source block

```R
#  ```{r plotting-the-quantiles}
ggplot( data.frame(list( x = data )), aes(x) )+
  stat_ecdf( geom="step" ) +
  geom_hline( yintercept = .95
            , color = "red"
            , linetype = "dashed"
            )
#  ```
```

By default, all the output will be printed, 
if you want to avoid, simly add `echo=F` to the "header" of the block.
Thus you can add blocks used just for the settings.

E.g. we would like to disable exponential notation for our presentation:

```R
# ```{r not-using-expo-notation, echo=FALSE}
options( scipen = 999 )
# ```
```

## Rendering and troubleshooting 

To render the presentation you can click the button `knit` if you're
using Rstudio or Vscode.
But if you would like to run it within Emacs, 
or e.g. put it in some kind of script to automate the process,
you can use this command:

```bash
Rscript -e "rmarkdown::render('presentation.Rmd')"
```

You can also put it in the Makefile, so that you can render the 
presentation with `M-x compile`.

This will execute all the code blocks, plot all the graphs, 
and write an html file in the same folder you've been working in.

Now, do you remember about *names* of the code block we were talking
about? If you there are any errors, it will be easier for you to
track them, as it will give you the name of the block where it appeared:

```bash
processing file: presentation.Rmd
  |.....                                       |  11% [imports]                
Quitting from lines 19-26 [imports] (presentation.Rmd)
Error in `library()`:
! there is no package called 'duckdb'
Backtrace:
 1. base::library(duckdb)
                                                                                                                    
Execution halted
```

## Output

<div id="info">

*For making this presetotion, I've been using the code
from the post about the weighted average, you can check it
[here](/blog-posts/understanding-weighted-average/).*

</div>
Now, when all issues with code are solved we have our html file, that can be opened in any browser! Press `F11` to go the fullscreen mode, 
and use the arrows to navigate.

You can place a  or even embed it right into your page! (and here's the [link to it](/presentation.html))


<iframe class="not-prose" type="text/html" src="/presentation.html" width="800" height=700>
</iframe>


