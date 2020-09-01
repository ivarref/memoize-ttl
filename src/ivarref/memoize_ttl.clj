(ns ivarref.memoize-ttl
  (:require [ivarref.memoize-ttl.impl :as impl]))

(defn now-millis []
  (System/currentTimeMillis))

(defn locking-fn
  "Returns a function that will hold a lock while calling the `f` function."
  [f]
  (let [lock (atom {})]
    (fn [& args]
      (locking lock
        (apply f args)))))

(defn memoize-ttl
  "Returns a function that will cache the `f` function.

   The function `f` must return a map containing two keys:
   - :val the value that this function will cache and return.
   - :ttl number of seconds for the return value to live."
  [f]
  (let [mem (atom {})]
    (fn [& args]
      (impl/memoize-ttl! (now-millis) f mem args))))
