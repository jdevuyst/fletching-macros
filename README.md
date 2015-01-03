# Fletching macros

A collection of Clojure macros that complement the clojure.core threading (arrow) macros `->` and `->>`.

## Setup

To start, create a [Leiningen](http://leiningen.org) project and add the following dependency to `project.clj`:

![Clojars Project](http://clojars.org/fletching-macros/latest-version.svg)

Next, load the macros as follows:

```clojure
(require '[fletching.macros :refer :all])
```

## `>>`

Removes itself from the head of the enclosing S-expression, and then moves the second form in the remaining expression to the end of the expression.

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

Removes itself from the head of the enclosing S-expression, and then moves the last form in the expression to the second position.

It's not possible to nest `->` within `->>`. This macro addresses this problem.

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

## `?>` and `<?`

Removes itself from the head of the enclosing S-expression, and then respectively removes the second (`?>`) or last (`<?`) form in the remaining expression and binds it to `?`.

These are useful when you're composing functions using `->` or `->>` and you find yourself needing a little bit more flexibility for one of the threaded expressions.

```clojure
(->> [1 2 3 4 5]
     (take 3)
     (reduce *)
     (&lt;? str ? " is " (if (even? ?) "even" "odd"))
     (&lt;? do [? ?]))
;=> ["6 is even" "6 is even"]
```

## `><`

Removes itself from the head of the enclosing S-expression.

This macro is useful when you want to thread functions (as opposed to function arguments):

```clojure
(-> [+ - * /]
    (nth 2)
    (>< 3 4))
;=> 12
```

Similarly,

```clojure
(->> [+ - * /]
     (drop 2)
     first
     (<< >< 3 4))
;=> 12
```

## `<fn` and `<&fn`

`<fn` removes itself from the head of the enclosing S-expression, and then inserts a variable in the second position of the remaining expression. Finally, the expression is transformed into a lambda, which binds the variable.

More simply, this macro helps you transform `->`, `->>`, `as->`, and other threading macros into functions.

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