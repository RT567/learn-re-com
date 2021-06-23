(ns learn-re-com.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [learn-re-com.events :as events]
   [learn-re-com.routes :as routes]
   [learn-re-com.views :as views]
   [learn-re-com.config :as config]
   [re-com.core             :refer [at h-box v-box box gap line scroller border horizontal-tabs horizontal-bar-tabs vertical-bar-tabs horizontal-pill-tabs vertical-pill-tabs label button single-dropdown p]]
   [re-com.tabs             :refer [horizontal-tabs-args-desc bar-tabs-args-desc pill-tabs-args-desc
                                    horizontal-tabs-parts-desc bar-tabs-parts-desc pill-tabs-parts-desc]]
   [re-com.util             :refer [item-for-id]]))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (routes/start!)
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
