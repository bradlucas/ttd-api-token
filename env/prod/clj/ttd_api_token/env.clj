(ns ttd-api-token.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[ttd-api-token started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[ttd-api-token has shut down successfully]=-"))
   :middleware identity})
