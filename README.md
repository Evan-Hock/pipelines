# pipelines

Adds pipelines to Clojure.

## Usage

### Importing

In Lein, under your `defproject` declaration in `project.clj`:

```clj
:dependencies [[pipelines.core "0.1.0"]]
```

In your `ns` declaration:

```clj
(:use pipelines.core)
```

The module only exports the `|>` symbol.


### What it does?

This thing adds a macro for writing pipelines à la Elixir, although with more flexibility than
either Elixir's pipelines or Clojure's threading macros.

For basic uses, it behaves the same as Clojure's "thread last" macro, e.g.

```clj
(|> s
    clojure.string/split-lines
    (map #(Integer/parseInt %))
    (reduce #(+ (* 2 %1) %2) 0))
```

and

```clj
(->> s
    clojure.string/split-lines
    (map #(Integer/parseInt %))
    (reduce #(+ (* 2 %1) %2) 0))
```

mean the same thing: the previous form is threaded as the last argument in the following form.

The `|>` pipeline operator adds two new features for specifying where arguments go in pipelines.

The first is underscore substitution. In the pipeline, you may write any number of underscores,
and the preceding form will replace each underscore.

```clj
(|> 7
    (* _ _ _)
    (quot _ 7))
```

will evaluate to 49.


The second is vector specification. If a form is a vector instead of a list, it is expected to
be a two-element vector, and the first element will determine where in the form (that is the second element) the argument
will be substituted.

```clj
(|> s
    [:first (clojure.string/split #"\s+")]
    (map #(Integer/parseInt %)))
```

The possible values for the first element are `:first`, `:last`, or an integer greater than 0 (if the integer is greater
than or equal to the number of elements in the form, it will be treated the same as `:last`).

This feature does come with an unfortunate consequence: you can no longer pipe into vector literals. If you
wish to do this, you can just surround your vector literal in parentheses (or put it in a vector specifier, &c) and it
will work as you probably intended.