(ns learn-re-com.views
  (:require
   [re-frame.core :as re-frame]
   [re-com.core :as re-com :refer [at]]
   [learn-re-com.styles :as styles]
   [learn-re-com.events :as events]
   [learn-re-com.routes :as routes]
   [learn-re-com.subs :as subs]
   [re-com.util :refer [item-for-id]]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]))



;; home

;; connect 4

(defn straight-check
  [player board [x y] [dx dy] n]
  (every? true?
          (for [i (range n)]
            (= (get-in board [(+ (* i dy) y) (+ (* i dx) x)])
               player))))

(def connect4-board
  (vec (repeat 7 [])))

(def state (reagent/atom {:board connect4-board
                          :turn "r"
                          :winner 0}))

(defn win-check4
  [player board n]
  (some true?
        (for [x (range 0 6)
              y (range 0 7)
              dir [[0 1] [1 0] [1 1] [1 -1]]]
          (straight-check player board [x y] dir n))))

(defn ext-con4-handle
  [x]
  (let [{:keys [winner board turn]} @state]
    (when (and (>= 5 (count (get-in @state [:board x]))) (= winner 0))
      (swap! state update-in [:board x] (fn [a] (conj a turn)))
      (swap! state assoc-in [:turn] (if (= turn "r") "y" "r"))
      (prn board)
      (when
       (win-check4 "r" (:board @state) 4)
        (swap! state assoc-in [:winner] "Red")
        (prn "red wins!"))
      (when
       (win-check4 "y" (:board @state) 4)
        (swap! state assoc-in [:winner] "Yellow")))))

