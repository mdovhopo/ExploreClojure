(ns utils.core)

(defn ten [val]
  (if (< val 10) (str "0" val) val) )

(defn seconds-to-time-view [seconds]
  (str (ten (mod (int (/ second 3600)) 216000)) ":"
       (ten (mod (int (/ seconds 60)) 3600)) ":"
       (ten (mod seconds 60))))
