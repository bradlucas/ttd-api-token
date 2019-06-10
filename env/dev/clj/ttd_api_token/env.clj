(ns ttd-api-token.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [ttd-api-token.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[ttd-api-token started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[ttd-api-token has shut down successfully]=-"))
   :middleware wrap-dev})
