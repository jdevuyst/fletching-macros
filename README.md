# Fletching macros

A collection of Clojure macros that complement the `clojure.core` threading macros `->` and `->>`.

## Setup

To start, create a [Leiningen](http://leiningen.org) project and add the following dependency to `project.clj`:

![Clojars Project](https://clojars.org/fletching-macros/latest-version.svg)

Next, load the macros as follows:

```clojure
(require '[fletching.macros :refer :all])
```

## `>>`

> The macro `>>` removes itself from the head of the enclosing S-expression, and then moves the second form in the remaining expression to the end of the expression.

Intended to be used within `->` to switch to `->>` conventions.

```clojure
(-> [[1 2 3] [3 4 5]]
    (nth 1)
    (->> (map -))
    (nth 1))
;=> -4
```

This can be rewritten as

```clojure
(-> [[1 2 3] [3 4 5]]
    (nth 1)
    (>> map -)
    (nth 1))
;=> -4
```

## `<<`

> The macro `<<` removes itself from the head of the enclosing S-expression, and then moves the last form in the expression to the second position.

This macro addresses the problem of nesting `->` within `->>`.

Working around this composition problem using `clojure.core` macros was already possible, but not always very elegant. You might have been writing code like this:

```clojure
(-> (->> [-1 -2 -3 -4 -5]
         (map -)
         (filter even?))
    last
    range
    rest
    (->> (reduce *)))
;=> 6
```

Or like this:

```clojure
(as-> [-1 -2 -3 -4 -5] x
      (map - x)
      (filter even? x)
      (-> x last range rest)
      (reduce * x))
;=> 6
```

Now you can write this:

```clojure
(->> [-1 -2 -3 -4 -5]
     (map -)
     (filter even?)
     (<< -> last range rest)
     (reduce *))
;=> 6
```

Of course, `<<` is also useful in one-off situations:

```clojure
(->> [1 2 3 4 5]
     (map -)
     (<< nth 2)
     (/ 6))
;=> -2
```

## `?>`, `<?`

> The macros `?>` and `<?` remove themselves from the head of the enclosing S-expression, and then remove the second (`?>`) or last (`<?`) form in the remaining expression and bind it to `?`.

These macros are useful when you're composing functions using `->` or `->>` and you find yourself needing a little bit more flexibility for some of the threaded expressions.

```clojure
(->> [1 2 3 4 5]
     (take 3)
     (reduce *)
     (<? str ? " is " (if (even? ?) "even" "odd"))
     (<? do [? ?]))
;=> ["6 is even" "6 is even"]
```

## `><`

> The macro `><` removes itself from the head of the enclosing S-expression.

This macro is useful when you want to thread functions (as opposed to function arguments):

```clojure
(-> [+ - * /]
    (nth 2)
    (>< 3 4)
    inc)
;=> 13
```

Equivalently,

```clojure
(->> [+ - * /]
     (drop 2)
     first
     (<< >< 3 4)
     inc)
;=> 13
```

## `<fn`, `<&fn`

> The macros `<fn` and `<&fn` remove themselves from the head of the enclosing S-expression, and then insert a variable in the second position of the remaining expression. Finally, the expression is transformed into a 1-ary (`<fn`) or variadic (`<&fn`) lambda, which binds the variable.

`<fn` and `<&fn` can be used for defining functions in a [point-free style](https://en.wikipedia.org/wiki/Point-free_programming):

```clojure
(def f (<fn ->> (map -)
                (filter even?)
                (reduce *)))
(f [2 4 5 6])
;=> -48
```

`<&fn` is like `<fn` but takes a variable number of arguments, which it threads into the expression as a sequence.

```clojure
(def g (<&fn ->> (map -)
                 (filter even?)
                 (reduce *)))
(g 2 4 5 6)
;=> -48
```

## License

Copyright © 2015 Jonas De Vuyst

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.