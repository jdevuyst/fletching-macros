(defproject fletching-macros "0.9"
  :description "Clojure macros that complement the clojure.core threading (arrow) macros"
  :url "https://github.com/jdevuyst/fletching"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :profiles {:dev {:injections [(require '[fletching.macros :refer :all])]}})