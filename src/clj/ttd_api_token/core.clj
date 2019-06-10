(ns ttd-api-token.core
  (:require
    [ttd-api-token.handler :as handler]
    [ttd-api-token.nrepl :as nrepl]
    [luminus.http-server :as http]
    [ttd-api-token.config :refer [env]]
    [clojure.tools.cli :refer [parse-opts]]
    [clojure.tools.logging :as log]
    [mount.core :as mount]
    [ttd-api-token.ttd :as ttd])
  (:gen-class))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)]])

(mount/defstate ^{:on-reload :noop} http-server
  :start
  (http/start
    (-> env
        (assoc  :handler #'handler/app)
        (update :io-threads #(or % (* 2 (.availableProcessors (Runtime/getRuntime)))))
        (update :port #(or (-> env :options :port) %))))
  :stop
  (http/stop http-server))

(mount/defstate ^{:on-reload :noop} repl-server
  :start
  (when (env :nrepl-port)
    (nrepl/start {:bind (env :nrepl-bind)
                  :port (env :nrepl-port)}))
  :stop
  (when repl-server
    (nrepl/stop repl-server)))


(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (doseq [component (-> args
                        (parse-opts cli-options)
                        mount/start-with-args
                        :started)]
    (log/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))


(defn start-web [args]
  (start-app args))

(defn cmdline [args]
  (if (= 2 (count args))
    (let [delay (read-string (second args))]
      (if (ttd/valid-delay delay)
        (println (ttd/create-token))
        (println "Invalid delay value. Must be a positive integer")))
    (println "Missing required value for delay")))


(defn usage []
  (println "Usage:\n
-create <delay> : Create token with a delay value
-webapp         : Start web version
\n"))

(defn -main [& args]
  (if args
    (let [flag (first args)]
      ;; if --webapp then start-app
      (if (or (= "--webapp" flag) (= "-w" flag))
        (start-web args)
        ;; else support command line
        (if (or (= "--create" flag) (= "-c" flag))
          (cmdline args)
          (usage))))
    (usage)))


