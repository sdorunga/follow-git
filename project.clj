(defproject follow-git "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.7.0"]
                 [tentacles "0.3.0"]]
  :main ^:skip-aot follow-git.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
