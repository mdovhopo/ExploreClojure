(ns explore-clojure.prod
  (:require [explore-clojure.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
