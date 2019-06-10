(ns ttd-api-token.routes.home
  (:require
    [ttd-api-token.layout :as layout]
    [clojure.java.io :as io]
    [ttd-api-token.middleware :as middleware]
    [ring.util.http-response :as response]
    [ttd-api-token.ttd :as ttd]))

(defn home-page [request]
  (layout/render request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page [request]
  (layout/render request "about.html"))


(defn form-page [request]
  (layout/render request "form.html"))

(defn create-token [password delay]
  (if (not (ttd/valid-password password))
    {:error "Invalid password"}
    (if (not (ttd/valid-delay delay))
      {:error "Invalid delay value. Must be non-negative integer"}
      {:token (ttd/create-token)})))

(defn create-page [{:keys [params]:as request}]
  (println "--------------------------------------------------")
  (let [password (:password params)
        delay (:delay params)]
    (clojure.pprint/pprint params)
    (let [m (create-token password (if (not-empty delay) (read-string delay) delay))
          m2 (merge params)]
      (clojure.pprint/pprint m2)
      (layout/render request "result.html" (merge m {:params params
                                                     :debug-str (with-out-str (clojure.pprint/pprint m2))})))))
  
(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ;; ["/" {:get home-page}]

   ["/" {:get form-page
         :post create-page}]
       
   ["/about" {:get about-page}]])

