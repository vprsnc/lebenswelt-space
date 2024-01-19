:page/title Relief of pain making presentations
:blog-post/description Render enterprise-level device-agnostic presentation  while executing R or Python code, with graphs, nice looking tables, all that at the same time without leaving your code edtor!
:blog-post/tags [:R :emacs :programming]
:blog-post/author {:person/id :georgy}
:blog-post/date-created 2024-01-11
:blog-post/last-updated 2024-01-19
:open-graph/title Clojure data-processing
:open-graph/description Render enterprise-level presentation while executing R or Python code, with graphs, nice looking tables, all that at the same time without leaving your code edtor! (Also mobile friendly).
:page/body

# Relief of pain making presentations

<div align="center">

![Quite typical](/images/powerpoint.jpg)

</div>

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
which I advise you to check.

There are a couple of presentation output formats available,
we are going to use is *ioslides*,
and export it to `html`, so that it can be opened on any device
with a web browser (excluding iPhones).
That's right, you don't need any crap like powerpoint to be installed,
it just works!

<div id="info">

There's also [revealjs](https://revealjs.com/) option to use, 
which is much more fancy and has much more possibilities,
but it takes a bit of time to learn.

</div>


So, let's first install it.

```R
install.packages("rmarkdown")
```

We're goot to go now, let's create a file called `presentation.Rmd`
and start rolling.

First things first, we need to add a `YAML` header to our file
where all the settings for the output would be.
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

One thing to note is that out of the box, it looks a bit too old-fashioned,
so it is a good idea to add some custom css.
Unfortunately, it is not very well documented, 
so it can be quite challenging.
[This repo](https://github.com/matteocourthoud/ioslides-theme)
contains pretty good defaults that you can use
(don't forget to give it a star).

<div id="info">

*Most folks do use R Studio for that kind of things,
but you can not beat Emacs in its ability to highlight the syntax
of R, Python, LaTeX, and Yaml, run code on the fly or by blocks, and
all that in one file!*<br>

*If you'd like to learn more about the setup check out
[this post](/blog-posts/setting-up-emacs-for-data-science/).*

</div>

## Slides

Adding slides is pretty straightforward you either create a
second lecond level header (`##` or `<h2>`),
or add a line separator (`---`) to create a new slide.

First-level headers will go on separate slides, 
and all the headers with a lower level than the second will not create
a slide but will be rendered in the same one.

Basically, you write the regular **markdown**, 
the difference is with the code blocks.
With regular markdown syntax, you would create them with ````,
and optionally specifying the language right after.

In R markdown you put the language into the *curly brackets*, 
and you can also specify the name of the code block!
(This will come in handy when *rendering* the document).
Here's the example of such a source block

```R
#  ```{r the-named-block}
cat("this block has the name!")        
#  ```
```

By default, all the output will be printed, as well as the code blocks content.
if you want to avoid, simply add `echo=F` to the "header" of the block,
this will hide the code block,
and `results="hide"` will hide the output.
You can also put `results="asis"`, and that will print the output
without any "code box", as a regular markdown.
Thus you can add blocks used just for the settings, data loading or whatever.

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

Now, do you remember *names* of the code blocks we were talking
about? If you there are any errors, it will be easier for you to
track them, as it will give you the name of the block where it appeared.

Say, we've added a dependency that is not installed:

```R
#```{R imports}
library(duckdb)
#```
```

When we compile our file we're going to get:

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

So, we know exactly where that happened!

## Output

Here's a very basic example of the presentation:

```markdown
# Presentation chaddery

## How to make presentations with R

Here goes some **markdown**.<br>
<red>Read to the end to see the presentation itself!<red>.

Here's the code output printed as markdown:

#```{r code-example, results='asis'}
cat("### Hello there!")
#```

And the code with no output:

#```{r imports, results='hide'}
library(ggplot2)
#```

*See, no output!*

---

Also python code:
#```{python}
# you would need "reticulate" package for that
def say(x):
    print("I say: " + x)

say("hello")
#```

## Graph with columns

#```{r data-gen, echo=FALSE}
dat <- data.frame(cond = factor(rep(c("A","B"), each=200)), 
                   rating = c(rnorm(200),rnorm(200, mean=.8)))
#```

<div class="columns-2">


<div>
Some list:

- bullet 1
- bullet 2
- bullet 3

</div>

<div>

#```{r graph, echo=FALSE}
ggplot(dat, aes(x=rating)) +
    geom_histogram(binwidth=.5) +
    theme_bw()
#```

</div>

</div>

---

### *hidden code block and table*
#```{r md-table, echo=F}
library(kableExtra)

kable(head(dat), "pipe")

#```
```

When all issues with code are solved we have our html file, that can be opened in any browser! Press `F11` or just `F` to go the fullscreen mode,
`W` to wide-page mode, and `O` to slide-view mode,
use the arrows to navigate.

You can even embed it right into your page! You can open it in the new tab, *right click* the presentation `This Frame -> Open Frame in New Tab`

<iframe align="center" class="not-prose" type="text/html" src="/presentation.html" width="800" height=700>
</iframe>

