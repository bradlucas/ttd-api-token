(ns ttd-api-token.ttd
  (:require [cheshire.core :as c]
            [clojure.walk :as w]
            [clojure.edn :as edn]
            [clj-http.client :as http]
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

;; (defn username []
;;   (:username (load-config)))

;; (defn password []
;;   (:password (load-config)))

(defn debug []
  (println (load-config)))

(defn valid-password [password]
  (= password (:password (load-config))))

(defn valid-delay [delay]
  (pos-int? delay))

(defn build-url [root-url path]
  (let [s (str root-url path)]
    s))

(defn build-authentication-body [login password token-expiration-in-minutes]
  ;; {
  ;; "Login": "sample string 1",
  ;; "Password": "sample string 2",
  ;; "TokenExpirationInMinutes": 1
  ;; }
  (let [m {:body (str "{\"Login\": \"" login "\"," "\"Password\": \"" password "\"," "\"TokenExpirationInMinutes\": " token-expiration-in-minutes "}")
           :content-type :json
           :accept :json}]
    m))
                                
(defn get-token [root-url username password delay]
  (-> (build-url root-url "authentication")
      (http/post (build-authentication-body username password delay))
      :body
      c/decode
      w/keywordize-keys
      ))

(defn create-token []
  (let [{:keys [root-url username password]} (load-config)]
    (:Token (get-token root-url username password 1))))
  
