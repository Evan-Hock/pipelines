(ns pipelines.core)

(defn- thread-nth [n x form]
  (let [[prefix suffix] (split-at n form)]
    `(~@prefix ~x ~@suffix)))

(defn- thread-last [x form]
  `(~@form ~x))

(defn- underscore? [symbol]
  (= '_ symbol))

(defmacro ^:export |>
  "Pipeline operator.
   
  Functions like the standard \"thread last\" operator, but with some extensions:
  * If a form is a vector, it is expected to have two members, and the first member
    should be either `:first`, `:last`, or a number greater than or equal to 1. This will determine the piping mode,
    that is, which argument into a form the preceding form should be inserted into. If you want to pipe a
    value into a vector, you should put the vector into a list by surrounding it in parentheses.
  * If a form is a list, the preceding form will be inserted as the last element,
    unless that form contains underscores. If that form contains underscores,
    they will be substituted with the preceding form.
  * If a form is not a list, it will be converted into a list, and the preceding form will
    be appended as the last element (this is the same behaviour as the `->>` macro)." 
  {:clj-kondo/ignore [:unresolved-symbol :invalid-arity]}
  [x & forms]
  (loop [x x, forms forms]
    (if (empty? forms)
      x
      (let [form (first forms)
            threaded
            (cond 
              (vector? form)
              (do
                (assert (= (count form) 2) "Form specifications must be vectors of length 2.")
                (let [[threading-mode form] form]
                  (assert
                   (or (= :first threading-mode)
                       (= :last threading-mode)
                       (and (integer? threading-mode)
                            (>= threading-mode 1)))
                   "Threading mode is either :first, :last, or an integer greater than 0.")
                   (if (seq? form)
                     (case threading-mode
                       :first (thread-nth 1 x form)
                       :last (thread-last x form)
                       (thread-nth threading-mode x form))
                     (list form x))))

              (seq? form)
              (if (boolean (some underscore? form))
                (let [g (gensym)]
                  `(let [~g ~x]
                     ~(map #(if (underscore? %) g %) form)))
                (thread-last x form))

              :else (list form x))]
        (recur threaded (next forms))))))