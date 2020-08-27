# memoize-ttl

A Clojure library that lets you cache function return values for a dynamic number of seconds.

## Install

TODO.

## Requirements

The JVM.

## Example

```clojure
(require '[memoize-ttl.memoize-ttl :as ttl])

;; First we define a function that presumably have some expensive operation 
;; which result will change over time.
(defn myfunc [a]
  (println "doing some work on" a)
  {:val (+ a (rand-int 10)) ; what to return is given in :val
   :ttl 10})                ; and how many seconds it should be cached

;; Create a cached version of the function
(def cached-myfunc (ttl/memoize-ttl myfunc))

;; The first time it is invoked, the function is called
(cached-myfunc 10)
doing some work on 10
=> 19

; Execute it again before 10 seconds has elapsed and the old return value will be used
(cached-myfunc 10) 
=> 19

; Wait ten seconds or more and the function will be invoked again
(cached-myfunc 10)
doing some work on 10
=> 11

; Caching is done on a per argument basis
(cached-myfunc 20)
doing some work on 20
=> 22
```

## TODO

* Support ClojureScript? Pull requests welcome.

## License
   
Copyright © 2020 Ivar Refsdal
   
Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.