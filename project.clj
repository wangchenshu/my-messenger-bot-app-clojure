(defproject my-messenger-bot-app-clojure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [compojure "1.5.0"]
                 [org.clojure/data.json "0.2.6"]
                 [http-kit "2.1.19"]
                 [clj-http "3.1.0"]]
  :main my-messenger-bot-app-clojure.core)
