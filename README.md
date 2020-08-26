# memoize-ttl

A tiny library that lets you cache function return values for a dynamic number of seconds.

## Install

TODO.

## Requirements

The JVM.

## Example

```clojure
(require '[memoize-ttl.memoize-ttl :as ttl])

;; First we define a function that presumably have some expensive operation that may change over time.
(defn myfunc []
  (println "doing some work")
  {:val (rand-int 100) ; what to return is given in :val
   :ttl 3})            ; and how many seconds it should be cached

;; Create a cached version of the function
(def cached-myfunc (ttl/memoize-ttl myfunc))

;; The first time it is invoked, the function is called
(cached-myfunc)
doing some work
=> 77

; Execute it again before 3 seconds has elapsed and the old return value will be used
(cached-myfunc) 
=> 77

; Wait three seconds or more and the function will be invoked again
(cached-myfunc)
doing some work
=> 95
```

## TODO

* Support ClojureScript? Pull requests welcome.

## License
   
Copyright © 2020 Ivar Refsdal
   
Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.