(ns explore-clojure.core
  (:require
    [utils.core :as utils]
    [reagent.core :as r]))
;
(def ^:private payment-form-state (r/atom {
                                           :is-payment-expired false
                                           :is-payment-done    false
                                           :remember-card      {:value false}
                                           :card-number        {:value "" :is-valid nil}
                                           :expires-in         {:value "" :is-valid nil}
                                           :cardholder-name    {:value "" :is-valid nil}
                                           :cvv                {:value "" :is-valid nil}
                                           }))

(defn reset-payment-form []
  #(reset! payment-form-state {
                               :is-payment-expired false
                               :is-payment-done    true
                               :remember-card      {:value false}
                               :card-number        {:value "" :is-valid nil}
                               :expires-in         {:value "" :is-valid nil}
                               :cardholder-name    {:value "" :is-valid nil}
                               :cvv                {:value "" :is-valid nil}
                               })
  )

;
;(defn count-down
;
;  [seconds on-done]
;  (r/create-class {
;                   :component-did-mount (fn []
;                                          ;(reset-counter seconds)
;                                          ;(fn [] (swap! interval-id (.setInterval js/window
;                                          ;                                        (fn []
;                                          ;                                          (println "Count " @count-down-state)
;                                          ;                                          (fn [] (swap! count-down assoc dec))
;                                          ;                                          (if (= @count-down-state 0) (.clearInterval js/window @interval-id)))
;                                          ;                                        1000)))
;                                          (println "Hi"))
;                   ;(fn []
;                   ;  #(reset! count-down seconds)
;                   ;  #(reset! interval-id (.setInterval js/window
;                   ;                                     (fn []
;                   ;                                       (println "Count " @count-down-state)
;                   ;                                       (swap! count-down assoc dec)
;                   ;                                       (if (= @count-down-state 0) (.clearInterval js/window @interval-id))
;                   ;                                       )
;                   ;                                     1000))
;                   ;  )
;                   :reagent-render      (fn [] [:span (utils/seconds-to-time-view @count-down-state)])
;                   }))

;; TODO replace hardcoded colors with values from style theme.

(defn tooltip
  [tip]
  (let [is-open (r/atom false)]
    (fn []
      [:span
       [:i.fas.fa-question-circle.k-cursor-pointer {
                                                    :style          {:color "#698793" :font-size "0.8rem"}
                                                    :on-mouse-over  #(swap! is-open not)
                                                    :on-mouse-leave #(swap! is-open not)
                                                    }]
       (if @is-open [:div.k-shadow-sm.k-tooltip-popup tip])])))

(defn form-header
  [price]                                                   ;
  [:div.k-background-dark.d-flex.justify-content-between.px-2.pb-2.pt-1.k-pay-form-header
   [:div.d-flex.justify-content-center.flex-column
    [:img {:src "https://kasta.ua/static/img/svg/header/mk-logo.svg"}]]
   [:div.d-flex.flex-column
    [:span.k-text-light-dark.text-right {:style {:font-size "0.75rem"}} "До оплати"]
    [:span.k-text-light {:style {:font-size "1.5rem"}} price " грн"]]])

;
(defn pay-card-header []
  [:div.d-flex.justify-content-between.px-3.py-2
   [:div.d-flex
    [:i.fas.fa-dot-circle.mt-2.mr-1 {:style {:color "#4266ff" :font-size "0.8rem"}}]
    [:div.d-flex.flex-column
     [:div {:style {:color "#3285ff" :font-size "1rem"}} "Нова картка"]
     [:div {:style {:color "#7d8aa2" :font-size "0.8rem"}} "Visa, Mastercard"]]]
   [:div [:i.fas.fa-credit-card {:style {:color "#698793"}}]]])