(defn connect4
  []
  (fn []
    [re-com/v-box
     :align :center
     :children [[:svg
                 {:view-box "0 0 70 60"
                  :width 700
                  :height 600
                  :style {:background "blue"}
                  :transform "rotate(180)"};
                 (doall (for [i (range 0 7)      ;x
                              j (range 0 6)]     ;y
                          ^{:key [i j]} [:circle {:r 4.1 :cx (+ 5 (* i 10)) :cy (+ 5 (* j 10))
                                                  :fill (case (get-in @state [:board i j])
                                                          nil "lightgrey"
                                                          "r" "red"
                                                          "y" "yellow")
                                                  :on-click #(ext-con4-handle i)}]))]]]))

(defn winner-announce2 []
  (fn []
    [:div
     [:p {:style {:visibility
                  (if (= 0 (:winner @state))
                    "hidden"
                    "visible")}}
      "The winner is " (:winner @state) "!"]]))

;;; end connect ;;;

;;; button stuff ;;;
(def wrestler-name-generator [["Burly"
                               "Surly"
                               "Wide"
                               "Untouchable"
                               "The one and only"
                               "Glorious"
                               "Stone cold"
                               "Loose cannon"
                               "The great"
                               "Hammer fists"]
                              ["Johnson"
                               "Hogan"
                               "McGee"
                               "Man"
                               "Brick"
                               "Jiminy"]])
                               
(defn wrestler-button
  []
  (let [current-name (reagent/atom "")]
    (fn []
      [re-com/v-box
       :height "630px"
       :align :center
       :gap "100px"
       :children [[re-com/button
                   :src (at)
                   :label "Generate your new wreslting name!"
                   :on-click #(reset! current-name (str (rand-nth (first wrestler-name-generator)) " " (rand-nth (second wrestler-name-generator))))
                   :tooltip "Greatness awaits!"
                   :tooltip-position :above-left]
                  [:h1 {:style {:color "orange"}} @current-name]]])))
;;;
(+ 1 2)
;;; tick-boxes ;;;
;;tick-boxes to binary -> decimal
;;
(defn tick-boxes
  []
  (let [box# 12
        state-binary (reagent/atom (vec (repeat box# false)))
        to-dec (fn aa [state] (reduce + (map #(if %1 %2 0) state (take (count state) (iterate (partial * 2) 1)))))]
   (fn []
     [re-com/v-box
      :align :start
      :height "630px"
      :gap "10px"
      :children [(doall (for [x (range box#)]
                    ^{:key [x]} [re-com/checkbox
                                 :model (nth @state-binary x)
                                 :on-change #(swap! state-binary assoc-in [x] %)
                                 :label (str (last (take (inc x) (iterate (partial * 2) 1))))]))
                 [re-com/p (str "In binary: " (reduce #(str (if %2 1 0) %1) "" @state-binary))]
                 [re-com/p (str "In decimal: " (to-dec @state-binary))]]])))
;;;;
;;; date picker ;;;
(defn date-pick
  []
  [re-com/datepicker
   :src (at)
   :style {:font-size 10}
   :on-change #(str %)])

;;;;



(defn home-title []
  (let [name (re-frame/subscribe [::subs/name])]
    [re-com/title
     :src   (at)
     :label (str "Welcome to my website");"Hello from " @name ". This is the Home Page.")
     :level :level1
     :class (styles/level1)]))

(defn link-to-about-page []
  [re-com/hyperlink
   :src      (at)
   :label    "go to About Page"
   :on-click #(re-frame/dispatch [::events/navigate :about])])

;;;; tabs

(def tabs-definition
  [{:id ::tab1  :label "Connect 4"  :say-this [re-com/p [connect4] [winner-announce2]]}
   {:id ::tab2  :label "Wrestler"  :say-this [re-com/v-box :children [[wrestler-button]]]}
   {:id ::tab3  :label "Binary Checkboxes"  :say-this [tick-boxes]}
   {:id "another tab" :label "test tab" :say-this [date-pick]}])

(defn tab-styles-demo
  []
  (let [selected-tab-id (reagent/atom (:id (first tabs-definition)))     ;; holds the id of the selected tab
        change-tab      #(reset! selected-tab-id %)]
    (fn []
      [re-com/v-box :src (at)
       :gap      "30px"
       :align :center
       :children [[re-com/v-box :src (at)
                   :align    :start
                   :children [[re-com/horizontal-pill-tabs :src (at)
                               ;:style {:background-color}
                               :model     selected-tab-id
                               :tabs      tabs-definition
                               :on-change change-tab]]]
                  [re-com/h-box
                   :align :start
                   :children [[re-com/p (:say-this (item-for-id @selected-tab-id tabs-definition))]]]]])))

(def tabs-info ;we have a vector of maps bound to the term "tabs-info"
  [{:id ::first :label "First Tab" :display [re-com/throbber]} ;each map has an id, a label and a display keyand value
   {:id ::second :label "Second Tab" :display "The quick brown fox jumped over the lazy dog."}]) 

(defn tabs-test
  []
  (let [selected-tab-id (reagent/atom (:id (first tabs-info)))  ;the id of the first element in the vector is stored in an atom and bound to selected-tab-id
        change-tab #(reset! selected-tab-id %)]                 ;the change-tab functions resets the value in the atom with the value it recieves
    (fn []                                                      ;render function
      [re-com/v-box
       :align :center
       :children [[re-com/horizontal-pill-tabs                  ;first child of the v-box is the tabs themselves
                   :model selected-tab-id                       ;the model is the unique identifier of the currently selected tab
                   :tabs tabs-info                              ;passing in the vector of all the tabs in a specific form, a vector of maps [{} {} {}]
                   :on-change change-tab]                       ;the function called upon a selection (these first 3 "attributes are required")
                  [re-com/p (:display (item-for-id @selected-tab-id tabs-info))]]]))) ;  :id -> whole item -> :display


;;; header with tabs?
(defn header
  []
  [re-com/h-box
   :src (at)
   :style {:background-color "aqua"}
   :height "60px"
   :width "auto"
   :align :center
   :children [[re-com/gap :size "200px"]
              [re-com/title 
               :src (at)
               :label "Robsite"
               :level :level1]
              [:img {:src "rt.png"
                     :height "50px"
                     :width "50px"} ]]])



(defn home-panel []
  (fn []
    [re-com/v-box
     :children [[header]
                [re-com/v-box
                 :src      (at)
                 :gap      "20px"
                 :size "auto"
                 :align :center
                 :children [[home-title]
                            [link-to-about-page]
                            [tab-styles-demo]
                            [tabs-test]]]]]))

(defmethod routes/panels :home-panel [] [home-panel])

;; about

(defn about-title []
  [re-com/title
   :src   (at)
   :label "This is the About Page."
   :level :level1])

(defn link-to-home-page []
  [re-com/hyperlink
   :src      (at)
   :label    "go to Home Page"
   :on-click #(re-frame/dispatch [::events/navigate :home])])

(defn about-panel []
  [re-com/v-box
   :src      (at)
   :gap      "1em"
   :children [[about-title]
              [link-to-home-page]]])

(defmethod routes/panels :about-panel [] [about-panel])

;; main

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [re-com/v-box
     :src      (at)
     :height   "100%"
     :children [(routes/panels @active-panel)]]))
