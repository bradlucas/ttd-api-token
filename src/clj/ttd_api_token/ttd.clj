(ns ttd-api-token.ttd
  (:require [clojure.edn :as edn]
            [environ.core :refer [env]]))



;; ----------------------------------------------------------------------------------------------------
;; #!/bin/bash
;; export TTD_API_ROOT_URL="https://api.thetradedesk.com/v3/"
;; export TTD_API_TOKEN="ENTER YOUR TOKEN"
;;
(defn load-env-config []
  {:root-url (env :ttd-api-token-root-url)
   :username (env :ttd-api-token-username)
   :password (env :ttd-api-token-password)})


;; ----------------------------------------------------------------------------------------------------
;; Create a edn file called config.edn inside a directory called .ttd-api in your $HOME directory
;;
;; {:root-url "https://api.thetradedesk.com/v3/"
;;  :token "<token you created with the authentication method"
;;  }
;;
;; @see https://api.thetradedesk.com/v3/doc/api/post-authentication
;;
;; You can manually make a call using an app such as Postman (https://www.getpostman.com/)

(defn load-config []
  (let [filename (str (System/getProperty "user.home") "/.ttd-api-token/config.edn")]
    (if (.exists (clojure.java.io/as-file filename))
      (edn/read-string (slurp filename))
      (load-env-config))))

(defn root-url []
  (:root-url (load-config)))

(defn username []
  (:username (load-config)))

(defn password []
  (:password (load-config)))


(defn debug []
  (println (root-url))
  (println (username))
  (println (password)))