(defn validate-card-number [value]
  (if (nil? (re-matches #"[0-9]{16}" value)) false true))

(defn on-card-number-change [el]
  (swap! payment-form-state assoc
         :card-number {
                       :value    (-> el .-target .-value)
                       :is-valid (validate-card-number (-> el .-target .-value))
                       }))

(defn validate-expiration-date [value]
  (if (nil? (re-matches #"[0-9]{2}/[0-9]{2}" value)) false true))

(defn on-expiration-date-change [el]
  (swap! payment-form-state assoc
         :expires-in {
                      :value    (-> el .-target .-value)
                      :is-valid (validate-expiration-date (-> el .-target .-value))
                      }))
(defn validate-cardholder-name [value]
  (if (nil? (re-matches #"(?i)[^0-9]{3,40}" value)) false true))


(defn on-cardholder-name-change [el]
  (swap! payment-form-state assoc
         :cardholder-name {
                           :value    (-> el .-target .-value)
                           :is-valid (validate-cardholder-name (-> el .-target .-value))
                           }))
(defn validate-cvv [value]
  (if (nil? (re-matches #"[0-9]{3,4}" value)) false true))

(defn on-cvv-change [el]
  (swap! payment-form-state assoc
         :cvv {
               :value    (-> el .-target .-value)
               :is-valid (validate-cvv (-> el .-target .-value))
               }))

(defn get-input-class [val]
  (str "form-control " (if (nil? val) "" (if val "is-valid" "is-invalid"))))

(defn pay-card-body []
  [:div.k-dashed-border-top.k-dashed-border-bottom.px-3.py-2
   [:div.d-flex.justify-content-between.mt-2
    [:div.col-8.col-sm-9
     [:label.mr-2 {:style {:color "#284368"}} "Номер карти"]
     [:input {:placeholder  "#### #### #### ####"
              :class-name   (get-input-class (:is-valid (:card-number @payment-form-state)))
              :type         "text"
              ;:pattern     #"\d*"
              ;:required    true
              :id           "frmCCNum"
              :autoComplete "cc-number"
              :inputmod     "numeric"
              :on-change    on-card-number-change
              :max-length   16
              :value        (:value (:card-number @payment-form-state))
              :name         "cardnumber"}]
     (if (= false (:is-valid (:card-number @payment-form-state)))
       [:span.k-text-sm-error "Невірна картка"])]           ;.is-invalid
    [:div.col-4.col-sm-3.pr-0.pl-0.pl-sm-1
     [:label {:style {:color "#284368"}} "Термін дії"]
     [:input {
              :placeholder  "MM / PP"
              :type         "text"
              :class-name   (get-input-class (:is-valid (:expires-in @payment-form-state)))
              ;:pattern     #"\d*"
              ;:required    true
              :name         "cc-exp"
              :id           "frmCCExp"
              :autoComplete "cc-exp"
              :on-change    on-expiration-date-change
              :max-length   "5"
              :value        (:value (:expires-in @payment-form-state))}]
     (if (= false (:is-valid (:expires-in @payment-form-state)))
       [:span.k-text-sm-error "Невірний термін"])]]
   [:div.d-flex.justify-content-between.mt-4.mb-5
    [:div.col-8.col-sm-9
     [:label.mr-2 {:style {:color "#284368"}} "Власник карти"]
     [tooltip "Прізвище та ім'я людини на яке випущена карта. Для іменних карт - нанесенно на картку"]
     [:input {
              :class-name  (get-input-class (:is-valid (:cardholder-name @payment-form-state)))
              :placeholder "CARDHOLDER NAME"
              :type        "text"
              :name        "ccname"
              :id          "frmNameCC"
              :on-change   on-cardholder-name-change
              :max-length  40
              :value       (:value (:cardholder-name @payment-form-state))}]
     (if (= false (:is-valid (:cardholder-name @payment-form-state)))
       [:span.k-text-sm-error "Введіть ПІБ власника картки"])]
    [:div.col-4.col-sm-3.pr-0.pl-0.pl-sm-1
     [:label.mr-2 {:style {:color "#284368"}} "CVV"]
     [tooltip "CVV код, розташованний на зворотньому боці картки"]
     [:input {
              :class-name  (get-input-class (:is-valid (:cvv @payment-form-state)))
              :placeholder "XXX"
              ;:required    true
              :type        "text"
              ;:pattern     #"\d*"
              :on-change   on-cvv-change
              :max-length  4
              :value       (:value (:cvv @payment-form-state))
              :name        "cvv"
              }]
     (if (= false (:is-valid (:cvv @payment-form-state)))
       [:span.k-text-sm-error "Невірний CVV код"])]]])

(defn pay-card-footer [price]
  [:div.d-flex.flex-column.justify-content-center.align-items-center
   [:div.mt-3.w-100.d-flex.justify-content-center
    [:input.mr-1.custom-control.custom-checkbox {
                                                 :type  "checkbox"
                                                 :value (:value (:remember-card @payment-form-state))
                                                 }]
    [:div {:style {:font-size "0.9rem"}} "Запам'ятати цю картку?"]]
   [:button.button-pink.px-5.mt-2 {:type "submit" :disabled (:is-payment-expired @payment-form-state)} "Оплатити " price " грн"]
   [:div.pt-1.pb-5 {:style {:font-size "0.8rem" :color "#7d8aa2" :opacity 0.5}} "на виконання платежу 15:00"
    ;[count-down 10 (fn [] (swap! payment-form-state assoc :is-payment-expired true))]
    ]])
;
(defn form-body
  [price on-submit]
  ;(println @payment-form-state)
  [:div
   [:form.k-shadow-sm.rounded.m-2.border {:on-submit (fn [e]
                                                       (.preventDefault e)
                                                       (if (and
                                                             (:is-valid (:card-number @payment-form-state))
                                                             (:is-valid (:cardholder-name @payment-form-state))
                                                             (:is-valid (:expires-in @payment-form-state))
                                                             (:is-valid (:cvv @payment-form-state)))
                                                         (
                                                          (swap! payment-form-state assoc :is-payment-done true)
                                                          (reset-payment-form)
                                                          )
                                                         ;(on-submit {
                                                         ;            :card-number     (:value (:card-number @payment-form-state))
                                                         ;            :expires-in      (:value (:expires-in @payment-form-state))
                                                         ;            :cardholder-name (:value (:cardholder-name @payment-form-state))
                                                         ;            :cvv             (:value (:cvv @payment-form-state))
                                                         ;            :remember-card   (:value (:remember-card @payment-form-state))
                                                         ;            })
                                                         ))}

    (if (= (:is-payment-done @payment-form-state) true)
      [:div.mt-3.d-flex.justify-content-center
       [:button.button-pink.my-5 "Операція пройшла успішно!"]
       ]
      [:div
       (pay-card-header)
       [pay-card-body]
       [pay-card-footer price]]
      )
    ]
   [:div.d-flex.justify-content-around.mt-5.align-items-center.py-2 {:style {:background-color "#fafafa"}}
    [:img {:src "/images/pci-dss-compliant.png" :width "90px"}]
    [:img {:src "https://kasta.ua/static/img/svg/mastercard-secure.svg" :width "60px" :height "25px"}]
    [:img {:src "https://kasta.ua/static/img/svg/visa-secure.svg" :width "80px"}]
    ]])

(defn form-footer []
  [:div.k-background-dark.d-flex.justify-content-center.k-pay-form-footer
   [:img.my-3 {:src "/images/kasta-pay.png" :width "100px"}]])

(defn payment-form
  "Form for capturing client payment info"
  [price on-submit]
  [:div.full-screen-overlay
   [:div {:class-name "k-pay-form-root"}
    (form-header price)
    [form-body price on-submit]
    (form-footer)]]
  )

; -------------------------
; Initialize app
(defn mount-root []
  (r/render (payment-form 1 (fn [form] (println "submit" form))) (.getElementById js/document "app")))

(defn init! []
  ;(clerk/initialize!)
  (mount-root))
