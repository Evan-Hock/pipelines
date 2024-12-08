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

The `|>` pipeline operator adds two new features for writing pipelines.