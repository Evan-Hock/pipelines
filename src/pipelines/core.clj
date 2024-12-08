(ns pipelines.core)

(defn- thread-nth [n x form]
  (let [[prefix suffix] (split-at n form)]
    `(~@prefix ~x ~@suffix)))

(defn- thread-last [x form]
  `(~(first form) ~@(next form) ~x))

(defn- underscore? [symbol]
  (= '_ symbol))

(defmacro |>
  "Pipeline operator." 
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
                  (case threading-mode
                    :first (thread-nth 1 x form)
                    :last (thread-last x form)
                    (do
                      (assert (and (integer? threading-mode) (>= threading-mode 1)) "Threading mode must be an integer and >= 1.")
                      (thread-nth threading-mode x form)))))

              (seq? form)
              (if (boolean (some underscore? form))
                (map #(if (underscore? %) x %) form)
                (thread-last x form))

              :else (list form x))]
        (recur threaded (next forms))))))