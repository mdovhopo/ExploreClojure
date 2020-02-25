(defproject explore_clojure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ant.design "6.2.0"]
                 [ring-server "0.5.0"]
                 [reagent "0.9.0-rc3"]
                 [reagent-utils "0.3.3"]
                 [ring "1.8.0"]
                 [ring/ring-defaults "0.3.2"]
                 [hiccup "1.0.5"]
                 [yogthos/config "1.1.7"]
                 [org.clojure/clojurescript "1.10.597"
                  :scope "provided"]
                 [metosin/reitit "0.3.10"]
                 [pez/clerk "1.0.0"]
                 [venantius/accountant "0.2.5"
                  :exclusions [org.clojure/tools.reader]]]

  :plugins [[lein-environ "1.1.0"]
            [lein-cljsbuild "1.1.7"]
            [lein-asset-minifier "0.4.6"
             :exclusions [org.clojure/clojure]]]

  :ring {:handler      explore-clojure.handler/app
         :uberwar-name "explore-clojure.war"}

  :min-lein-version "2.5.0"
  :uberjar-name "explore-clojure.jar"
  :main explore-clojure.server
  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]

  :source-paths ["src/clj" "src/cljc" "src/cljs"]
  :resource-paths ["resources" "target/cljsbuild"]

  :minify-assets
  [[:css {:source "resources/public/css/site.css"
          :target "resources/public/css/site.min.css"}]]

  :cljsbuild
  {:builds {:min
            {:source-paths ["src/cljs" "src/cljc" "env/prod/cljs"]
             :compiler
                           {:output-to     "target/cljsbuild/public/js/app.js"
                            :output-dir    "target/cljsbuild/public/js"
                            :source-map    "target/cljsbuild/public/js/app.js.map"
                            :optimizations :advanced
                            :infer-externs true
                            :pretty-print  false}}
            :app
            {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
             :figwheel     {:on-jsload "explore-clojure.core/mount-root"}
             :compiler
                           {:main          "explore-clojure.dev"
                            :asset-path    "/js/out"
                            :output-to     "target/cljsbuild/public/js/app.js"
                            :output-dir    "target/cljsbuild/public/js/out"
                            :source-map    true
                            :optimizations :none
                            :pretty-print  true}}

            }
   }

  :figwheel
  {:http-server-root "public"
   :server-port      3449
   :nrepl-port       7002
   :nrepl-middleware [cider.piggieback/wrap-cljs-repl
                      ]
   :css-dirs         ["resources/public/css"]
   :ring-handler     explore-clojure.handler/app}



  :profiles {:dev     {:repl-options {:init-ns explore-clojure.repl}
                       :dependencies [[cider/piggieback "0.4.2"]
                                      [cljsjs/react-tooltip "3.3.0-0"] ;; latest release
                                      [binaryage/devtools "0.9.11"]
                                      [ring/ring-mock "0.4.0"]
                                      [ring/ring-devel "1.8.0"]
                                      [prone "2019-07-08"]
                                      [figwheel-sidecar "0.5.19"]
                                      [nrepl "0.6.0"]
                                      [pjstadig/humane-test-output "0.10.0"]

                                      ]

                       :source-paths ["env/dev/clj"]
                       :plugins      [[lein-figwheel "0.5.19"]
                                      ]

                       :injections   [(require 'pjstadig.humane-test-output)
                                      (pjstadig.humane-test-output/activate!)]

                       :env          {:dev true}}

             :uberjar {:hooks        [minify-assets.plugin/hooks]
                       :source-paths ["env/prod/clj"]
                       :prep-tasks   ["compile" ["cljsbuild" "once" "min"]]
                       :env          {:production true}
                       :aot          :all
                       :omit-source  true}})
