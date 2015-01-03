(ns fletching.macros)

(defmacro >> [& forms]
  "Removes itself from the head of the enclosing S-expression, and then moves
  the second form in the remaining expression to the end of the expression."
  `(~@(next forms) ~@(take 1 forms)))

(defmacro << [& forms]
  "Removes itself from the head of the enclosing S-expression, and then moves
  the last form in the expression to the second position."
  (let [[head forms] (split-at 1 forms)]
    `(~@head ~@(take 1 (reverse forms)) ~@(butlast forms))))

(defmacro ?> [& forms]
  "Removes itself from the head of the enclosing S-expression, and then removes
  the second form in the remaining expression and binds it to `?`."
  `(let [~'? ~(first forms)]
     ~(next forms)))

(defmacro <? [& forms]
  "Removes itself from the head of the enclosing S-expression, and then removes
  the last form and binds it to `?`."
  `(let [~'? ~(last forms)]
     ~(butlast forms)))

(defmacro >< [& forms]
  "Removes itself from the head of the enclosing S-expression."
  forms)

(defmacro <fn [& forms]
  "Removes itself from the head of the enclosing S-expression, and then inserts
  a variable in the second position of the remaining expression. Finally, the
  expression is transformed into a lambda, which binds the variable."
  `(fn [x#]
     (~@(take 1 forms) x# ~@(next forms))))

(defmacro <&fn [& forms]
  "Removes itself from the head of the enclosing S-expression, and then inserts
  a variable in the second position of the remaining expression. Finally, the
  expression is transformed into a lambda, which binds the variable."
  `(fn [& xs#]
     (~@(take 1 forms) xs# ~@(next forms))))