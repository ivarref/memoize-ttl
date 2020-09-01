(ns ivarref.memoize-ttl.impl)
; private API, subject to change

(defn find-args
  [now-millis mem args]
  (if-let [e (find mem args)]
    (let [{:keys [expires] :as v} (val e)]
      (if (>= now-millis expires)
        nil
        [(:val v)]))
    nil))

(defn memoize-ttl!
  [now-millis f mem args]
  (if-let [v (find-args now-millis @mem args)]
    (first v)
    (let [m (apply f args)]
      (assert (and (map? m)
                   (find m :val)
                   (number? (:ttl m))
                   (pos? (:ttl m)))
              "f must return a map containing the keys :val and :ttl (seconds)")
      (swap! mem assoc args {:val     (:val m)
                             :expires (+ (* 1000 (:ttl m)) now-millis)})
      (:val m))))